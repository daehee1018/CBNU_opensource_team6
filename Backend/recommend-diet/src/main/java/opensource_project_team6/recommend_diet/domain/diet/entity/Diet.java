package opensource_project_team6.recommend_diet.domain.diet.entity;

import jakarta.persistence.*;
import lombok.*;
import opensource_project_team6.recommend_diet.domain.food.entity.Food;
import opensource_project_team6.recommend_diet.domain.user.entity.User;

import java.time.LocalDate;

@Entity
@Table(name = "diet")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Diet {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diet_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id")
    private Food food;

    private Double amount;

    @Enumerated(EnumType.STRING)
    private MealTime mealTime;

    private LocalDate date;

    private Integer energy;
    private Double protein;
    private Double fat;
    private Double carbohydrate;

    private Double sugar;
    private Double sodium;
    private Double cholesterol;
    private Double saturatedFat;
}
