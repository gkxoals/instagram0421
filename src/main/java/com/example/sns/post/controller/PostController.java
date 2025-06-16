package com.example.sns.post.controller;

import com.example.sns.User.Details.CustomUserDetails;
import com.example.sns.User.entity.User;
import com.example.sns.UserProfiles.entity.Profile;
import com.example.sns.UserProfiles.repository.ProfileRepository;
import com.example.sns.comment.DTO.CommentDTO;
import com.example.sns.comment.service.CommentService;
import com.example.sns.notification.service.NotificationService;
import com.example.sns.post.DTO.PostDTO;
import com.example.sns.post.entity.Post;
import com.example.sns.post.repository.PostRepository;
import com.example.sns.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Sort;


import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostRepository postRepository;
    private final PostService postService;
    private final CommentService commentService;
    private final NotificationService notificationService;
    private final ProfileRepository profileRepository;


    @GetMapping("/")
    public String showMainFeed(Model model,
                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        model.addAttribute("posts", postService.getAllPosts(user));
        model.addAttribute("notifications", notificationService.getUnreadNotifications(user)); // ✅ 정상 주입됨
        return "main";
    }



    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("posts", postRepository.findAll(Sort.by(Sort.Direction.DESC, "id")));
        return "posts/create";
    }

    @PostMapping("")
    public ResponseEntity<String> uploadPost(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                             @RequestParam("content") String content,
                                             @RequestParam("mediaFiles") List<MultipartFile> mediaFiles) {
        try {
            Long userId = customUserDetails.getUser().getUserId();
            postService.savePost(userId, content, mediaFiles);
            return ResponseEntity.ok("업로드 성공");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("에러: " + e.getMessage());
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다."));
        model.addAttribute("post", post);
        return "posts/edit";
    }

    @PostMapping("/edit/{id}")
    public String updatePost(@PathVariable Long id,
                             @RequestParam String content,
                             @RequestParam(value = "mediaFiles", required = false) List<MultipartFile> mediaFiles) throws IOException {
        postService.updatePost(id, content, mediaFiles != null ? mediaFiles : List.of());
        return "redirect:/";
    }


    @PostMapping("/delete/{id}")
    public String deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return "redirect:/";
    }
    @GetMapping("/{id}")
    public String getPostDetail(@PathVariable Long id, Model model, Principal principal) {
        PostDTO post = postService.getPostById(id, principal);
        model.addAttribute("post", post);
        model.addAttribute("comments", post.getComments()); // 선택 사항

        return "posts/detail-modal";
    }

    @GetMapping("/search")
    public String search(@RequestParam("q") String keyword, Model model) {
        List<Post> results = postService.searchPosts(keyword);
        model.addAttribute("posts",results);
        model.addAttribute("keyword",keyword);
        return "search/search";
    }
    // 모달용 게시물 상세 보기
    @GetMapping("/{id}/modal")
    public String getPostModal(@PathVariable Long id, Model model) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        List<CommentDTO> commentDTOList = commentService.getCommentsByPostId(id);
        String nickname = profileRepository.findByUserId(post.getUser())
                .map(Profile::getNickname)
                .orElse("알 수 없음");

        PostDTO dto = new PostDTO(post, null, nickname, commentDTOList, null);
        model.addAttribute("post", dto);
        return "posts/detail-modal :: modal-content";
    }



}
