package opensource_project_team6.recommend_diet.domain.food.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "food")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Food {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "food_id")
    private Long id;

    private String name; // 음식 이름
    private String category;
    private String repName;
    private String subcategory;
    private String standardAmount;
    private String imageUrl;

    private Integer energy; //칼로리
    private Double protein;
    private Double fat;
    private Double carbohydrate;
    private Double sugar;
    private Double sodium;
    private Double cholesterol;
    private Double saturatedFat;

}
