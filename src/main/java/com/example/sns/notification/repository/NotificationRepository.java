package com.example.sns.notification.repository;

import com.example.sns.User.entity.User;
import com.example.sns.notification.NotificationType;
import com.example.sns.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,Long> {
    List<Notification> findByReceiverAndIsReadFalseOrderByCreatedAtDesc(User receiver);
    List<Notification> findByReceiverAndIsReadFalse(User user);
    List<Notification> findByReceiverOrderByCreatedAtDesc(User receiver);

    int countByReceiverAndIsReadFalse(User user);

    void deleteByTargetIdAndType(Long targetId, NotificationType type);

}
