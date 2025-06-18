package opensource_project_team6.recommend_diet.domain.myPage.dto;

public record DietScoreResponse(
        double carbScore,
        double proteinScore,
        double fatScore,
        double finalScore,
        String message
) {}
