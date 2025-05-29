package opensource_project_team6.recommend_diet.domain.user.service;

import opensource_project_team6.recommend_diet.domain.user.dto.AdditionalInfoRequest;
import opensource_project_team6.recommend_diet.domain.user.dto.LoginRequest;
import opensource_project_team6.recommend_diet.domain.user.dto.LoginResponse;
import opensource_project_team6.recommend_diet.domain.user.dto.SignupRequest;
import opensource_project_team6.recommend_diet.domain.user.repository.UserRepository;


public interface AuthService {

    /*
    *
    * */

    /*
    * @param request 회원가입 요청 DTO
    * */
    void signup(SignupRequest request);

    /*
    * @param request 로그인 요청 DTO
    * @return 발급된 JWT 토큰
    * */
    LoginResponse login(LoginRequest request);

    /*
    * @param token 클라이언트가 전달한 토큰
    * */
    /*void logout(String token);*/

    void updateAdditionalInfo(Long userId, AdditionalInfoRequest dto);

}
