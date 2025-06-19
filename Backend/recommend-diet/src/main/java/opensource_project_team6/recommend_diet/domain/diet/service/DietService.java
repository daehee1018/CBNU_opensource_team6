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
                .sugar(food.getSugar() * ratio)
                .sodium(food.getSodium() * ratio)
                .cholesterol(food.getCholesterol() * ratio)
                .saturatedFat(food.getSaturatedFat() * ratio)
                .build();

        dietRepository.save(diet);
    }

    public Food saveDietByImage(MultipartFile image) throws IOException {
        log.info("[DietService] saveDietByImage 호출");
        Food food = openAIService.analyzeFoodImage(image);
        log.info("[DietService] 분석된 음식: {}", food.getName());
        return food;
    }

    public Food previewDietImage(MultipartFile image) throws IOException {
        log.info("[DietService] previewDietImage 호출");
        return openAIService.previewFoodImage(image);
    }

    public List<DietResponseDTO> getDietByDateAndMeal(User user, MealTime mealTime, LocalDate date) {
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
                        .sugar(round(d.getSugar()))
                        .sodium(round(d.getSodium()))
                        .cholesterol(round(d.getCholesterol()))
                        .saturatedFat(round(d.getSaturatedFat()))
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

        if (carbRatio > 1) carbRatio /= 100.0;
        if (proteinRatio > 1) proteinRatio /= 100.0;
        if (fatRatio > 1) fatRatio /= 100.0;

        double recommendedEnergy = calculateBmr(user);
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

        if (totalCarb + totalProtein + totalFat == 0) {
            return new DietScoreResponse(0, 0, 0, 0, "데이터가 없습니다.");
        }

        double carbRatio = user.getTargetCarbRatio() != null ? user.getTargetCarbRatio() : 0;
        double proteinRatio = user.getTargetProteinRatio() != null ? user.getTargetProteinRatio() : 0;
        double fatRatio = user.getTargetFatRatio() != null ? user.getTargetFatRatio() : 0;

        if (carbRatio > 1) carbRatio /= 100.0;
        if (proteinRatio > 1) proteinRatio /= 100.0;
        if (fatRatio > 1) fatRatio /= 100.0;

        double bmr = calculateBmr(user);
        double recCarb = bmr * carbRatio / 4.0;
        double recProtein = bmr * proteinRatio / 4.0;
        double recFat = bmr * fatRatio / 9.0;

        int carbScore = calcScoreByRatio(totalCarb, recCarb);
        int proteinScore = calcScoreByRatio(totalProtein, recProtein);
        int fatScore = calcScoreByRatio(totalFat, recFat);

        int finalScore = (carbScore + proteinScore + fatScore) / 3;
        String message = getMessage(finalScore);

        return new DietScoreResponse(carbScore, proteinScore, fatScore, finalScore, message);
    }

    private int calcScoreByRatio(double actual, double recommended) {
        if (recommended == 0) return 0;
        double ratio = actual / recommended;
        double score = 100 - Math.abs(ratio - 1) * 100;
        return (int) Math.max(0, Math.round(score));
    }

    private String getMessage(int score) {
        if (score >= 90) return "최고예요! 계속 유지하세요!";
        if (score >= 70) return "좋아요! 조금만 더 노력해보세요!";
        if (score >= 50) return "노력이 필요해요! 힘내세요!";
        return "식단 개선이 필요합니다! 화이팅!";
    }

    private double calculateBmr(User user) {
        double weight = user.getWeight() != null ? user.getWeight() : 0.0;
        double height = user.getHeight() != null ? user.getHeight() : 0.0;
        int age = java.time.Period.between(user.getBirthDate(), LocalDate.now()).getYears();
        boolean male = user.getGender() != null && user.getGender().contains("남");
        double genderConst = male ? 5.0 : -161.0;
        return 10 * weight + 6.25 * height - 5 * age + genderConst;
    }

    private double parseAmount(String standardAmount) {
        return Double.parseDouble(standardAmount.replaceAll("[^\\d.]", ""));
    }

    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}