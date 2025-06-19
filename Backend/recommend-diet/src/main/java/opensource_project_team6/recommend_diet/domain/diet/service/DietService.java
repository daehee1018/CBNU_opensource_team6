package opensource_project_team6.recommend_diet.domain.diet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import opensource_project_team6.recommend_diet.domain.diet.dto.DietRequestDTO;
import opensource_project_team6.recommend_diet.domain.diet.dto.DietResponseDTO;
import opensource_project_team6.recommend_diet.domain.diet.entity.Diet;
import opensource_project_team6.recommend_diet.domain.diet.entity.MealTime;
import opensource_project_team6.recommend_diet.domain.diet.repository.DietRepository;
import opensource_project_team6.recommend_diet.domain.food.entity.Food;
import opensource_project_team6.recommend_diet.domain.food.repository.FoodRepository;
import opensource_project_team6.recommend_diet.domain.food.service.OpenAIService;
import opensource_project_team6.recommend_diet.domain.user.entity.User;
import opensource_project_team6.recommend_diet.domain.myPage.dto.DietScoreResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DietService {
    private final DietRepository dietRepository;
    private final FoodRepository foodRepository;
    private final OpenAIService openAIService;

    public void saveDiet(DietRequestDTO dto, User user) {
        Food food = null;
        if (dto.getFoodId() != null) {
            food = foodRepository.findById(dto.getFoodId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 음식이 존재하지 않습니다."));
        } else if (dto.getFoodName() != null) {
            food = foodRepository.findByName(dto.getFoodName())
                    .orElseThrow(() -> new IllegalArgumentException("해당 음식이 존재하지 않습니다."));
        } else {
            throw new IllegalArgumentException("음식 정보가 필요합니다.");
        }

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

    public void saveDietByImage(MultipartFile image, MealTime mealTime, LocalDate date, User user) throws IOException {
        log.info("[DietService] saveDietByImage 호출 - userId: {}, mealTime: {}, date: {}", user.getId(), mealTime, date);
        Food food = openAIService.analyzeFoodImage(image);
        log.info("[DietService] 분석된 음식: {}", food.getName());

        Diet diet = Diet.builder()
                .user(user)
                .food(food)
                .amount(parseAmount(food.getStandardAmount()))
                .mealTime(mealTime)
                .date(date)
                .energy(food.getEnergy())
                .protein(food.getProtein())
                .fat(food.getFat())
                .carbohydrate(food.getCarbohydrate())
                .build();

        dietRepository.save(diet);
        log.info("[DietService] 식단 저장 완료. id={}", diet.getId());
    }

    public Food previewDietImage(MultipartFile image) throws IOException {
        log.info("[DietService] previewDietImage 호출");
        return openAIService.previewFoodImage(image);
    }

    public List<DietResponseDTO> getDietByDateAndMeal(User user, MealTime mealTime, java.time.LocalDate date) {
        List<Diet> dietList = dietRepository.findWithFoodByUserAndDateAndMealTime(user, date, mealTime);

        return dietRepository.findWithFoodByUserAndDateAndMealTime(user, date, mealTime).stream()
                .map(d -> DietResponseDTO.builder()
                        .foodName(d.getFood().getName())
                        .imageUrl(d.getFood().getImageUrl())
                        .amount(round(d.getAmount()))
                        .mealTime(d.getMealTime())
                        .date(d.getDate())
                        .energy(d.getEnergy())
                        .protein(round(d.getProtein()))
                        .fat(round(d.getFat()))
                        .carbohydrate(round(d.getCarbohydrate()))
                        .build())
                .collect(Collectors.toList());
    }

    public Map<String, Object> getTotalNutrientsByDate(User user, LocalDate date) {
        List<Diet> allMeals = dietRepository.findAllWithFoodByUserAndDate(user, date);

        double totalCarb = 0, totalProtein = 0, totalFat = 0, totalSodium = 0;
        int totalEnergy = 0;

        for (Diet d : allMeals) {
            totalCarb += d.getCarbohydrate();
            totalProtein += d.getProtein();
            totalFat += d.getFat();
            totalSodium += d.getFood().getSodium() != null ? d.getFood().getSodium() : 0;
            totalEnergy += d.getEnergy();
        }

        double carbRatio = user.getTargetCarbRatio() != null ? user.getTargetCarbRatio() : 0;
        double proteinRatio = user.getTargetProteinRatio() != null ? user.getTargetProteinRatio() : 0;
        double fatRatio = user.getTargetFatRatio() != null ? user.getTargetFatRatio() : 0;

        double recommendedEnergy = user.getTargetWeight() * 30.0;
        double recCarb = recommendedEnergy * carbRatio / 4.0;
        double recProtein = recommendedEnergy * proteinRatio / 4.0;
        double recFat = recommendedEnergy * fatRatio / 9.0;

        Map<String, Object> result = new HashMap<>();
        result.put("date", date);
        result.put("totalCarbohydrate", round(totalCarb));
        result.put("totalProtein", round(totalProtein));
        result.put("totalFat", round(totalFat));
        result.put("totalSodium", round(totalSodium));
        result.put("totalEnergy", totalEnergy);
        result.put("recommendedCarbohydrate", round(recCarb));
        result.put("recommendedProtein", round(recProtein));
        result.put("recommendedFat", round(recFat));
        return result;
    }

    public DietScoreResponse getDietScore(User user, LocalDate date) {
        List<Diet> allMeals = dietRepository.findAllWithFoodByUserAndDate(user, date);

        double totalCarb = 0, totalProtein = 0, totalFat = 0;
        for (Diet d : allMeals) {
            totalCarb += d.getCarbohydrate();
            totalProtein += d.getProtein();
            totalFat += d.getFat();
        }

        double total = totalCarb + totalProtein + totalFat;
        if (total == 0) {
            return new DietScoreResponse(0, 0, 0, 0, "데이터가 없습니다.");
        }

        double actualCarbRatio = totalCarb / total;
        double actualProteinRatio = totalProtein / total;
        double actualFatRatio = totalFat / total;

        double targetCarbRatio = user.getTargetCarbRatio() != null ? user.getTargetCarbRatio() : 0;
        double targetProteinRatio = user.getTargetProteinRatio() != null ? user.getTargetProteinRatio() : 0;
        double targetFatRatio = user.getTargetFatRatio() != null ? user.getTargetFatRatio() : 0;

        double carbScore = calcScore(actualCarbRatio, targetCarbRatio);
        double proteinScore = calcScore(actualProteinRatio, targetProteinRatio);
        double fatScore = calcScore(actualFatRatio, targetFatRatio);

        double finalScore = (carbScore + proteinScore + fatScore) / 3.0;
        String message = getMessage(finalScore);

        return new DietScoreResponse(round(carbScore), round(proteinScore), round(fatScore), round(finalScore), message);
    }

    private double calcScore(double actual, double target) {
        double diff = Math.abs(actual - target);
        double score = (1.0 - diff) * 100.0;
        return Math.max(score, 0.0);
    }

    private String getMessage(double score) {
        if (score >= 90) return "최고예요! 계속 유지하세요!";
        if (score >= 70) return "좋아요! 조금만 더 노력해보세요!";
        if (score >= 50) return "노력이 필요해요! 힘내세요!";
        return "식단 개선이 필요합니다! 화이팅!";
    }


    private double parseAmount(String standardAmount) {
        return Double.parseDouble(standardAmount.replaceAll("[^\\d.]", ""));
    }

    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
