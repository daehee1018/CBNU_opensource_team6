package opensource_project_team6.recommend_diet.domain.diet.dto;

import lombok.Getter;
import opensource_project_team6.recommend_diet.domain.diet.entity.MealTime;

import java.time.LocalDate;

@Getter
public class DietRequestDTO {
    private Long foodId;
    private Double amount;
    private MealTime mealTime;
    private LocalDate date;
}
