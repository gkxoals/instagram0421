package com.example.sns.User.entity;

<<<<<<< HEAD
import com.example.sns.UserProfiles.entity.Profile;
=======
>>>>>>> 2276687 (초기 커밋)
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Table(name = "user", uniqueConstraints = {
        @UniqueConstraint(columnNames = "phone"),
        @UniqueConstraint(columnNames = "email")
})
@Getter
@Setter
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(length = 11, unique = true, nullable = false)
    private String phone;

    @Column(length = 50, unique = true, nullable = false)
    private String email;

    @Column(length = 200, nullable = false)
    private String password;

    @Column(length = 20, nullable = false)
    @Pattern(regexp = "^[^0-9]+$", message = "이름에는 숫자를 포함할 수 없습니다.")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    private LocalDateTime updatedAt = LocalDateTime.now();

    // ✅ 프로필 이미지 필드 추가
    @Column(length = 255)
    private String profileImage = "/images/default-profile.png"; // 기본 프로필 이미지 설정

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
