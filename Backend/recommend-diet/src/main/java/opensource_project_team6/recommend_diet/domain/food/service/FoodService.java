package opensource_project_team6.recommend_diet.domain.food.service;

import lombok.RequiredArgsConstructor;
import opensource_project_team6.recommend_diet.domain.food.dto.FoodDTO;
import opensource_project_team6.recommend_diet.domain.food.dto.FoodSimpleDTO;
import opensource_project_team6.recommend_diet.domain.food.entity.Food;
import opensource_project_team6.recommend_diet.domain.food.repository.FoodRepository;
import org.springframework.stereotype.Service;

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
        return foodRepository.findByNameContaining(keyword).stream()
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

}
