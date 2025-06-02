package com.example.sns.User.Details;

import com.example.sns.User.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

// Spring Security의 UserDetails 인터페이스를 구현한 사용자 정보 클래스
public class CustomUserDetails implements UserDetails {

    private final User user; // 데이터베이스에서 조회한 사용자 정보

    // 생성자에서 User 객체를 받아옴
    public CustomUserDetails(User user) {
        this.user = user;
    }

    // 사용자의 권한을 반환 (기본적으로 ROLE_USER 부여)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    // 사용자의 비밀번호 반환
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // 사용자의 로그인 ID 반환 (이메일 사용)
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    // 계정이 만료되지 않았는지 여부
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정이 잠겨 있지 않은지 여부
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 비밀번호가 만료되지 않았는지 여부
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정이 활성화되어 있는지 여부
    @Override
    public boolean isEnabled() {
        return true;
    }

    // 사용자 ID 반환
    public Long getUserId() {
        return user.getUserId();
    }

    // 사용자 이름 반환
    public String getName() {
        return user.getName(); // User 엔티티에서 name 필드를 반환
    }

    public User getUser() {
        return this.user;
    }
}
