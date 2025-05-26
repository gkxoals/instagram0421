package com.example.sns.dm.repository;

import com.example.sns.dm.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomIdOrderBySentAt(Long chatRoomId);
    boolean existsByChatRoom_Id(Long chatRoomId);
}
