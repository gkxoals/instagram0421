package com.example.sns.config.webSocket;

import com.example.sns.dm.dto.ChatMessageDTO;
import com.example.sns.dm.entity.ChatMessage;
import com.example.sns.dm.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class ChatMessageWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;

    @MessageMapping("/chat.send/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId, ChatMessageDTO dto) {
        dto.setSentAt(LocalDateTime.now()); // 서버 시간

        // 1. 메시지 저장
        ChatMessage savedMessage = chatMessageService.save(dto, roomId);

        // 2. 저장된 메시지로부터 새로운 DTO 생성 (ID 포함됨)
        ChatMessageDTO responseDto = new ChatMessageDTO(
                savedMessage.getId(),
                savedMessage.getSender().getUserId(),
                chatMessageService.getNickname(savedMessage.getSender().getUserId()),
                savedMessage.getContent(),
                savedMessage.getSentAt(),
                savedMessage.isDeleted()
        );

        // 3. JS로 보냄
        messagingTemplate.convertAndSend("/topic/chat/" + roomId, responseDto);
    }

}
