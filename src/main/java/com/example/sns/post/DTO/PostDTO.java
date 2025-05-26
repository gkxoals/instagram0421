package com.example.sns.post.DTO;

import com.example.sns.UserProfiles.DTO.UserProfileDTO;
import com.example.sns.comment.DTO.CommentDTO;
<<<<<<< HEAD
import com.example.sns.comment.entity.Comment;
=======
>>>>>>> 2276687 (초기 커밋)
import com.example.sns.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
<<<<<<< HEAD
import java.util.ArrayList;
=======
>>>>>>> 2276687 (초기 커밋)
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {
    private Long id;
    private String content;
<<<<<<< HEAD
    private String imageUrl;
=======
>>>>>>> 2276687 (초기 커밋)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer likeCount;
    private List<CommentDTO> comments;
    private boolean likedByMe;
    private String nickname;
<<<<<<< HEAD
    private UserProfileDTO user;  // 이 UserProfileDTO를 명확히 사용
=======
    private UserProfileDTO user;
    private List<PostMediaDTO> mediaList;

>>>>>>> 2276687 (초기 커밋)

    public PostDTO(Post post, String profileImage, String nickname, List<CommentDTO> comments) {
        this.id = post.getId();
        this.content = post.getContent();
<<<<<<< HEAD
        this.imageUrl = post.getImageUrl();
=======
>>>>>>> 2276687 (초기 커밋)
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
        this.likeCount = post.getLikeCount();
        this.nickname = nickname;
        this.comments = comments;


        this.user = UserProfileDTO.builder()
                .id(post.getUser().getUserId())
                .nickname(nickname)
                .profileImage(profileImage)
                .build();

<<<<<<< HEAD
=======
        this.mediaList = post.getMediaList().stream()
                .map(media -> new PostMediaDTO(
                        media.getFilePath(),
                        media.getMediaType(),
                        media.getUploadOrder()
                ))
                .collect(Collectors.toList());
>>>>>>> 2276687 (초기 커밋)
    }


    public String getProfileImage() {
        return user != null && user.getProfileImage() != null && !user.getProfileImage().isEmpty()
                ? user.getProfileImage()
                : "/images/default-profile.png";
    }


}
