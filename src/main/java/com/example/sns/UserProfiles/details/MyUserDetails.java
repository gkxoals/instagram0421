package com.example.sns.UserProfiles.details;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;
import com.example.sns.User.entity.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter
@Setter
@NoArgsConstructor
public class MyUserDetails implements UserDetails {
    private Long userId; // 사용자 ID
    private String username; // 사용자 이름 (로그인 ID)
    private String password; // 사용자 비밀번호
    private Collection<? extends GrantedAuthority> authorities; // 사용자의 권한 목록

    // 모든 필드를 초기화하는 생성자
    public MyUserDetails(Long userId, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    // User 엔티티를 받아 `MyUserDetails` 객체로 변환하는 생성자
    public MyUserDetails(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User 객체가 null입니다.");
        }

        this.userId = user.getUserId();
        this.username = user.getName();
        this.password = user.getPassword();
        this.authorities = List.of(new SimpleGrantedAuthority(user.getRole().name())); // 사용자 역할(Role)을 권한으로 변환
    }

    @Override
    public String getUsername(){
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities != null ? authorities : List.of();
    }

    // 계정이 만료되지 않았는지 여부 (true면 만료되지 않음)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정이 잠겨있지 않은지 여부 (true면 잠겨있지 않음)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 자격 증명이 만료되지 않았는지 여부 (true면 만료되지 않음)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정이 활성화되었는지 여부 (true면 활성화됨)
    @Override
    public boolean isEnabled() {
        return true;
    }
}
