package opensource_project_team6.recommend_diet.domain.food.service;

import lombok.RequiredArgsConstructor;
import opensource_project_team6.recommend_diet.domain.food.dto.FoodDTO;
import opensource_project_team6.recommend_diet.domain.food.dto.FoodSimpleDTO;
import opensource_project_team6.recommend_diet.domain.food.entity.Food;
import opensource_project_team6.recommend_diet.domain.food.repository.FoodRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodService {

    private final FoodRepository foodRepository;

    /**
     * 음식 키워드 검색
     */
    public List<FoodSimpleDTO> searchFoods(String keyword) {
        return foodRepository.findByName(keyword).stream()
                .map(food -> FoodSimpleDTO.builder()
                        .id(food.getId())
                        .name(food.getName())
                        .imageUrl(food.getImageUrl())
                        .energy(food.getEnergy())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 음식 상세 정보 조회
     */
    public FoodDTO getFoodById(Long id) {
        Optional<Food> food = foodRepository.findById(id);
        return food.map(this::convertToDTO).orElse(null);
    }

    /**
     * 오늘의 추천 식단 (임시 데이터)
     */
    public Map<String, List<FoodDTO>> getTodayFoods() {
        List<Food> foods = foodRepository.findAll(PageRequest.of(0, 9)).getContent();
        List<FoodDTO> dtos = foods.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Map<String, List<FoodDTO>> result = new HashMap<>();
        result.put("breakfast", dtos.subList(0, Math.min(3, dtos.size())));
        result.put("lunch", dtos.subList(Math.min(3, dtos.size()), Math.min(6, dtos.size())));
        result.put("dinner", dtos.subList(Math.min(6, dtos.size()), dtos.size()));
        return result;
    }

    private FoodDTO convertToDTO(Food food) {
        return FoodDTO.builder()
                .id(food.getId())
                .name(food.getName())
                .category(food.getCategory())
                .repName(food.getRepName())
                .subcategory(food.getSubcategory())
                .standardAmount(food.getStandardAmount())
                .imageUrl(food.getImageUrl())
                .energy(food.getEnergy())
                .protein(food.getProtein())
                .fat(food.getFat())
                .carbohydrate(food.getCarbohydrate())
                .sugar(food.getSugar())
                .sodium(food.getSodium())
                .cholesterol(food.getCholesterol())
                .saturatedFat(food.getSaturatedFat())
                .build();
    }

    public List<FoodSimpleDTO> searchFoodsByIngredient(String ingredient) {
        List<Food> foods = foodRepository.findBySubcategory(ingredient);
        return foods.stream()
                .map(food -> FoodSimpleDTO.builder()
                        .id(food.getId())
                        .name(food.getName())
                        .imageUrl(food.getImageUrl())
                        .energy(food.getEnergy())
                        .build())
                .collect(Collectors.toList());
    }
}
