package opensource_project_team6.recommend_diet.global.oauth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import opensource_project_team6.recommend_diet.domain.user.entity.Gender;
import opensource_project_team6.recommend_diet.domain.user.entity.HealthConcern;
import opensource_project_team6.recommend_diet.domain.user.entity.Interest;
import opensource_project_team6.recommend_diet.domain.user.entity.User;
import opensource_project_team6.recommend_diet.domain.user.repository.UserRepository;
import opensource_project_team6.recommend_diet.global.jwt.JwtProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // oauth 프로필 추출
        String googleId = oAuth2User.getAttribute("sub");
        String email = oAuth2User.getAttribute("email");
        String profileImage = oAuth2User.getAttribute("picture");

        // 회원가입 여부 확인
        User user = userRepository.findByGoogleId(googleId).orElseGet(() -> {
            User newUser = User.builder()
                    .name("") //기본값
                    .email(email)
                    .password(null)
                    .birthDate(LocalDate.of(1900, 1, 1)) //기본값
                    .height(0.0)
                    .weight(0.0)
                    .targetWeight(0.0)
                    .gender(Gender.NONE)
                    .interest(Interest.NONE)
                    .healthConcern(HealthConcern.NONE)
                    .googleId(googleId)
                    .profileImage(profileImage)
                    .build();
            return userRepository.save(newUser);
        });

        Boolean isProfileComplete = user.isProfileComplete();


        String token = jwtProvider.generateToken(user.getEmail(),isProfileComplete);
        /*
        방법 1. 프론트와 연결 후 리다이렉트 방식으로 변경 프론트에서 토큰 추출 -> 프론트에서 replaceState()를 이용하여 URL에서 제거
        response.sendRedirect("http://localhost:3000/oauth-success?token=" + token + "&isProfileComplete=" + isProfileComplete);
        /*
        * 방법 2. 프론트와 연결 후 리다이렉트 방식을 이용하지만 토큰을 Set-Cookie 헤더로 전달
        * Cookie cookie = new Cookie("token", token);
          cookie.setHttpOnly(true);
          cookie.setPath("/");
          cookie.setSecure(true); // HTTPS 환경일 때
          response.addCookie(cookie);
          response.sendRedirect("http://localhost:3000/oauth2/success");
        * */

        // json으로 응답 프론트에서 토큰을 꺼내어서 localStorage에 저장
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"token\": \"" + token + "\"}");
        response.getWriter().flush();

        log.info("✅ [구글 로그인] JWT 발급 완료 - 토큰 값: {}", token);
    }
}
