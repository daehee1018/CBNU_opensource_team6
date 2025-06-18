package opensource_project_team6.recommend_diet.domain.food.repository;

import opensource_project_team6.recommend_diet.domain.food.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FoodRepository extends JpaRepository<Food, Long> {
    List<Food> findByNameContaining(String keyword); // 특정 키워드가 포함된 음식을 검색 ex) "제육" 검색 -> 제육볶음, 제육덮밥 반환
    Optional<Food> findByName(String name); // 음식 이름으로 정확히 검색
    List<Food> findBySubcategory(String subcategory);
}
