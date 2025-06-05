package opensource_project_team6.recommend_diet.domain.diet.controller;

import lombok.RequiredArgsConstructor;
import opensource_project_team6.recommend_diet.domain.diet.dto.DietRequestDTO;
import opensource_project_team6.recommend_diet.domain.diet.entity.MealTime;
import opensource_project_team6.recommend_diet.domain.diet.service.DietService;
import opensource_project_team6.recommend_diet.domain.user.entity.User;
import opensource_project_team6.recommend_diet.domain.user.repository.UserRepository;
import opensource_project_team6.recommend_diet.global.util.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diet")
public class DietContaroller {
    private final DietService dietService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> saveDiet(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                      @RequestBody DietRequestDTO dto) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        dietService.saveDiet(dto, user);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "식단이 성공적으로 저장되었습니다.");
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> getDietByDateAndMeal(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                  @RequestParam("date") String dateStr,
                                                  @RequestParam("mealTime") MealTime mealTime) {

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        LocalDate date = LocalDate.parse(dateStr);
        var result = dietService.getDietByDateAndMeal(user, mealTime, date);

        Map<String, Object> response = new HashMap<>();
        if (result.isEmpty()) {
            response.put("status", 404);
            response.put("message", "해당 날짜와 시간대의 식단이 없습니다.");
            return ResponseEntity.status(404).body(response);
        }

        response.put("status", 200);
        response.put("message", "식단 조회 성공");
        response.put("data", result);
        return ResponseEntity.ok(response);
    }
}
