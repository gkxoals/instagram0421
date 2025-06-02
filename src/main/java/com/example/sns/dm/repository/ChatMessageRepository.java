package com.example.sns.dm.repository;

import com.example.sns.dm.entity.ChatMessage;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomIdOrderBySentAt(Long chatRoomId);
    boolean existsByChatRoom_Id(Long chatRoomId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ChatMessage m WHERE m.chatRoom.id = :roomId")
    void hardDeleteByChatRoomId(@Param("roomId") Long roomId);

}
