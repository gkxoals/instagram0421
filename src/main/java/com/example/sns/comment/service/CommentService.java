package com.example.sns.comment.service;
import com.example.sns.UserProfiles.entity.Profile;
import com.example.sns.User.DTO.UserResponseDTO;
import com.example.sns.User.entity.User;
import com.example.sns.User.repository.UserRepository;
import com.example.sns.UserProfiles.repository.ProfileRepository;
import com.example.sns.comment.DTO.CommentDTO;
import com.example.sns.comment.entity.Comment;
import com.example.sns.comment.repository.CommentRepository;
import com.example.sns.notification.NotificationType;
import com.example.sns.notification.repository.NotificationRepository;
import com.example.sns.notification.service.NotificationService;
import com.example.sns.post.entity.Post;
import com.example.sns.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;
    private final ProfileRepository profileRepository;

    @Transactional
    public void saveComment(Long postId, String content, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());

        commentRepository.save(comment);

        // 내가 쓴 게시글에 다른 사람이 댓글 달면 가는 알림
        if (!comment.getUser().equals(post.getUser())) {
            notificationService.notifyCommentPost(
                    comment.getUser(),      // 보낸 사람
                    post.getUser(),         // 게시글 작성자
                    comment                 // 댓글 객체
            );
        }

    }

    @Transactional
    public void createReply(String content, Post post, Comment parent, UserResponseDTO userResponseDTO) {
        User user = userRepository.findById(userResponseDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Comment reply = new Comment();
        reply.setPost(post);
        reply.setParent(parent);
        reply.setUser(user);
        reply.setContent(content);
        reply.setCreatedAt(LocalDateTime.now());
        reply.setUpdatedAt(LocalDateTime.now());

        commentRepository.save(reply);

//  내가 쓴 댓글에 대댓글이 달리면 알림 가게 하기
        if (!user.equals(parent.getUser())) {
            notificationService.notifyReply(
                    user,                   // 대댓글 단 사람
                    parent.getUser(),       // 부모 댓글 단 사람(알림 받을 대상)
                    reply                   // 대댓글 객체
            );
        }


    }

    @Transactional
    public List<CommentDTO> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);

        return comments.stream()
                .map(comment -> {
                    String nickname = profileRepository.findByUserId(comment.getUser())
                            .map(Profile::getNickname)
                            .orElse("알 수 없음");
                    return new CommentDTO(comment, nickname);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteComment(Long commentId, UserResponseDTO currentUser) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        Post post = comment.getPost();

        boolean isCommentOwner = comment.getUser().getUserId().equals(currentUser.getUserId());
        boolean isPostOwner = post.getUser().getUserId().equals(currentUser.getUserId());

        if (!isCommentOwner && !isPostOwner) {
            throw new SecurityException("삭제 권한이 없습니다.");
        }
        // 댓글 삭제 직전에 관련 알림 삭제
        notificationRepository.deleteByTargetIdAndType(commentId, NotificationType.COMMENT_POST);
        notificationRepository.deleteByTargetIdAndType(commentId, NotificationType.REPLY);

        commentRepository.delete(comment);
    }

}
