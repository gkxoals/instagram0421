package com.example.sns.like.service;

import com.example.sns.User.entity.User;
import com.example.sns.User.repository.UserRepository;
import com.example.sns.like.DTO.LikeRequestDTO;
import com.example.sns.like.DTO.LikeResponseDTO;
import com.example.sns.like.entity.Like;
import com.example.sns.like.repository.LikeRepository;
import com.example.sns.notification.NotificationType;
import com.example.sns.notification.service.NotificationService;
import com.example.sns.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.sns.post.entity.Post;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final NotificationService notificationService;


    public LikeResponseDTO toggleLike(Long userId, LikeRequestDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        Post post = postRepository.findById(dto.getTargetId())
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        var existingLike = likeRepository.findByUserAndTargetIdAndTargetType(user, dto.getTargetId(), dto.getTargetType());

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
        } else {
            Like like = Like.builder()
                    .user(user)
                    .targetId(dto.getTargetId())
                    .targetType(dto.getTargetType())
                    .createdAt(LocalDateTime.now())
                    .build();
            likeRepository.save(like);

            if (!user.equals(post.getUser())) {
                notificationService.notifyLikePost(
                        user,               // 좋아요 누른사람
                        post.getUser(),     // 좋아요 대상자
                        post.getId()        // 좋아요 객체
                );
            }
        }

        int count = likeRepository.countByTargetIdAndTargetType(dto.getTargetId(), dto.getTargetType());
        boolean liked = likeRepository.findByUserAndTargetIdAndTargetType(user, dto.getTargetId(), dto.getTargetType()).isPresent();

        return LikeResponseDTO.builder()
                .targetId(dto.getTargetId())
                .targetType(dto.getTargetType())
                .likeCount(count)
                .likedByMe(liked)
                .build();
    }

}
