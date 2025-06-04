package opensource_project_team6.recommend_diet.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    /*
    * CORS란 브라우저가 다른 출처의 자원 요청을 허용할지를 서버가 결정하는 보안 정책
    * */

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // 모든 요청 경로에 대해 CORS 허용
                        .allowedOrigins(
                                "http://localhost:3000", // 프론트 개발 서버 주소
                                "http://10.0.2.2:8080" // Android Emulator에서 localhost를 가리키는 주소
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS","PATCH") // 허용할 HTTP 메서드
                        .allowedHeaders("*") // 요청에 포함될 수 있는 모든 헤더 허용
                        .allowCredentials(true); // 인증정보(쿠키, 헤더 등) 허용
            }
        };
    }
}
