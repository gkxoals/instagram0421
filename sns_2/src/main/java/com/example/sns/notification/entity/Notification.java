package com.example.sns.notification.entity;

import com.example.sns.User.entity.User;
import com.example.sns.notification.NotificationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 알림 받는 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    // 알림 보낸 사용자 (좋아요 누른 사람, 댓글 단 사람 등)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private Long targetId; // 게시글 ID 또는 댓글 ID

    private boolean isRead;

    private LocalDateTime createdAt;
}
