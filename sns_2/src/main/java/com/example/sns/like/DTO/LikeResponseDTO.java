package com.example.sns.like.DTO;
import com.example.sns.like.LikeType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeResponseDTO {

    private Long targetId;
    private LikeType targetType;
    private int likeCount;
    private boolean likedByMe;
}
