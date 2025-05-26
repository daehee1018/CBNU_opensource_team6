package opensource_project_team6.recommend_diet.domain.food.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;          // 음식 이름
    private int calories;         // 칼로리
    private String imageUrl;      // 이미지 URL

    private float protein;        // 단백질(g)
    private float fat;            // 지방(g)
    private float carbohydrate;   // 탄수화물(g)
}
