package com.example.sns.search.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostSearchResultDto {
    private Long postId;
    private String content;
    private String thumbnailUrl;
    private Long userId;
}
