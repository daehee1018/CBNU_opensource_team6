package opensource_project_team6.recommend_diet.domain.food.controller;

import lombok.RequiredArgsConstructor;
import opensource_project_team6.recommend_diet.domain.food.dto.FoodDTO;
import opensource_project_team6.recommend_diet.domain.food.dto.FoodSimpleDTO;
import opensource_project_team6.recommend_diet.domain.food.service.FoodService;
import opensource_project_team6.recommend_diet.global.util.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/food")
@RequiredArgsConstructor
public class FoodController {
    private final FoodService foodService;

    /**
     * 음식 이름 키워드로 검색
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchFoods(@RequestParam String keyword) {
        List<FoodSimpleDTO> result = foodService.searchFoods(keyword);
        Map<String, Object> response = new HashMap<>();

        if (result.isEmpty()) {
            response.put("status", 404);
            response.put("message", "검색 결과가 없습니다.");
            return ResponseEntity.status(404).body(response);
        }

        response.put("status", 200);
        response.put("message", "음식 검색 성공");
        response.put("data", result);
        return ResponseEntity.ok(response);
    }

    /*@PostMapping("/ai-recognized")
    public ResponseEntity<?> addRecognizedFood(@RequestBody Map<String, Object> payload) {
        try {
            String name = (String) payload.get("name");
            double energy = Double.parseDouble(payload.get("energy").toString());
            double carbohydrate = Double.parseDouble(payload.get("carbohydrate").toString());
            double protein = Double.parseDouble(payload.get("protein").toString());
            double fat = Double.parseDouble(payload.get("fat").toString());
            double sugar = Double.parseDouble(payload.get("sugar").toString());
            double sodium = Double.parseDouble(payload.get("sodium").toString());
            double cholesterol = Double.parseDouble(payload.get("cholesterol").toString());
            double saturatedFat = Double.parseDouble(payload.get("saturatedFat").toString());

            FoodDTO dto = foodService.addRecognizedFood(name, energy, carbohydrate, protein, fat,
                    sugar, sodium, cholesterol, saturatedFat);
            return ResponseEntity.ok(Map.of(
                    "message", "AI 인식 음식 추가 완료",
                    "data", dto
            ));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("message", "요청 파라미터 오류", "error", e.getMessage()));
        }
    }*/

    /**
     * 음식 상세 정보 조회 (food_id로)
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getFoodById(@PathVariable Long id) {
        try {
            FoodDTO food = foodService.getFoodById(id);
            Map<String, Object> response = new HashMap<>();

            if (food == null) {
                response.put("status", 404);
                response.put("message", "해당 음식 정보를 찾을 수 없습니다.");
                return ResponseEntity.status(404).body(response);
            }

            response.put("status", 200);
            response.put("message", "음식 상세 조회 성공");
            response.put("data", food);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", 500);
            error.put("message", "음식 상세 조회 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * 오늘의 추천 식단 조회
     */
    @GetMapping("/today")
    public ResponseEntity<?> getTodayFoods() {
        Map<String, List<FoodDTO>> meals = foodService.getTodayFoods();
        return ResponseEntity.ok(meals);
    }

    @GetMapping("/ingredient")
    public ResponseEntity<?> searchFoodsByIngredient(@RequestParam String keyword) {

        List<FoodSimpleDTO> result = foodService.searchFoodsByIngredient(keyword);
        Map<String, Object> response = new HashMap<>();

        if (result.isEmpty()) {
            response.put("status", 404);
            response.put("message", "해당 식재료를 이용한 음식 데이터가 없습니다.");
            return ResponseEntity.status(404).body(response);
        }

        response.put("status", 200);
        response.put("message", "식재료 검색 성공");
        response.put("data", result);
        return ResponseEntity.ok(response);
    }
}
