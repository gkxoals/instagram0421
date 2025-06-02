package com.example.sns.User.DTO;

import lombok.*;

// Lombok을 사용하여 Getter, Setter, 생성자 자동 생성
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private String name;     // 사용자 이름
    private String password; // 비밀번호
    private String phone;    // 전화번호
    private String email;    // 이메일
}
