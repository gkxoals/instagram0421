package com.example.sns.UserProfiles.entity;

import com.example.sns.User.entity.User;
import com.example.sns.UserProfiles.Gender;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "user_profiles") // 데이터베이스 테이블 이름 설정
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 ID 설정
    private Long profileId; // 프로필 ID

    @OneToOne(fetch = FetchType.EAGER) // 즉시 로딩(EAGER) 방식으로 설정하여 사용자 정보 즉시 가져옴
    @JoinColumn(name = "userId", unique = true, nullable = false) // userId를 외래키(FK)로 설정, 고유(unique) 설정
    private User userId; // 연결된 사용자 정보

    @Column(length = 30, unique = true, name = "nickname", nullable = true)
    private String nickname; // 닉네임 (최대 30자, 고유 값)

    @Column
    private String bio; // 자기소개 (Bio)

    @Enumerated(EnumType.STRING) // ENUM 값을 문자열로 저장
    @Column
    private Gender gender; // 성별 정보

    @Column
    private String profileImage; // 프로필 이미지 경로 또는 URL

    @Column(updatable = false) // 최초 생성된 이후 수정되지 않도록 설정
    private LocalDateTime createdAt = LocalDateTime.now(); // 프로필 생성 날짜

    @Column
    private LocalDateTime updatedAt = LocalDateTime.now(); // 프로필 수정 날짜
}
