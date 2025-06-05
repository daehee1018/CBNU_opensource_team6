package opensource_project_team6.recommend_diet.domain.diet.service;

import lombok.RequiredArgsConstructor;
import opensource_project_team6.recommend_diet.domain.diet.dto.DietRequestDTO;
import opensource_project_team6.recommend_diet.domain.diet.dto.DietResponseDTO;
import opensource_project_team6.recommend_diet.domain.diet.entity.Diet;
import opensource_project_team6.recommend_diet.domain.diet.entity.MealTime;
import opensource_project_team6.recommend_diet.domain.diet.repository.DietRepository;
import opensource_project_team6.recommend_diet.domain.food.entity.Food;
import opensource_project_team6.recommend_diet.domain.food.repository.FoodRepository;
import opensource_project_team6.recommend_diet.domain.user.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DietService {
    private final DietRepository dietRepository;
    private final FoodRepository foodRepository;

    public void saveDiet(DietRequestDTO dto, User user) {
        Food food = foodRepository.findById(dto.getFoodId())
                .orElseThrow(() -> new IllegalArgumentException("해당 음식이 존재하지 않습니다."));

        double ratio = dto.getAmount() / parseAmount(food.getStandardAmount());

        Diet diet = Diet.builder()
                .user(user)
                .food(food)
                .amount(dto.getAmount())
                .mealTime(dto.getMealTime())
                .date(dto.getDate())
                .energy((int) Math.round(food.getEnergy() * ratio))
                .protein(food.getProtein() * ratio)
                .fat(food.getFat() * ratio)
                .carbohydrate(food.getCarbohydrate() * ratio)
                .build();

        dietRepository.save(diet);
    }

    public List<DietResponseDTO> getDietByDateAndMeal(User user, MealTime mealTime, java.time.LocalDate date) {
        List<Diet> dietList = dietRepository.findWithFoodByUserAndDateAndMealTime(user, date, mealTime);

        return dietList.stream().map(d -> DietResponseDTO.builder()
                .foodName(d.getFood().getName())
                .imageUrl(d.getFood().getImageUrl())
                .amount(d.getAmount())
                .mealTime(d.getMealTime())
                .date(d.getDate())
                .energy(d.getEnergy())
                .protein(d.getProtein())
                .fat(d.getFat())
                .carbohydrate(d.getCarbohydrate())
                .build()).collect(Collectors.toList());
    }


    private double parseAmount(String standardAmount) {
        return Double.parseDouble(standardAmount.replaceAll("[^\\d.]", ""));
    }
}
