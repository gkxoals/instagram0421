package com.example.sns.post.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class PostMedia {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filePath;

    private String mediaType;

    private int uploadOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

}
