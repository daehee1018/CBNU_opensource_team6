package opensource_project_team6.recommend_diet.domain.food.repository;

import opensource_project_team6.recommend_diet.domain.food.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRepository extends JpaRepository<Food, Long> {
    Food findByName(String name); // 선택: 이름으로 조회
}
