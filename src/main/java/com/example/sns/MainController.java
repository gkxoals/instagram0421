package com.example.sns;

import com.example.sns.User.DTO.UserResponseDTO;
import com.example.sns.User.entity.User;
import com.example.sns.User.repository.UserRepository;
import com.example.sns.UserProfiles.entity.Profile;
import com.example.sns.UserProfiles.repository.ProfileRepository;
import com.example.sns.post.DTO.PostDTO;
import com.example.sns.post.entity.Post;
import com.example.sns.post.service.PostService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MainController {

    private final PostService postService;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    public MainController(PostService postService, UserRepository userRepository, ProfileRepository profileRepository) {
        this.postService = postService;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
    }


    @GetMapping("/")
    public String Main(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = null;

        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {

            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails userDetails) {
                String email = userDetails.getUsername();
                user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));

                String profileImage = profileRepository.findByUserId(user)
                        .map(Profile::getProfileImage)
                        .orElse("/images/default-profile.png");

                UserResponseDTO dto = new UserResponseDTO(
                        user.getUserId(),
                        user.getName(),
                        user.getPhone(),
                        user.getEmail(),
                        profileImage
                );

                model.addAttribute("loggedInUser", dto);
            }
        }

        // ✅ 이제 user 사용 가능
        List<PostDTO> posts = postService.getAllPosts(user)
                .stream()
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .collect(Collectors.toList());

        model.addAttribute("posts", posts);

        return "main";
    }


}
