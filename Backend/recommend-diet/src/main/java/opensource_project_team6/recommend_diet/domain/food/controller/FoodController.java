package opensource_project_team6.recommend_diet.domain.food.controller;

import lombok.RequiredArgsConstructor;
import opensource_project_team6.recommend_diet.domain.food.dto.FoodDTO;
import opensource_project_team6.recommend_diet.domain.food.dto.FoodSimpleDTO;
import opensource_project_team6.recommend_diet.domain.food.service.FoodService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
}
