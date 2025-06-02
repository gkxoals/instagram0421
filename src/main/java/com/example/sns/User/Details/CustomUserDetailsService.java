package com.example.sns.User.Details;

import com.example.sns.User.entity.User;
import com.example.sns.User.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Spring Security에서 사용자 정보를 로드하는 서비스 클래스
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // 생성자를 통해 UserRepository 주입
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 이메일을 기반으로 사용자 정보를 로드하는 메서드
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("로그인 시도 이메일: " + email);

        // 이메일이 null이거나 비어 있는 경우 예외 발생
        if (email == null || email.trim().isEmpty()) {
            throw new UsernameNotFoundException("이메일이 비어 있습니다.");
        }

        // 데이터베이스에서 해당 이메일을 가진 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));

        // 조회된 사용자 정보를 Spring Security에서 사용할 수 있는 UserDetails 객체로 변환하여 반환
        return new CustomUserDetails(user);
    }
}
