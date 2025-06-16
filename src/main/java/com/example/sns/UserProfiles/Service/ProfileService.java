package com.example.sns.UserProfiles.Service;

import com.example.sns.UserProfiles.DTO.ProfileUpdateRequest;
import com.example.sns.UserProfiles.DTO.UserProfileDTO;
import com.example.sns.UserProfiles.Img.ImageUtil;
import com.example.sns.UserProfiles.repository.ProfileRepository;
import com.example.sns.User.entity.User;
import com.example.sns.User.repository.UserRepository;
import com.example.sns.UserProfiles.entity.Profile;
import com.example.sns.exception.ResourceNotFoundException;
import com.example.sns.post.repository.PostRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

// 프로필 관련 기능을 제공하는 서비스 클래스
@Service
public class ProfileService {

    //학원
    private static final String UPLOAD_DIR = "C:/Users/User/Desktop/sns_2/profileImage/";
//    //노트북
//    private static final String UPLOAD_DIR = "C:/Users/하태민/OneDrive/바탕 화면/sns_2/profileImage/";

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Autowired
    public ProfileService(ProfileRepository profileRepository, UserRepository userRepository, PostRepository postRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    public UserProfileDTO getUserProfileDTOByNickname(String nickname) {
        Profile profile = profileRepository.findByNickname(nickname)
                .orElseThrow(() -> new ResourceNotFoundException("프로필을 찾을 수 없습니다."));

        User user = profile.getUserId();
        int postCount = postRepository.countByUser(user); // 🔥 핵심

        return UserProfileDTO.builder()
                .id(user.getUserId())
                .nickname(profile.getNickname())
                .bio(profile.getBio())
                .gender(String.valueOf(profile.getGender()))
                .email(user.getEmail())
                .profileImage(user.getProfileImage())
                .postCount(postCount) // ✅ DTO에 포함
                .build();
    }






    /**
     * 사용자 ID로 프로필을 조회하거나, 존재하지 않으면 기본 프로필을 생성하여 반환
     *
     * @param userId 사용자 ID
     * @return 조회된 또는 새로 생성된 Profile 객체
     */
    public Profile getProfileWithUserId(Long userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with id=" + userId));

        Profile profile = profileRepository.findByUserId(user).orElseGet(() -> {
            Profile p = new Profile();
            p.setUserId(user);
            p.setNickname("User" + (int) (Math.random() * 10_000));
            return profileRepository.save(p);
        });

        return profile;
    }

    /**
     * 프로필 정보를 업데이트하는 메서드
     *
     * @param userId 사용자 ID
     * @param dto    업데이트할 프로필 정보
     */
    public void updateProfile(Long userId, ProfileUpdateRequest dto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with id=" + userId));

        Profile profile = profileRepository.findByUserId(user).orElseGet(() -> {
            Profile p = new Profile();
            p.setUserId(user);
            p.setNickname("user" + (int) (Math.random() * 10_000));
            return profileRepository.save(p);
        });

        if (dto.getNickname() != null && !dto.getNickname().trim().isEmpty()) {
            if (!dto.getNickname().equals(profile.getNickname())) {
                boolean exists = profileRepository.existsByNickname(dto.getNickname());
                if (exists) {
                    throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
                }
                profile.setNickname(dto.getNickname());
            }
        }


        if (dto.getBio() != null) {
            profile.setBio(dto.getBio());
        }
        if (dto.getGender() != null) {
            profile.setGender(dto.getGender());
        }

        // 프로필 이미지 처리
        MultipartFile profileImage = dto.getProfileImage();
        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                // 파일 확장자 확인
                String extension = FilenameUtils.getExtension(profileImage.getOriginalFilename()).toLowerCase();
                if (!isValidImageExtension(extension)) {
                    throw new IllegalArgumentException("허용되지 않은 파일 형식입니다.");
                }

                // 파일 크기 제한 (5MB)
                final long MAX_FILE_SIZE = 5 * 1024 * 1024;
                if (profileImage.getSize() > MAX_FILE_SIZE) {
                    throw new IllegalArgumentException("파일 크기가 너무 큽니다.");
                }

                // 업로드 디렉토리가 없으면 생성
                File uploadDir = new File(UPLOAD_DIR);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                // 기존 프로필 이미지 삭제
                deleteProfileImageFile(profile.getProfileImage());

                // 새로운 파일명 설정 (사용자 ID 기반)
                String filename = "profile_" + userId + "." + extension;
                String outputPath = UPLOAD_DIR + filename;

                // 이미지 저장
                ImageUtil.saveResizedImage(profileImage, outputPath);
                profile.setProfileImage("/profile-images/" + filename); // 이미지 URL 설정

            } catch (IOException e) {
                throw new RuntimeException("이미지 처리 중 오류 발생", e);
            }
        }

        profileRepository.save(profile);
    }

    /**
     * 사용자의 프로필 이미지를 삭제하는 메서드
     *
     * @param userId 사용자 ID
     */
    public void deleteProfileImage(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with id=" + userId));

        Profile profile = profileRepository.findByUserId(user).orElseThrow(() -> new ResourceNotFoundException("Profile not found for user id=" + userId));


        String imgPath = profile.getProfileImage();
        if (imgPath != null && !imgPath.isEmpty()) {
            deleteProfileImageFile(imgPath);
            profile.setProfileImage(null);
            profileRepository.save(profile);
        }
    }

    /**
     * 허용된 이미지 확장자인지 확인하는 메서드
     *
     * @param extension 파일 확장자
     * @return 허용된 확장자인 경우 true
     */
    private boolean isValidImageExtension(String extension) {
        return extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png") || extension.equals("gif");
    }

    /**
     * 저장된 프로필 이미지 파일을 삭제하는 메서드
     *
     * @param imagePath 삭제할 이미지 파일 경로
     */
    private void deleteProfileImageFile(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) return;

        // ✅ 기본 이미지라면 삭제하지 않음
        if (imagePath.contains("default-profile.png")) {
            System.out.println("기본 이미지는 삭제하지 않습니다.");
            return;
        }

        String filename = Paths.get(imagePath).getFileName().toString();
        String fullPath = UPLOAD_DIR + filename;
        File file = new File(fullPath);

        if (file.exists() && file.delete()) {
            System.out.println("이미지 삭제 성공: " + fullPath);
        } else {
            System.out.println("이미지 삭제 실패 또는 파일이 존재하지 않음: " + fullPath);
        }
    }


    public Profile getProfileByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id=" + userId));
        return profileRepository.findByUserId(user)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user id=" + userId));
    }

}
