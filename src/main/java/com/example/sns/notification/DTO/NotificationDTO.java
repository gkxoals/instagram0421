package com.example.sns.notification.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {
    private Long id;
    private String message;
    private boolean read;
    private LocalDateTime createdAt;

    private Long targetId;
    private String targetType;
    private Long postId;
    private String anchor;

}
