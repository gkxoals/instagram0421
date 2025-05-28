package com.example.sns.UserProfiles.DTO;

import lombok.*;

@Builder // 빌더 패턴 적용
@Data // @Getter, @Setter, @ToString, @EqualsAndHashCode 포함
@NoArgsConstructor // 기본 생성자 제공
@AllArgsConstructor // 모든 필드를 포함한 생성자 제공
public class UserProfileDTO {
    private Long id;
    private String nickname; // 닉네임
    private String bio; // 자기소개
    private String gender; // 성별
    private String email; // 이메일
    private String profileImage;
}
