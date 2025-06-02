package com.example.sns.comment.repository;

import com.example.sns.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Modifying
    @Query("DELETE FROM Comment c WHERE c.post.id = :postId")
    void deleteByPostId(@Param("postId") Long postId);
    List<Comment> findByPostId(Long postId);
    void deleteByPostIdAndParentIdIsNotNull(Long postId);
    void deleteByPostIdAndParentIdIsNull(Long postId);

}
