package opensource_project_team6.recommend_diet.domain.food.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodSimpleDTO {
    private Long id;
    private String name;
    private String imageUrl;
    private Integer energy;
}
