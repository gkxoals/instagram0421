package com.example.sns.dm.service;

import com.example.sns.User.DTO.UserListDTO;
import com.example.sns.User.entity.User;
import com.example.sns.User.repository.UserRepository;
import com.example.sns.UserProfiles.entity.Profile;
import com.example.sns.UserProfiles.repository.ProfileRepository;
import com.example.sns.config.Security.SecurityUtils;
import com.example.sns.dm.entity.ChatMessage;
import com.example.sns.dm.entity.ChatRoom;
import com.example.sns.dm.repository.ChatRoomRepository;
import com.example.sns.dm.repository.ChatMessageRepository; // ✅ 추가
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DmService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository; // ✅ 추가
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    // ✅ 현재 사용자 기준으로 DM 상대 목록 가져오기 (기존 대화방만)
    public List<UserListDTO> getUserListForCurrentUser() {
        Long myId = getCurrentUserId();
        List<ChatRoom> rooms = chatRoomRepository.findByUserId(myId);

        return rooms.stream()
                // ✅ 삭제된 방 제외
                .filter(room -> {
                    if (room.getUser1().getUserId().equals(myId)) {
                        return !room.isDeletedByUser1();
                    } else if (room.getUser2().getUserId().equals(myId)) {
                        return !room.isDeletedByUser2();
                    }
                    return false; // 내 방이 아니면 제외
                })
                // ✅ 메시지 존재하는 방만
                .filter(room -> chatMessageRepository.existsByChatRoom_Id(room.getId()))
                .map(room -> {
                    User partner = room.getOtherParticipant(myId);
                    Profile profile = profileRepository.findByUserId(partner).orElse(null);

                    String nickname = profile != null ? profile.getNickname() : "알 수 없음";
                    String profileImage = profile != null ? profile.getProfileImage() : "/images/default-profile.png";
                    String lastMessage = room.getLastMessageContent();

                    return new UserListDTO(
                            partner.getUserId(),
                            nickname,
                            profileImage,
                            room.getId(),
                            lastMessage
                    );
                })
                .collect(Collectors.toList());
    }


    // ✅ 채팅 상대 닉네임 가져오기 (내가 해당 방에 포함된 경우만)
    public String getPartnerNickname(Long roomId, Long myId) {
        Optional<ChatRoom> roomOpt = chatRoomRepository.findById(roomId);
        if (roomOpt.isEmpty()) return "알 수 없음";

        ChatRoom room = roomOpt.get();

        if (!room.getUser1().getUserId().equals(myId) &&
                !room.getUser2().getUserId().equals(myId)) {
            return "알 수 없음"; // 접근 불가
        }

        User partner = room.getOtherParticipant(myId);
        return profileRepository.findByUserId(partner)
                .map(Profile::getNickname)
                .orElse("알 수 없음");
    }

    // ✅ DM 시작 시 방이 없으면 새로 생성
    public Long createOrGetRoom(Long userId1, Long userId2) {
        return chatRoomRepository.findByParticipants(userId1, userId2)
                .map(ChatRoom::getId)
                .orElseGet(() -> {
                    ChatRoom room = new ChatRoom(
                            userRepository.getReferenceById(userId1),
                            userRepository.getReferenceById(userId2)
                    );
                    return chatRoomRepository.save(room).getId();
                });
    }

    private Long getCurrentUserId() {
        return SecurityUtils.getCurrentUserId();
    }

    public void deleteChatRoomForUser(Long roomId, Long userId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));

        if (room.getUser1().getUserId().equals(userId)) {
            room.setDeletedByUser1(true);
        } else if (room.getUser2().getUserId().equals(userId)) {
            room.setDeletedByUser2(true);
        } else {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }

        chatRoomRepository.save(room);
    }

    @Transactional
    public void deleteMyMessage(Long messageId, Long currentUserId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("메세지를 찾을 수 없습니다."));

        if (!message.getSender().getUserId().equals(currentUserId)) {
            throw new AccessDeniedException("자신의 메세지만 삭제 할수 있습니다.");
        }

        message.setDeleted(true);
        message.setContent("[삭제된 메세지]");

        chatMessageRepository.save(message);
    }

    @Transactional
    public void deleteChatRoom(Long roomId, Long currentUserId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방이 존재하지 않습니다."));

        boolean isUser1 = chatRoom.getUser1().getUserId().equals(currentUserId);
        boolean isUser2 = chatRoom.getUser2().getUserId().equals(currentUserId);

        if (!isUser1 && !isUser2) {
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }

        if (isUser1) chatRoom.setDeletedByUser1(true);
        if (isUser2) chatRoom.setDeletedByUser2(true);

        if (chatRoom.isDeletedByUser1() && chatRoom.isDeletedByUser2()) {
            chatRoomRepository.delete(chatRoom); // 메시지는 cascade + orphanRemoval로 같이 삭제됨
        }
    }


}
