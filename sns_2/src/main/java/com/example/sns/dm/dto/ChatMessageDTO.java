package com.example.sns.dm.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDTO {

    @JsonProperty("id") // ✅ JSON의 "id" 필드 → messageId로 매핑
    private Long messageId;

    private Long senderId;
    private String nickname;
    private String content;
    private LocalDateTime sentAt;
    private boolean isDeleted;
}


