package com.example.sns.like.DTO;

import com.example.sns.like.LikeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LikeRequestDTO {
    private Long targetId;
    private LikeType targetType;
}
