package com.example.sns.post.DTO;

import com.example.sns.UserProfiles.DTO.UserProfileDTO;
import com.example.sns.comment.DTO.CommentDTO;
import com.example.sns.comment.entity.Comment;
import com.example.sns.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {
    private Long id;
    private String content;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer likeCount;
    private List<CommentDTO> comments;
    private boolean likedByMe;
    private String nickname;
    private UserProfileDTO user;  // 이 UserProfileDTO를 명확히 사용

    public PostDTO(Post post, String profileImage, String nickname, List<CommentDTO> comments) {
        this.id = post.getId();
        this.content = post.getContent();
        this.imageUrl = post.getImageUrl();
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

    }


    public String getProfileImage() {
        return user != null && user.getProfileImage() != null && !user.getProfileImage().isEmpty()
                ? user.getProfileImage()
                : "/images/default-profile.png";
    }


}
