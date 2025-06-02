package com.example.sns.User.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserListDTO {
    private Long userId;
    private String nickname;
    private String profileImage;
    private Long roomId;
    private String lastMessage;

}
