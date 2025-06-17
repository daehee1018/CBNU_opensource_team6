package opensource_project_team6.recommend_diet.domain.user.dto;

import opensource_project_team6.recommend_diet.domain.user.entity.Gender;
import opensource_project_team6.recommend_diet.domain.user.entity.HealthConcern;
import opensource_project_team6.recommend_diet.domain.user.entity.Interest;

import java.time.LocalDate;

public record SignupRequest(
        String name,       // 이름
        String email,            // 이메일 (로그인 ID)
        String password,         // 비밀번호
        LocalDate birthDate,      // 생년월일
        Double height,
        Double weight,
        Double targetWeight,
        String gender,              // 성별
        String interest,          // 관심사
        String healthConcern // 건강 고민

) {}
