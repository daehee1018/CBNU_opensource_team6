package opensource_project_team6.recommend_diet.domain.food.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodDTO {
    private Long id;
    private String name;
    private String category;
    private String repName;
    private String subcategory;
    private String standardAmount;
    private String imageUrl;

    private Integer energy;
    private Double protein;
    private Double fat;
    private Double carbohydrate;
    private Double sugar;
    private Double sodium;
    private Double cholesterol;
    private Double saturatedFat;
}
