package com.example.sns.dm.entity;

import com.example.sns.User.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id")
    private User user1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id")
    private User user2;

    private String lastMessage; // 최신 메시지 캐시

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> messages = new ArrayList<>();


    @Column(name = "deleted_by_user1")
    private boolean deletedByUser1 = false;

    @Column(name = "deleted_by_user2")
    private boolean deletedByUser2 = false;

    public ChatRoom(User user1, User user2) {
        this.user1 = user1;
        this.user2 = user2;
    }

    public User getOtherParticipant(Long myId) {
        return user1.getUserId().equals(myId) ? user2 : user1;
    }

    public String getLastMessageContent() {
        return lastMessage != null ? lastMessage : "";
    }

}
