package opensource_project_team6.recommend_diet.domain.food.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FoodDto {
    private String name;
    private String imageUrl;
    private int calories;
    private float carbohydrate;
    private float protein;
    private float fat;
}
