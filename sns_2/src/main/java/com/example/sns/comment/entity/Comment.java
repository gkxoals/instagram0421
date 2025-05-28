package com.example.sns.comment.entity;

import com.example.sns.User.entity.User;
import com.example.sns.post.entity.Post;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private     User user;

    @Column(nullable = false)
    private String content;  // 댓글 내용

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();  // 댓글 작성 시간

    @Column
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;  // 부모 댓글, null이면 최상위 댓글

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Comment> replies = new ArrayList<>();  // 대댓글 리스트

}
