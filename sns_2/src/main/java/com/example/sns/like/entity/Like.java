package com.example.sns.like.entity;

import com.example.sns.User.entity.User;
import com.example.sns.like.LikeType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "likes")
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Long targetId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LikeType targetType;

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    }
