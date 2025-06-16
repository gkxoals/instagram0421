package com.example.sns.comment.controller;

import ch.qos.logback.core.model.Model;
import com.example.sns.User.DTO.UserResponseDTO;
import com.example.sns.User.service.UserService;
import com.example.sns.User.entity.User;
import com.example.sns.comment.DTO.CommentReplyRequestDto;
import com.example.sns.comment.entity.Comment;
import com.example.sns.comment.repository.CommentRepository;
import com.example.sns.comment.service.CommentService;
import com.example.sns.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {

    private final UserService userService;
    private final CommentService commentService;
    private final CommentRepository commentRepository;


    @PostMapping("/add")
    public String addComment(@RequestParam Long postId, @RequestParam String content, Principal principal) {
        String userEmail = principal.getName();
        UserResponseDTO userDTO = userService.getUserResponseByEmail(userEmail);
        commentService.saveComment(postId, content, userDTO.getUserId());
        return "redirect:/";
    }

    @PostMapping("/reply")
    public String createReply(@ModelAttribute CommentReplyRequestDto dto, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        String email = principal.getName();
        UserResponseDTO userDTO = userService.getUserResponseByEmail(email);

        Comment parentComment = commentRepository.findById(dto.getParentId())
                .orElseThrow(() -> new IllegalArgumentException("부모 댓글이 없습니다."));

        Post post = parentComment.getPost();
        commentService.createReply(dto.getContent(), post, parentComment, userDTO);
        return "redirect:/";
    }

    @PostMapping("/delete/{id}")
    public String deleteComment(@PathVariable Long id, Principal principal) {
        if (principal == null) return "redirect:/login";

        String email = principal.getName();
        UserResponseDTO userDTO = userService.getUserResponseByEmail(email); // ✅ DTO 반환 메서드 필요

        commentService.deleteComment(id, userDTO); // ✅ DTO 사용
        return "redirect:/";
    }

}
