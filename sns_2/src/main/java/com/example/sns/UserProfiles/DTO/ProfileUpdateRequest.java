package com.example.sns.UserProfiles.DTO;

import com.example.sns.UserProfiles.Gender;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ProfileUpdateRequest {
    private String nickname; // 사용자의 변경할 닉네임
    private String bio; // 사용자의 자기소개 (한줄 소개)
    private MultipartFile profileImage; // 프로필 이미지 (파일 업로드)
    private Gender gender; // 성별 (ENUM 타입)
}
