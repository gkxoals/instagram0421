package com.example.sns.notification.controller;

import com.example.sns.User.Details.CustomUserDetails;
import com.example.sns.User.entity.User;
import com.example.sns.notification.DTO.NotificationDTO;
import com.example.sns.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // ✅ 1. 전체 알림 목록 조회 (읽은 + 안 읽은)
    @GetMapping("")
    public String getNotifications(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        List<NotificationDTO> notifications = notificationService.getAllNotifications(user);
        model.addAttribute("notifications", notifications);
        return "layout/notifications"; // 알림 목록 페이지
    }

    // ✅ 2. 읽지 않은 알림 개수 조회 (배지 숫자용)
    @GetMapping("/count")
    @ResponseBody
    public int getUnreadCount(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return notificationService.countUnread(userDetails.getUser());
    }

    // ✅ 3. 모든 읽지 않은 알림 읽음 처리 (패널 열 때)
    @PostMapping("/mark-read")
    @ResponseBody
    public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal CustomUserDetails userDetails) {
        notificationService.markAllAsRead(userDetails.getUser());
        return ResponseEntity.ok().build();
    }

    // ✅ 4. 특정 알림 하나 읽음 처리 (알림 클릭 시)
    @PostMapping("/{id}/read")
    @ResponseBody
    public ResponseEntity<Void> markNotificationRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }
}
