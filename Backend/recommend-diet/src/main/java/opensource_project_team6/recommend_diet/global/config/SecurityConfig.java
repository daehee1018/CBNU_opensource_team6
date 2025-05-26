package opensource_project_team6.recommend_diet.global.config;

import lombok.RequiredArgsConstructor;
import opensource_project_team6.recommend_diet.global.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // 설정 클래스
@EnableWebSecurity // Spring Security 활성화
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /*
    * 사용자 비밀번호를 안전하게 암호화하기 위하여
    * */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /*
    * 사용자를 검증하고 인증 성공 시 Authentication 겍체를 반환
    * */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig
    ) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> {}) //CorsConfig에서 설정한 CORS 적용
                .csrf(csrf -> csrf.disable()) // JWT는 CSRF 필요 없으므로 꺼주기
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/api/auth/**").permitAll() // 회원가입, 로그인은 인증 불필요
                                //.anyRequest().authenticated() // 나머지 요청은 인증 필요
                                .anyRequest().permitAll() // 모든 요청 허용, DB POST, GET이용
                )
                .formLogin(login -> login.disable()) // 로그인 폼 비활성화 (나중에 지워줘야 할 코드)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
