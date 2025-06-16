package com.example.sns.post.repository;

import com.example.sns.User.entity.User;
import com.example.sns.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByUserUserId(Long userId);


    @Query("SELECT p FROM Post p " +
            "JOIN p.user u " +
            "JOIN Profile prof ON prof.userId = u " +
            "WHERE LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(prof.nickname) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Post> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT DISTINCT p FROM Post p LEFT JOIN FETCH p.mediaList WHERE p.user.userId = :userId")
    List<Post> findByUserIdWithMedia(@Param("userId") Long userId);

    @Query("SELECT p FROM Post p " +
            "LEFT JOIN FETCH p.mediaList " +
            "JOIN FETCH p.user u " +
            "WHERE p.id = :postId")
    Optional<Post> findByIdWithMediaAndUser(@Param("postId") Long postId);


    int countByUser(User user);
}
