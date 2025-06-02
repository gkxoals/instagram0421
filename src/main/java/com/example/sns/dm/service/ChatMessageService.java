package com.example.sns.dm.service;

import com.example.sns.User.entity.User;
import com.example.sns.User.repository.UserRepository;
import com.example.sns.UserProfiles.repository.ProfileRepository;
import com.example.sns.dm.dto.ChatMessageDTO;
import com.example.sns.dm.entity.ChatMessage;
import com.example.sns.dm.entity.ChatRoom;
import com.example.sns.dm.repository.ChatMessageRepository;
import com.example.sns.dm.repository.ChatRoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.sns.UserProfiles.entity.Profile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    // ✅ WebSocket 컨트롤러에서 사용
    @Transactional
    public ChatMessage save(ChatMessageDTO dto, Long roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방 없음"));
        User sender = userRepository.findById(dto.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        ChatMessage message = new ChatMessage();
        message.setChatRoom(room);
        message.setSender(sender);
        message.setContent(dto.getContent());
        message.setSentAt(LocalDateTime.now());

        return chatMessageRepository.save(message);
    }


    // ✅ 채팅방 ID 기준으로 메시지 조회
    @Transactional
    public List<ChatMessageDTO> getMessagesByRoomId(Long roomId) {
        return chatMessageRepository.findByChatRoomIdOrderBySentAt(roomId).stream()
                .map(msg -> {
                    Long senderId = msg.getSender().getUserId();

                    String nickname = profileRepository.findByUserIdLong(senderId)
                            .map(Profile::getNickname)
                            .orElse("알 수 없음");

                    return new ChatMessageDTO(
                            msg.getId(),
                            senderId,
                            nickname,
                            msg.isDeleted() ? "[삭제된 메세지]" : msg.getContent(),
                            msg.getSentAt(),
                            msg.isDeleted() // ✅ 여기 추가
                    );
                })
                .toList();
    }

    @Transactional
    public String getNickname(Long userId) {
        return profileRepository.findByUserIdLong(userId)
                .map(Profile::getNickname)
                .orElse("알 수 없음");
    }

}
