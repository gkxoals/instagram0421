package com.example.sns.comment.DTO;

import com.example.sns.comment.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
    private Long id;
    private String nickname; // 템플릿에서 comment.nickname 으로 접근하기 쉽게 명명
    private String content;
    private LocalDateTime createdAt;
    private Long parentId;  // 부모 댓글 ID (null이면 최상위 댓글)
    private Long userId;

    public CommentDTO(Comment comment,String nickname) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
        this.parentId = comment.getParent() != null ? comment.getParent().getId() : null;
        this.userId = comment.getUser().getUserId();
        this.nickname = nickname;
    }


}