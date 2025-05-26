package com.example.sns.UserProfiles.controller;

<<<<<<< HEAD
import com.example.sns.User.DTO.UserResponseDTO;
import com.example.sns.User.entity.User;
import com.example.sns.User.repository.UserRepository;
import com.example.sns.UserProfiles.DTO.ProfileUpdateRequest;
import com.example.sns.UserProfiles.Gender;
import com.example.sns.UserProfiles.Service.ProfileService;
import com.example.sns.UserProfiles.entity.Profile;
import com.example.sns.UserProfiles.repository.ProfileRepository;
import com.example.sns.config.SecurityUtils;
=======
import com.example.sns.UserProfiles.DTO.ProfileUpdateRequest;
import com.example.sns.UserProfiles.Gender;
import com.example.sns.UserProfiles.Service.ProfileService;
import com.example.sns.UserProfiles.repository.ProfileRepository;
import com.example.sns.User.repository.UserRepository;
import com.example.sns.config.Security.SecurityUtils;
>>>>>>> 2276687 (초기 커밋)
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

<<<<<<< HEAD
import java.util.Optional;

=======
>>>>>>> 2276687 (초기 커밋)
// 프로필 관련 요청을 처리하는 컨트롤러
@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    // 생성자를 통한 의존성 주입
    @Autowired
    public ProfileController(ProfileService profileService, UserRepository userRepository, ProfileRepository profileRepository) {
        this.profileService = profileService;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
    }

    // 프로필 페이지 요청 처리
    @GetMapping("")
    public String profile(Model model, @ModelAttribute("error") String error) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return "redirect:/login";
        }

        // ✅ 기존 프로필 정보도 유지
        model.addAttribute("profile", profileService.getProfileWithUserId(userId));

        if (!error.isEmpty()) {
            model.addAttribute("error", error);
        }

        return "profiles/profile";
    }



    // 프로필 업데이트 요청 처리
    @PostMapping("/update")
    public String updateProfile(@RequestParam(value = "nickname", required = false) String nickname,
                                @RequestParam(value = "bio", required = false) String bio,
                                @RequestParam(value = "gender", required = false) Gender gender,
                                @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
                                RedirectAttributes redirectAttributes) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return "redirect:/login"; // 로그인되지 않은 경우 로그인 페이지로 이동
        }

        // DTO 생성 및 값 설정
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setNickname(nickname);
        request.setBio(bio);
        request.setGender(gender);
        request.setProfileImage(profileImage);

        try {
            profileService.updateProfile(userId, request);
        } catch (IllegalArgumentException e) {
            // 예외 발생 시 메시지를 Flash Attribute로 저장 후, 원래 폼 페이지로 리디렉트
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/profile"; // 에러 메시지를 전달하며 폼 페이지로 리디렉트
        }

        return "redirect:/profile"; // 성공 시 프로필 페이지로 이동
    }

    // 프로필 이미지 삭제 요청 처리
    @PostMapping("/deleteImage")
    public ResponseEntity<String> deleteProfileImage() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            profileService.deleteProfileImage(userId);
            return ResponseEntity.ok("프로필 이미지 삭제 성공");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("프로필 사진 삭제 실패: " + e.getMessage());
        }
    }
}
