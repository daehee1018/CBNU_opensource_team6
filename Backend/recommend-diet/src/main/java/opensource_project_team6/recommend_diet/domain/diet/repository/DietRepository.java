package opensource_project_team6.recommend_diet.domain.diet.repository;

import opensource_project_team6.recommend_diet.domain.diet.entity.Diet;
import opensource_project_team6.recommend_diet.domain.diet.entity.MealTime;
import opensource_project_team6.recommend_diet.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DietRepository extends JpaRepository<Diet, Long> {
    @Query("SELECT d FROM Diet d JOIN FETCH d.food WHERE d.user = :user AND d.date = :date AND d.mealTime = :mealTime")
    List<Diet> findWithFoodByUserAndDateAndMealTime(@Param("user") User user,
                                                    @Param("date") LocalDate date,
                                                    @Param("mealTime") MealTime mealTime);
}
