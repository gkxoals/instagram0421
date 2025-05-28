package com.example.sns.like.repository;

import com.example.sns.User.entity.User;
import com.example.sns.like.LikeType;
import com.example.sns.like.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    int countByTargetIdAndTargetType(Long targetId, LikeType targetType);
    Optional<Like> findByUserAndTargetIdAndTargetType(User user, Long targetId, LikeType targetType);
    void deleteByUserAndTargetIdAndTargetType(User user, Long targetId, LikeType targetType);
}