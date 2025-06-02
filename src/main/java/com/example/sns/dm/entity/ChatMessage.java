package com.example.sns.dm.entity;

import com.example.sns.User.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ChatMessage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private ChatRoom chatRoom; // 또는 private Dm dm;

    @ManyToOne
    private User sender;

    @Column(name = "content", nullable = false, length = 255)
    private String content;


    private LocalDateTime sentAt;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;


    public ChatMessage(ChatRoom chatRoom, User sender, String content) {
        this.chatRoom = chatRoom;
        this.sender = sender;
        this.content = content;
        this.sentAt = LocalDateTime.now();
    }
}
