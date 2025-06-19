package opensource_project_team6.recommend_diet.domain.diet.service;

import lombok.RequiredArgsConstructor;
import opensource_project_team6.recommend_diet.domain.diet.entity.Diet;
import opensource_project_team6.recommend_diet.domain.diet.entity.MealTime;
import opensource_project_team6.recommend_diet.domain.diet.repository.DietRepository;
import opensource_project_team6.recommend_diet.domain.food.entity.Food;
import opensource_project_team6.recommend_diet.domain.food.service.FoodService;
import opensource_project_team6.recommend_diet.domain.user.entity.User;
import opensource_project_team6.recommend_diet.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DietAiService {

    private final FoodService foodService;
    private final UserRepository userRepository;
    private final DietRepository dietRepository;

    public void addDietFromAi(Long userId, String name, double energy,
                              double carbohydrate, double protein, double fat,
                              double sugar, double sodium,
                              double cholesterol, double saturatedFat,
                              MealTime mealTime, double amount, LocalDate date) {
        Food food = foodService.addRecognizedFood(name, energy, carbohydrate, protein, fat,
                sugar, sodium, cholesterol, saturatedFat);
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        // amount 기준으로 실제 섭취량 계산
        int finalEnergy = (int) Math.round(energy * amount);
        double finalProtein = round(protein * amount);
        double finalFat = round(fat * amount);
        double finalCarbohydrate = round(carbohydrate * amount);

        Diet diet = Diet.builder()
                .user(user)
                .food(food)
                .mealTime(mealTime)
                .amount(amount)
                .date(date)
                .energy(finalEnergy)
                .protein(finalProtein)
                .fat(finalFat)
                .carbohydrate(finalCarbohydrate)
                .sugar(round(sugar * amount))
                .sodium(round(sodium * amount))
                .cholesterol(round(cholesterol * amount))
                .saturatedFat(round(saturatedFat * amount))
                .build();

        dietRepository.save(diet);
    }

    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
