package com.example.sns.User.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor // 기본 생성자 추가
public class UserResponseDTO {
    private Long userId;
    private String name;  // 사용자 이름
    private String phone; // 전화번호
    private String email; // 이메일
    private String profileImage;

    // 일부 필드만 받는 생성자
    public UserResponseDTO(Long userId, String name, String phone, String email) {
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.profileImage = "/images/default-profile.png";
    }
}
