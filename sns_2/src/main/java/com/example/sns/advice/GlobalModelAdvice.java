package com.example.sns.advice;

import com.example.sns.User.DTO.UserResponseDTO;
import com.example.sns.User.Details.CustomUserDetails;
import com.example.sns.User.service.UserService;
import com.example.sns.notification.DTO.NotificationDTO;
import com.example.sns.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collections;
import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAdvice {

    private final UserService userService;
    private final NotificationService notificationService;

    @ModelAttribute("loggedInUser")
    public UserResponseDTO populateLoggedInUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) return null;

        var user = userDetails.getUser();
        String profileImage = userService.getProfileImageByUserId(user.getUserId());

        return new UserResponseDTO(
                user.getUserId(),
                user.getName(),
                user.getPhone(),
                user.getEmail(),
                profileImage
        );
    }

    @ModelAttribute
    public void addUnreadNotificationCount(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails != null) {
            int count = notificationService.countUnread(userDetails.getUser());
            model.addAttribute("unreadCount", count);
        }
    }
    @ModelAttribute("notifications")
    public List<NotificationDTO> populateNotifications(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) return Collections.emptyList();
        return notificationService.getUnreadNotifications(userDetails.getUser());
    }

}
