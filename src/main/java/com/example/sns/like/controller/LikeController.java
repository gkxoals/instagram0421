package com.example.sns.like.controller;

import com.example.sns.config.SecurityUtils;
import com.example.sns.like.DTO.LikeRequestDTO;
import com.example.sns.like.DTO.LikeResponseDTO;
import com.example.sns.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("")
    public ResponseEntity<LikeResponseDTO> toggleLike(@RequestBody LikeRequestDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        LikeResponseDTO response = likeService.toggleLike(userId, dto);
        return ResponseEntity.ok(response);
    }
}
