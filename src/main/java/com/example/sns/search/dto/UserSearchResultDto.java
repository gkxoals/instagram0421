package com.example.sns.search.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserSearchResultDto {
    private Long userId;
    private String name;
    private String email;
}