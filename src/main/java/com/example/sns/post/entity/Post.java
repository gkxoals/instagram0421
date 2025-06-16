package com.example.sns.post.entity;

import com.example.sns.User.entity.User;
import com.example.sns.comment.entity.Comment;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 작성자 정보 가져오기
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String content;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("uploadOrder ASC")
    private List<PostMedia> mediaList = new ArrayList<>();


    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();


    @Column
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(nullable = false)
    private Integer likeCount = 0;


    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    @OrderBy("createdAt ASC")
    private List<Comment> comments = new ArrayList<>();

    public String getThumbnailUrl() {
        return mediaList.stream()
                .filter(media -> "IMAGE".equals(media.getMediaType()))
                .findFirst()
                .map(PostMedia::getFilePath)
                .orElse("/images/default-thumbnail.png"); // 기본 이미지 경로
    }


}
