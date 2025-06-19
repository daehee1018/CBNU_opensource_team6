package opensource_project_team6.recommend_diet.domain.myPage.dto;

import java.time.LocalDate;

public record MyPageResponse(
        String name,
        String gender,
        LocalDate birthDate,
        int age,
        Integer height,
        Double weight,
        Double targetWeight,
        String profileImageUrl,
        long followerCount,
        long follwingCount
) {}
