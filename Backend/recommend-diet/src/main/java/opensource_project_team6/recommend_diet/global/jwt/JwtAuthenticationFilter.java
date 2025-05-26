package opensource_project_team6.recommend_diet.global.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    /*
    * JwtAuthenticationFilter는 HTTP 요청에서 JWT토큰을 추출하고 이를 통해 인증된 사용자임을 Spring Security에 알려주는 역할
    * 1. 클라이언트가 Authorization: Bearer {JWT} 헤더와 함께 요청 보냄
    * 2. doFilterInternal()이 실행됨
    *    └ resolveToken() → JWT 추출
    *    └ validateToken() → 유효성 검사
    * 3. 유효한 토큰이면
    *    └ getUsername() → 사용자 정보 추출
    *    └ UsernamePasswordAuthenticationToken 생성
    * 4. SecurityContextHolder.getContext().setAuthentication(...)으로
    *    현재 요청에 대해 "이 사용자는 인증된 사용자입니다"라고 등록
    * 5. 이후 컨트롤러에 도달하면 @AuthenticationPrincipal, SecurityUtil 등으로 사용자 정보 조회 가능
    * 6. 인증이 완료된 상태로 다음 필터로 요청 전달 (filterChain.doFilter)
    * */

    private final JwtProvider jwtProvider;

    /*
    * 이 필터는 모든 HTTP 요청마다 실행된다.
    * JWT 토큰이 포함되어 있으면 인증 처리까지 수행함
    * */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 1. 요청 헤더에서 토큰을 추출함
        String token = resolveToken(request);

        // 2. 토큰이 존재하고 유효성 검사를 통과하면
        if(token != null && jwtProvider.validateToken(token)){
            // 3. 토큰에서 사용자 정보 추출
            String username = jwtProvider.getUsername(token);

            // 4. 인증 객체 생성 (사용자 이름, 인증 정보 null, 권한 정보 없음)
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    username, // 인증된 사용자 정보 (username 또는 userDetails 객체)
                    null, // 비밀번호 또는 인증 정보 (JWT 사용 시 비밀번호가 필요 없으므로 null 로 둔다)
                    List.of() // 사용자의 권한 목록
            );

            // 5. 현재 요청의 SecurityContext에 인증 객체 등록
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        // 6. 다음 필터 체인으로 요청 전달
        filterChain.doFilter(request, response);
    }

    /*
    * HTTP 요청 헤더에서 JWT 토큰을 추출
    * @param request 현재 HTTP 요청
    * @return "Bearer " 접두어를 제거한 JWT 토큰
    * */
    private String resolveToken(HttpServletRequest request){
        String header = request.getHeader("Authorization");
        if(header != null && header.startsWith("Bearer ")){
            return header.substring(7);
        }
        return null;
    }
}
