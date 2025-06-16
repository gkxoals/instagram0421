package com.example.sns.User.service;

import com.example.sns.User.DTO.UserDTO;
import com.example.sns.User.DTO.UserListDTO;
import com.example.sns.User.DTO.UserResponseDTO;
import com.example.sns.User.entity.Role;
import com.example.sns.User.entity.User;
import com.example.sns.User.repository.UserRepository;
import com.example.sns.UserProfiles.entity.Profile;
import com.example.sns.UserProfiles.repository.ProfileRepository;
import com.example.sns.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Lombok을 사용하여 생성자 주입 자동화
public class
UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Spring Security 의 비밀번호 암호화 기능
    private final ProfileRepository profileRepository;

    // 회원가입 기능
    @Transactional
    public UserResponseDTO create(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalStateException("이미 존재하는 이메일입니다.");
        }
        if (userRepository.existsByPhone(userDTO.getPhone())) {
            throw new IllegalStateException("이미 존재하는 전화번호입니다.");
        }

        User user = new User();
        user.setName(userDTO.getName());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setPhone(userDTO.getPhone());
        user.setEmail(userDTO.getEmail());
        user.setProfileImage("/images/default-profile.png"); // ✅ 기본 프로필 이미지 설정

        if (user.getRole() == null) {
            user.setRole(Role.ROLE_USER);
        }

        user = userRepository.save(user);

        String generatedNickname;
        do {
            generatedNickname = "User" + (int) (Math.random() * 10_000);
        } while (profileRepository.existsByNickname(generatedNickname));

        Profile profile = new Profile();
        profile.setUserId(user); // 프로필에 유저 연결
        profile.setNickname(generatedNickname); // 기본 닉네임을 이름으로 설정 (또는 다른 기본값)
        profile.setProfileImage("/images/default-profile.png"); // 프로필 이미지 기본값
        profileRepository.save(profile); // 프로필 저장

        return new UserResponseDTO(user.getUserId(), user.getName(), user.getPhone(), user.getEmail(), user.getProfileImage());
    }

    @Transactional
    public String getProfileImageByUserId(Long userId) {
        return userRepository.findById(userId)
                .flatMap(user -> profileRepository.findByUserId(user)
                        .map(profile -> {
                            String profileImage = profile.getProfileImage();
                            return (profileImage != null && !profileImage.isEmpty())
                                    ? profileImage
                                    : "/images/default-profile.png";
                        }))
                .orElse("/images/default-profile.png");
    }

    @Transactional
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 사용자를 찾을 수 없습니다."));
    }

    @Transactional
    public UserResponseDTO getUserResponseByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return new UserResponseDTO(
                user.getUserId(),
                user.getName(),
                user.getPhone(),
                user.getEmail(),
                user.getProfileImage()
        );
    }

    @Transactional
    public List<UserListDTO> getAllExcept(Long excludeId) {
        return userRepository.findAll().stream()
                .filter(user -> !user.getUserId().equals(excludeId))
                .map(this::convertToUserListDTO)
                .collect(Collectors.toList());
    }

    private UserListDTO convertToUserListDTO(User user) {
        Profile profile = profileRepository.findByUserId(user).orElse(null);

        String nickname = (profile != null && profile.getNickname() != null && !profile.getNickname().isEmpty())
                ? profile.getNickname()
                : "알 수 없음";

        String profileImage = (profile != null && profile.getProfileImage() != null && !profile.getProfileImage().isEmpty())
                ? profile.getProfileImage()
                : "/images/default-profile.png";

        return new UserListDTO(
                user.getUserId(),
                nickname,
                profileImage,
                null, // roomId는 없음
                null  // lastMessage도 없음
        );
    }

    @Transactional
    public User getCurrentUser(Principal principal) {
        if (principal == null) return null;
        String email = principal.getName(); // 기본적으로 email로 저장되어 있음
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));
    }

}
