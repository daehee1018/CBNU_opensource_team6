package opensource_project_team6.recommend_diet.domain.food.dto;

import lombok.Getter;
import lombok.AllArgsConstructor;
import opensource_project_team6.recommend_diet.domain.food.entity.Food;


@Getter
@AllArgsConstructor
public class FoodResponseDto {
    private Long id;
    private String name;
    private String imageUrl;
    private int calories;
    private float carbohydrate;
    private float protein;
    private float fat;

    public static FoodResponseDto from(Food food) {
        return new FoodResponseDto(
                food.getId(),
                food.getName(),
                food.getImageUrl(),
                food.getCalories(),
                food.getCarbohydrate(),
                food.getProtein(),
                food.getFat()
        );
    }
}
