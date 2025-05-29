package opensource_project_team6.recommend_diet.global.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {
    /*
    * JwtProvider는 JWT토큰을 생성, 검증, 필요한 정보를 추출하는 클래스이다.
    * 1. 로그인 요청
    * 2. username/password 확인
    * 3. 클라이언트에 토큰 반환
    * 4. 클라이언트가 요청 보낼 때 토큰 첨부
    * 5. 서버는 토큰에서 username 꺼내어 DB 조회 후 인증 처리
    * */

    // 환경변수에서 가져온 비밀키
    // 토큰이 위조되지 않았는지 확인하기 위해 필요
    @Value("${JWT_SECRET}")
    private String secretKey;

    // 토큰 만료 시간(단위는 밀리초)
    @Value("${JWT_EXPIRATION}")
    private long expiration;

    /*
    * JWT 토큰 생성
    * @param username 사용자 이름 또는 식별자
    * @return 서명된 JWT 문자열
    * */
    public String generateToken(String username, boolean isProfileComplete) {
        Date now = new Date(); // 현재 시간
        Date expiry = new Date(now.getTime() + expiration); // 만료 시간

        return Jwts.builder()
                .setSubject(username) // 토큰의 주체(보통 사용자 식별자)
                .claim("profileComplete", String.valueOf(isProfileComplete)) // 프론트에서 토큰을 파싱해서 isProfilecomplete값 확인
//                .claim("role", role) 사용자 권한에 따라 요청을 제안하고 싶은 경우
                .setIssuedAt(now) // 발급 시간
                .setExpiration(expiry) // 만료 시간
                .signWith(SignatureAlgorithm.HS256, secretKey) // 서명 + 비밀키
                .compact(); // 최종 JWT 문자열 생성
    }

    /*
    * JWT 토큰에서 사용자 이름 추출
    * @param token JWT 토큰
    * @return username (setSubject에 넣은 값)
    * 사용자 이름을 추출하여 해당 이름으로 DB에서 user를 조회 -> 인증된 사용자로 처리
    * */
    public String getUsername(String token){
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /*
    * 유효성 검사
    * @param 클라이언트가 보낸 JWT토큰
    * @return 유효하면 true 아니면 false
    * */
    public boolean validateToken(String token){
        try{
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token);
            return true;
        }catch(JwtException e){
            return false;
        }
    }
}
