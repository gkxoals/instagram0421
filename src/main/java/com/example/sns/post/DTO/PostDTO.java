package com.example.sns.post.DTO;

import com.example.sns.UserProfiles.DTO.UserProfileDTO;
import com.example.sns.comment.DTO.CommentDTO;
import com.example.sns.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer likeCount;
    private List<CommentDTO> comments;
    private boolean likedByMe;
    private String nickname;
    private UserProfileDTO user;
    private List<PostMediaDTO> mediaList;
    private String thumbnailUrl;






    public PostDTO(Post post, String profileImage, String nickname, List<CommentDTO> comments, String thumbnailUrl) {
        this.id = post.getId();
        this.content = post.getContent();
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

        this.mediaList = post.getMediaList().stream()
                .map(media -> new PostMediaDTO(
                        media.getFilePath(),
                        media.getMediaType(),
                        media.getUploadOrder()
                ))
                .collect(Collectors.toList());
        this.thumbnailUrl = thumbnailUrl;
    }


    public String getProfileImage() {
        return user != null && user.getProfileImage() != null && !user.getProfileImage().isEmpty()
                ? user.getProfileImage()
                : "/images/default-profile.png";
    }

}
