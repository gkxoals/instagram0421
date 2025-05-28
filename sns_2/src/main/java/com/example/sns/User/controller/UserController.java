package com.example.sns.User.controller;

import com.example.sns.User.DTO.UserDTO;
import com.example.sns.User.repository.UserRepository;
import com.example.sns.User.service.UserService;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j // 로깅(시스템이 작동되는 동안 발생하는 정보를 기록하는것을 의미)을 위한 Lombok 어노테이션
@RequiredArgsConstructor // final 필드에 대한 의존성 주입 자동 처리
@RequestMapping("/user") // "/user" 경로의 요청을 처리하는 컨트롤러
@Controller
public class UserController {

    private final UserService userService; // 사용자 관련 서비스
    private final UserRepository userRepository; // 사용자 저장소

    // 로그인 페이지 요청
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            Map<String, Object> model,
                            Authentication authentication
                            ) {
        if (authentication != null && authentication.isAuthenticated()) { //로그인 되면 로그인 접근 못함.
            return "redirect:/";
        }

        if (error != null) {
            model.put("errorMessage", "이메일 또는 비밀번호가 틀렸습니다.");
        }
        return "user/login";
    }

    // 회원가입 페이지 요청
    @GetMapping("/signup")
    public String signupPage(@RequestParam(value = "error", required = false) String error,
                             Map<String, Object> model,
                             Authentication authentication) {

        if (authentication != null && authentication.isAuthenticated()) { //로그인 되면 회원가입 접근 못함.
            return "redirect:/";
        }


        if (error != null) {
            model.put("errorMessage", "이미 존재하는 이메일 또는 전화번호입니다.");
        }
        return "user/signup";
    }

    // 회원가입 처리
    @PostMapping("/signup")
    public String signup(@ModelAttribute UserDTO userDTO, Map<String, Object> model) {
        try {
            log.info("회원가입 요청: {}", userDTO.getEmail());
            userService.create(userDTO);
            log.info("회원가입 성공! {}", userDTO.getEmail());
            return "redirect:/user/login";
        } catch (IllegalStateException e) {
            log.error("회원가입 실패: {}", e.getMessage());
            model.put("errorMessage", "이미 존재하는 이메일 또는 전화번호입니다.");
            return "user/signup";
        } catch (ConstraintViolationException e) {
            String message = e.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .findFirst()
                    .orElse("입력값이 올바르지 않습니다.");
            model.put("errorMessage", message);
            return "user/signup";
        }
    }


}

