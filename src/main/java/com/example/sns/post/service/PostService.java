package com.example.sns.post.service;

import com.example.sns.User.entity.User;
import com.example.sns.User.repository.UserRepository;
import com.example.sns.User.service.UserService;
import com.example.sns.UserProfiles.repository.ProfileRepository;
import com.example.sns.comment.DTO.CommentDTO;
import com.example.sns.comment.entity.Comment;
import com.example.sns.comment.repository.CommentRepository;
import com.example.sns.like.LikeType;
import com.example.sns.like.repository.LikeRepository;
import com.example.sns.notification.NotificationType;
import com.example.sns.notification.repository.NotificationRepository;
import com.example.sns.post.DTO.PostDTO;
import com.example.sns.post.entity.Post;
import com.example.sns.post.entity.PostMedia;
import com.example.sns.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.sns.UserProfiles.entity.Profile;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.io.FilenameUtils.getExtension;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final NotificationRepository notificationRepository;
    private final ProfileRepository profileRepository;

    @Transactional
    public void savePost(Long userId, String content, List<MultipartFile> mediaFiles) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Post post = new Post();
        post.setUser(user);
        post.setContent(content);
        postRepository.save(post); // 먼저 저장해서 ID 확보

        List<PostMedia> mediaList = new ArrayList<>();
        int order = 0;

        for (MultipartFile file : mediaFiles) {
            if (file.isEmpty()) continue;

            String extension = getExtension(file.getOriginalFilename()).toLowerCase();
            String type = extension.matches("mp4|webm") ? "VIDEO" : "IMAGE";

            if (type.equals("VIDEO") && mediaList.stream().anyMatch(m -> m.getMediaType().equals("VIDEO"))) {
                throw new IllegalStateException("영상은 1개만 업로드할 수 있습니다.");
            }

            String savedPath = saveFile(file); // UUID 기반 저장

            PostMedia media = new PostMedia();
            media.setFilePath(savedPath);
            media.setMediaType(type);
            media.setUploadOrder(order++);
            media.setPost(post);
            mediaList.add(media);
        }

        post.setMediaList(mediaList);
        postRepository.save(post);
    }

    @Transactional
    private String saveFile(MultipartFile file) throws IOException {
        //강의실
        String uploadDir = "C:/Users/User/Desktop/sns_2/postImages/";

        // 내 컴퓨터
//        String uploadDir = "C:/Users/하태민/OneDrive/바탕 화면/sns_2/postImages/";
        File uploadPath = new File(uploadDir);
        if (!uploadPath.exists()) uploadPath.mkdirs();

        String extension = file.getOriginalFilename()
                .substring(file.getOriginalFilename().lastIndexOf("."));
        String uniqueName = UUID.randomUUID() + extension;
        String fullPath = uploadDir + uniqueName;

        file.transferTo(new File(fullPath));
        return "/post-images/" + uniqueName;
    }

    @Transactional
    public List<PostDTO> getAllPosts(User currentUser) {
        List<Post> posts = postRepository.findAll();
        List<PostDTO> dtoList = new ArrayList<>();

        for (Post post : posts) {
            String profileImage = userService.getProfileImageByUserId(post.getUser().getUserId());

            // ✅ 게시글 작성자 nickname 조회
            String nickname = profileRepository.findByUserId(post.getUser())
                    .map(Profile::getNickname)
                    .orElse("알 수 없음");

            // ✅ 댓글 DTO 리스트 생성 (댓글 작성자의 nickname도 포함)
            List<CommentDTO> commentDTOs = post.getComments().stream()
                    .map(comment -> {
                        String commentNickname = profileRepository.findByUserId(comment.getUser())
                                .map(Profile::getNickname)
                                .orElse("알 수 없음");
                        return new CommentDTO(comment, commentNickname);
                    })
                    .toList(); // 또는 .collect(Collectors.toList())

            // ✅ 완성된 commentDTOs를 포함해 PostDTO 생성
            PostDTO dto = new PostDTO(post, profileImage, nickname, commentDTOs);

            // 좋아요 수
            int likeCount = likeRepository.countByTargetIdAndTargetType(post.getId(), LikeType.POST);
            dto.setLikeCount(likeCount);

            // 현재 유저가 좋아요 눌렀는지 여부
            boolean likedByMe = likeRepository
                    .findByUserAndTargetIdAndTargetType(currentUser, post.getId(), LikeType.POST)
                    .isPresent();
            dto.setLikedByMe(likedByMe);

            dtoList.add(dto);
        }

        return dtoList;
    }

    @Transactional
    public void updatePost(Long id, String content, List<MultipartFile> mediaFiles) throws IOException {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다."));

        post.setContent(content);
        post.getMediaList().clear(); // 기존 미디어 삭제

        List<PostMedia> newMediaList = new ArrayList<>();
        int order = 0;
        for (MultipartFile file : mediaFiles) {
            if (file.isEmpty()) continue;

            String extension = getExtension(file.getOriginalFilename()).toLowerCase();
            String type = extension.matches("mp4|webm") ? "VIDEO" : "IMAGE";

            String path = saveFile(file);

            PostMedia media = new PostMedia();
            media.setFilePath(path);
            media.setMediaType(type);
            media.setUploadOrder(order++);
            media.setPost(post);
            newMediaList.add(media);
        }

        post.setMediaList(newMediaList);
        postRepository.save(post);
    }


    @Transactional
    public void deletePost(Long postId) {
        // 게시글 삭제 전에 관련 좋아요 알림 삭제
        notificationRepository.deleteByTargetIdAndType(postId, NotificationType.LIKE_POST);
        // 게시글에 달린 댓글 리스트 가져오기
        List<Comment> comments = commentRepository.findByPostId(postId);
        for (Comment comment : comments) {
            notificationRepository.deleteByTargetIdAndType(comment.getId(), NotificationType.COMMENT_POST);
            notificationRepository.deleteByTargetIdAndType(comment.getId(), NotificationType.REPLY);
        }


        // 먼저 대댓글(부모 댓글을 참조하는 댓글) 삭제
        commentRepository.deleteByPostIdAndParentIdIsNotNull(postId);
        // 그 후 부모 댓글 삭제
        commentRepository.deleteByPostIdAndParentIdIsNull(postId);
        // 마지막으로 게시글 삭제
        postRepository.deleteById(postId);
    }

}
