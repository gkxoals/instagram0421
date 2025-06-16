package com.example.sns.search;

import com.example.sns.User.entity.User;
import com.example.sns.User.repository.UserRepository;
import com.example.sns.UserProfiles.entity.Profile;
import com.example.sns.UserProfiles.repository.ProfileRepository;
import com.example.sns.post.entity.Post;
import com.example.sns.post.repository.PostRepository;
import com.example.sns.search.dto.PostSearchResultDto;
import com.example.sns.search.dto.UserSearchResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final PostRepository postRepository;
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    // ✅ 게시물 검색 (닉네임 + 게시글 내용)
    @GetMapping("/posts")
    public ResponseEntity<List<PostSearchResultDto>> searchPosts(@RequestParam("q") String keyword) {
        List<Post> posts = postRepository.searchByKeyword(keyword);

        List<PostSearchResultDto> results = posts.stream().map(post -> {
            String thumbnailUrl = post.getMediaList().isEmpty()
                    ? "/images/default-thumbnail.png"
                    : post.getMediaList().get(0).getFilePath();

            return new PostSearchResultDto(
                    post.getId(), // ✅ 반드시 넣어야 함
                    post.getContent(),
                    thumbnailUrl,
                    post.getUser().getUserId()

            );
        }).toList();

        return ResponseEntity.ok(results);
    }


    // ✅ 사용자 검색 (이름 + 이메일 기반)
    @GetMapping("/users")
    public ResponseEntity<List<UserSearchResultDto>> searchUsers(@RequestParam("q") String keyword) {
        List<User> users = userRepository.findByNameContainingIgnoreCase(keyword);

        List<UserSearchResultDto> results = users.stream()
                .map(user -> new UserSearchResultDto(user.getUserId(), user.getName(), user.getEmail()))
                .toList();

        return ResponseEntity.ok(results);
    }
}
