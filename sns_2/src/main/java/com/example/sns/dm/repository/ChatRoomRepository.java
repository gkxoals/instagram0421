package com.example.sns.dm.repository;

import com.example.sns.dm.entity.ChatRoom;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT c FROM ChatRoom c WHERE c.user1.id = :userId OR c.user2.id = :userId")
    List<ChatRoom> findByUserId(@Param("userId") Long userId);

    @Query("SELECT c FROM ChatRoom c WHERE " +
            "(c.user1.id = :id1 AND c.user2.id = :id2) OR " +
            "(c.user1.id = :id2 AND c.user2.id = :id1)")
    Optional<ChatRoom> findByParticipants(@Param("id1") Long id1, @Param("id2") Long id2);

}
