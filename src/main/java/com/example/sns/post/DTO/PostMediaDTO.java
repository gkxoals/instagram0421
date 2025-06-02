package com.example.sns.post.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostMediaDTO {
    private String filePath;
    private String mediaType; // IMAGE or VIDEO
    private int uploadOrder;
}
