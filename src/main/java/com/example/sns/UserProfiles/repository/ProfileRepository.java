package com.example.sns.UserProfiles.repository;

import com.example.sns.User.entity.User;
import com.example.sns.UserProfiles.entity.Profile;
<<<<<<< HEAD
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
=======
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
>>>>>>> 2276687 (초기 커밋)
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    /**
     * 프로필을 조회할 때 User 엔티티도 함께 로딩하도록 설정
     *
     * @param id 프로필 ID
     * @return 조회된 Profile 객체 (Optional)
     */
    @EntityGraph(attributePaths = {"userId"})
    Optional<Profile> findById(Long id);

    /**
     * 사용자 ID로 프로필을 조회하는 메서드
     *
     * @param userId 조회할 User 객체
     * @return 조회된 Profile 객체 (Optional)
     */
    Optional<Profile> findByUserId(User userId);

<<<<<<< HEAD
=======
    @Query("SELECT p FROM Profile p WHERE p.userId.userId = :userId")
    Optional<Profile> findByUserIdLong(@Param("userId") Long userId); // ✅ Long 타입 지원용


>>>>>>> 2276687 (초기 커밋)
    /**
     * 특정 닉네임이 이미 존재하는지 확인하는 메서드
     *
     * @param nickname 확인할 닉네임
     * @return 존재하면 true, 없으면 false 반환
     */
    boolean existsByNickname(String nickname);
<<<<<<< HEAD
=======
    Optional<Profile> findByNickname(String nickname);

>>>>>>> 2276687 (초기 커밋)
}
