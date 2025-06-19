package opensource_project_team6.recommend_diet.domain.diet.dto;

import lombok.Builder;
import lombok.Getter;
import opensource_project_team6.recommend_diet.domain.diet.entity.MealTime;

import java.time.LocalDate;

@Getter
@Builder
public class DietResponseDTO {
    private String foodName;
    private String imageUrl;
    private Double amount;
    private MealTime mealTime;
    private LocalDate date;

    private Integer energy; //칼로리
    private Double protein;
    private Double fat;
    private Double carbohydrate;
    private Double sugar;
    private Double sodium;
    private Double cholesterol;
    private Double saturatedFat;
}
