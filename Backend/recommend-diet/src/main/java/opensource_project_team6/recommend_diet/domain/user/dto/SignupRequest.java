package opensource_project_team6.recommend_diet.domain.user.dto;

import opensource_project_team6.recommend_diet.domain.user.entity.Gender;
import opensource_project_team6.recommend_diet.domain.user.entity.HealthConcern;
import opensource_project_team6.recommend_diet.domain.user.entity.Interest;

import java.time.LocalDate;

public record SignupRequest(
        String name,       // 이름
        String email,            // 이메일 (로그인 ID)
        String password,         // 비밀번호
        String passwordConfirm,  // 비밀번호 확인 (서버 검증 or 프론트 확인)
        LocalDate birthDate,      // 생년월일
        Double height,
        Double weight,
        Double targetWeight,
        Gender gender,              // 성별
        Interest interest,          // 관심사
        HealthConcern healthConcern // 건강 고민

) {}
