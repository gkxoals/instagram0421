package com.example.sns.dm.controller;

import com.example.sns.User.DTO.UserListDTO;
import com.example.sns.User.service.UserService;
import com.example.sns.UserProfiles.entity.Profile;
import com.example.sns.User.repository.UserRepository;
import com.example.sns.UserProfiles.repository.ProfileRepository;
import com.example.sns.config.Security.SecurityUtils;
import com.example.sns.dm.dto.ChatMessageDTO;
import com.example.sns.dm.repository.ChatRoomRepository;
import com.example.sns.dm.service.ChatMessageService;
import com.example.sns.dm.service.DmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.sns.config.Security.SecurityUtils.getCurrentUserId;

@Controller
@RequestMapping("/dm")
@RequiredArgsConstructor
public class DmController {

    private final DmService dmService;
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final ChatMessageService chatMessageService;
    private final UserService userService;
    private final ChatRoomRepository chatRoomRepository;

    @GetMapping({"", "/"})
    public String redirectToUserList() {
        return "redirect:/dm/users";
    }

    // ✅ 유저 선택해서 DM 시작
    @GetMapping("/start")
    public String startDm(@RequestParam Long targetId) {
        Long currentUserId = getCurrentUserId();
        Long roomId = dmService.createOrGetRoom(currentUserId, targetId);
        return "redirect:/dm/users?roomId=" + roomId;
    }

    // ✅ DM 메인 페이지
    @GetMapping("/users")
    public String showDmPage(@RequestParam(required = false) Long roomId, Model model) {
        Long myId = getCurrentUserId();

        List<UserListDTO> allUsers = userService.getAllExcept(myId);
        model.addAttribute("allUsers", allUsers);

        List<UserListDTO> chatRooms = dmService.getUserListForCurrentUser();
        model.addAttribute("chatRooms", chatRooms);

        String nickname = profileRepository.findByUserId(userRepository.getReferenceById(myId))
                .map(Profile::getNickname)
                .orElse("알 수 없음");
        model.addAttribute("nickname", nickname);
        model.addAttribute("userId", myId);

        // ✅ 여기에 넣어야 함: 항상 넣는다
        model.addAttribute("roomId", roomId);

        if (roomId == null) {
            model.addAttribute("chatPartnerNickname", "");
            return "dm/dm";
        }

        boolean isValidRoom = chatRoomRepository.findById(roomId)
                .filter(room -> room.getUser1().getUserId().equals(myId) || room.getUser2().getUserId().equals(myId))
                .isPresent();

        if (isValidRoom) {
            try {
                List<ChatMessageDTO> messages = chatMessageService.getMessagesByRoomId(roomId);
                String chatPartnerNickname = dmService.getPartnerNickname(roomId, myId);

                model.addAttribute("messages", messages);
                model.addAttribute("chatPartnerNickname", chatPartnerNickname);
            } catch (Exception e) {
                model.addAttribute("chatPartnerNickname", "에러 발생");
            }
        } else {
            model.addAttribute("chatPartnerNickname", "잘못된 접근");
            model.addAttribute("messages", List.of());
        }

        return "dm/dm";
    }

    @PostMapping("/messages/{messageId}/delete")
    public ResponseEntity<?> deleteMyMessage(@PathVariable Long messageId) {
        Long userId = SecurityUtils.getLoggedInUserId();
        dmService.deleteMyMessage(messageId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/chatrooms/{roomId}/delete")
    public ResponseEntity<?> deleteChatRoom(@PathVariable Long roomId) {
        Long userId = SecurityUtils.getLoggedInUserId();
        dmService.deleteChatRoom(roomId, userId);
        return ResponseEntity.ok().build();
    }



}
