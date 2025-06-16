package com.example.sns.notification.service;

import com.example.sns.UserProfiles.entity.Profile;
import com.example.sns.User.entity.User;
import com.example.sns.UserProfiles.repository.ProfileRepository;
import com.example.sns.comment.entity.Comment;
import com.example.sns.comment.repository.CommentRepository;
import com.example.sns.notification.DTO.NotificationDTO;
import com.example.sns.notification.NotificationType;
import com.example.sns.notification.entity.Notification;
import com.example.sns.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class NotificationService {
    private final EntityManager entityManager;
    private final NotificationRepository notificationRepository;
    private final CommentRepository commentRepository;
    private final ProfileRepository profileRepository;

    // 알림 생성
    @Transactional
    private void createNotification(User sender, User receiver, NotificationType type, Long targetId) {
        if (sender.equals(receiver)) return; // 자신에게 알림 보내지 않음

        Notification notification = Notification.builder().sender(sender).receiver(receiver).type(type).targetId(targetId).isRead(false).createdAt(LocalDateTime.now()).build();

        notificationRepository.save(notification);
    }

    // 좋아요 알림
    @Transactional
    public void notifyLikePost(User sender, User receiver, Long postId) {
        createNotification(sender, receiver, NotificationType.LIKE_POST, postId);
    }

    // 게시글 댓글 알림
    @Transactional
    public void notifyCommentPost(User sender, User receiver, Comment comment) {
        createNotification(sender, receiver, NotificationType.COMMENT_POST, comment.getId());
    }

    // 대댓글 알림
    @Transactional
    public void notifyReply(User sender, User receiver, Comment replyComment) {
        createNotification(sender, receiver, NotificationType.REPLY, replyComment.getId());
    }


    // 모든 알림 가져오기 (읽음/안 읽음 모두 포함)
    @Transactional
    public List<NotificationDTO> getAllNotifications(User user) {
        return notificationRepository.findByReceiverOrderByCreatedAtDesc(user).stream().map(notification -> {
            String nickname = profileRepository.findByUserId(notification.getSender()).map(Profile::getNickname).orElse("알 수 없음");

            String message = switch (notification.getType()) {
                case LIKE_POST -> nickname + "님이 회원님의 게시물에 좋아요를 눌렀습니다.";
                case COMMENT_POST -> nickname + "님이 회원님의 게시물에 댓글을 남겼습니다.";
                case REPLY -> nickname + "님이 회원님의 댓글에 답글을 남겼습니다.";
                case FOLLOW -> nickname + "님이 회원님을 팔로우했습니다.";
            };
            String anchor = (notification.getType() == NotificationType.COMMENT_POST || notification.getType() == NotificationType.REPLY) ? "#comment-" + notification.getTargetId() : "";

            return new NotificationDTO(notification.getId(), message, notification.isRead(), notification.getCreatedAt(), notification.getTargetId(), getTargetType(notification.getType()), getPostId(notification), anchor);

        }).toList();
    }

    // 읽지 않은 알림만 가져오기
    @Transactional
    public List<NotificationDTO> getUnreadNotifications(User user) {
        return notificationRepository.findByReceiverAndIsReadFalseOrderByCreatedAtDesc(user).stream().map(notification -> {
            String senderName = notification.getSender().getName();

            String message = switch (notification.getType()) {
                case LIKE_POST -> senderName + "님이 회원님의 게시물에 좋아요를 눌렀습니다.";
                case COMMENT_POST -> senderName + "님이 회원님의 게시물에 댓글을 남겼습니다.";
                case REPLY -> senderName + "님이 회원님의 댓글에 답글을 남겼습니다.";
                case FOLLOW -> senderName + "님이 회원님을 팔로우했습니다.";
            };
            String anchor = (notification.getType() == NotificationType.COMMENT_POST || notification.getType() == NotificationType.REPLY) ? "#comment-" + notification.getTargetId() : "";

            return new NotificationDTO(notification.getId(), message, notification.isRead(), notification.getCreatedAt(), notification.getTargetId(), getTargetType(notification.getType()), getPostId(notification), anchor);
        }).toList();
    }

    // 4. 읽지 않은 알림 개수 세기
    @Transactional
    public int countUnread(User user) {
        return notificationRepository.countByReceiverAndIsReadFalse(user);
    }

    // 5. 모든 읽지 않은 알림을 읽음 처리
    @Transactional
    public void markAllAsRead(User user) {
        List<Notification> unread = notificationRepository.findByReceiverAndIsReadFalse(user);
        for (Notification n : unread) {
            n.setRead(true);
        }
        notificationRepository.saveAll(unread);
        entityManager.flush(); // 🔥 DB에 강제 반영
    }


    // 6. 특정 알림 하나만 읽음 처리
    @Transactional
    public void markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다."));
        notification.setRead(true);
    }

    @Transactional(readOnly = true)
    private String getTargetType(NotificationType type) {
        return switch (type) {
            case LIKE_POST, FOLLOW -> "POST";
            case COMMENT_POST -> "COMMENT";
            case REPLY -> "REPLY";
        };
    }

    @Transactional(readOnly = true)
    private Long getPostId(Notification notification) {
        return switch (notification.getType()) {
            case LIKE_POST -> notification.getTargetId();
            case COMMENT_POST, REPLY -> {
                Long commentId = notification.getTargetId();
                Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
                yield comment.getPost().getId();
            }
            case FOLLOW -> null; // 또는 receiver의 프로필 페이지로 이동할 수도
        };
    }

}
