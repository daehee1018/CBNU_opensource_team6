package opensource_project_team6.recommend_diet.domain.food.service;

import lombok.RequiredArgsConstructor;
import opensource_project_team6.recommend_diet.domain.food.dto.FoodRequestDto;
import opensource_project_team6.recommend_diet.domain.food.dto.FoodResponseDto;
import opensource_project_team6.recommend_diet.domain.food.entity.Food;
import opensource_project_team6.recommend_diet.domain.food.repository.FoodRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodService {

    private final FoodRepository foodRepository;

    public List<FoodResponseDto> getAllFoods() {
        return foodRepository.findAll().stream()
                .map(FoodResponseDto::from)
                .collect(Collectors.toList());
    }

    public FoodResponseDto addFood(FoodRequestDto dto) {
        Food food = new Food();
        food.setName(dto.getName());
        food.setImageUrl(dto.getImageUrl());
        food.setCalories(dto.getCalories());
        food.setCarbohydrate(dto.getCarbohydrate());
        food.setProtein(dto.getProtein());
        food.setFat(dto.getFat());

        Food saved = foodRepository.save(food);
        return FoodResponseDto.from(saved);
    }
}
