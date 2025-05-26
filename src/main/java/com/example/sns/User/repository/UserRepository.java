package com.example.sns.User.repository;

import com.example.sns.User.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Spring Data JPA의 JpaRepository를 상속받아 사용자 관련 데이터 처리
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); // 이메일로 사용자 조회
    boolean existsByEmail(String email);      // 이메일 존재 여부 확인
    boolean existsByPhone(String phone);      // 전화번호 존재 여부 확인
<<<<<<< HEAD
=======

>>>>>>> 2276687 (초기 커밋)
}
