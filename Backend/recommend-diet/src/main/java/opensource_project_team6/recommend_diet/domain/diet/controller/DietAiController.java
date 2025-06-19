package opensource_project_team6.recommend_diet.domain.diet.controller;

import lombok.RequiredArgsConstructor;
import opensource_project_team6.recommend_diet.domain.diet.entity.MealTime;
import opensource_project_team6.recommend_diet.domain.diet.service.DietAiService;
import opensource_project_team6.recommend_diet.global.util.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diet")
public class DietAiController {
    private final DietAiService dietAiService;

    @PostMapping("/add")
    public ResponseEntity<?> addDietFromAi(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                           @RequestBody Map<String, Object> payload) {

        String name = (String) payload.get("name");
        double energy = Double.parseDouble(payload.get("energy").toString());
        double carbohydrate = Double.parseDouble(payload.get("carbohydrate").toString());
        double protein = Double.parseDouble(payload.get("protein").toString());
        double fat = Double.parseDouble(payload.get("fat").toString());
        double sugar = Double.parseDouble(payload.get("sugar").toString());
        double sodium = Double.parseDouble(payload.get("sodium").toString());
        double cholesterol = Double.parseDouble(payload.get("cholesterol").toString());
        double saturatedFat = Double.parseDouble(payload.get("saturatedFat").toString());
        String mealTimeStr = (String) payload.get("mealTime");
        double amount = Double.parseDouble(payload.get("amount").toString());
        LocalDate date = LocalDate.parse((String) payload.get("date"));

        MealTime mealTime = MealTime.valueOf(mealTimeStr);

        dietAiService.addDietFromAi(userPrincipal.getId(), name, energy, carbohydrate, protein, fat, sugar, sodium, cholesterol, saturatedFat, mealTime, amount, date);

        return ResponseEntity.ok(Map.of("message", "AI 인식 결과로 식단 등록 성공"));
    }
}
