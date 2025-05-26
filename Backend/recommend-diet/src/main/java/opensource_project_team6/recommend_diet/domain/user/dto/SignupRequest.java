package opensource_project_team6.recommend_diet.domain.user.dto;

import java.time.LocalDate;

public record SignupRequest(
        String lastName,         // 성
        String firstName,        // 이름
        String email,            // 이메일 (로그인 ID)
        String password,         // 비밀번호
        String passwordConfirm,  // 비밀번호 확인 (서버 검증 or 프론트 확인)
        LocalDate birthDate      // 생년월일
) {}
