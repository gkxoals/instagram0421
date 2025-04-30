package com.example.sns.config;

// Spring Security 관련 클래스를 가져옴
import com.example.sns.User.Details.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// @Configuration: 해당 클래스가 설정 클래스임을 나타냄
@Configuration
public class SecurityConfig {

    // 사용자 인증을 위한 CustomUserDetailsService 객체 주입
    private final CustomUserDetailsService userDetailsService;

    // 생성자를 이용한 의존성 주입 (DI)
    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // Spring Security의 보안 설정을 담당하는 Bean 등록
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user/signup", "/user/login", "/api/user/**").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/posts", "/posts/**").permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable()) // CSRF 보호 기능 비활성화 (API 사용 시 보통 비활성화)
                .formLogin(form -> form
                        .loginPage("/user/login") // 로그인 페이지 지정
                        .loginProcessingUrl("/user/login") // 로그인 요청 처리 URL
                        .usernameParameter("email") // 로그인 시 입력할 username 필드명을 "email"로 변경
                        .passwordParameter("password") // 로그인 시 비밀번호 필드명 설정
                        .defaultSuccessUrl("/", true) // 로그인 성공 시 이동할 페이지
                        .failureUrl("/user/login?error=true") // 로그인 실패 시 이동할 페이지
                        .permitAll() // 로그인 페이지는 누구나 접근 가능
                )
                .logout(logout -> logout
                        .logoutUrl("/user/logout") // 로그아웃 요청 URL
                        .logoutSuccessUrl("/user/login") // 로그아웃 성공 후 이동할 페이지
                        .invalidateHttpSession(true) // 세션 무효화
                        .deleteCookies("JSESSIONID") // JSESSIONID 쿠키 삭제
                        .permitAll() // 로그아웃 기능은 누구나 접근 가능
                );

        return http.build();
    }

    // 비밀번호 암호화를 위한 PasswordEncoder Bean 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt 해시 알고리즘을 사용하여 비밀번호 암호화
    }

    // AuthenticationManager Bean 등록 (로그인 인증을 처리)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
