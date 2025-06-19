package opensource_project_team6.recommend_diet.domain.diet.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import opensource_project_team6.recommend_diet.domain.diet.dto.DietRequestDTO;
import opensource_project_team6.recommend_diet.domain.diet.entity.MealTime;
import opensource_project_team6.recommend_diet.domain.diet.service.DietService;
import opensource_project_team6.recommend_diet.domain.user.entity.User;
import opensource_project_team6.recommend_diet.domain.user.repository.UserRepository;
import opensource_project_team6.recommend_diet.global.util.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diet")
@Slf4j
public class DietContaroller {
    private final DietService dietService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> saveDiet(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                      @RequestBody DietRequestDTO dto) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        log.info("[DietController] /api/diet POST 호출 - userId: {}", user.getId());

        dietService.saveDiet(dto, user);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "식단이 성공적으로 저장되었습니다.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/image")
    public ResponseEntity<?> saveDietByImage(@RequestPart("image") MultipartFile image) throws java.io.IOException {
        log.info("[DietController] /api/diet/image POST 호출");
        var food = dietService.saveDietByImage(image);
        Map<String, Object> data = new HashMap<>();
        data.put("id", food.getId());
        data.put("name", food.getName());
        data.put("energy", food.getEnergy());
        data.put("carbohydrate", food.getCarbohydrate());
        data.put("protein", food.getProtein());
        data.put("fat", food.getFat());

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "사진 음식 저장 완료");
        response.put("data", data);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/image/preview")
    public ResponseEntity<?> previewDietImage(@RequestPart("image") MultipartFile image) throws java.io.IOException {
        log.info("[DietController] /api/diet/image/preview POST 호출");
        var food = dietService.previewDietImage(image);
        log.info("[DietController] 미리보기 결과: {}", food.getName());

        Map<String, Object> data = new HashMap<>();
        data.put("name", food.getName());
        data.put("energy", food.getEnergy());
        data.put("carbohydrate", food.getCarbohydrate());
        data.put("protein", food.getProtein());
        data.put("fat", food.getFat());

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "분석 성공");
        response.put("data", data);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> getDietByDateAndMeal(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                  @RequestParam("date") String dateStr,
                                                  @RequestParam("mealTime") MealTime mealTime) {

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        log.info("[DietController] /api/diet GET 호출 - userId: {}, date: {}, mealTime: {}",
                user.getId(), dateStr, mealTime);

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

    @GetMapping("/total")
    public ResponseEntity<?> getTotalNutrientsByDate(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                     @RequestParam("date") String dateStr) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        log.info("[DietController] /api/diet/total GET 호출 - userId: {}, date: {}", user.getId(), dateStr);

        LocalDate date = LocalDate.parse(dateStr);
        Map<String, Object> result = dietService.getTotalNutrientsByDate(user, date);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "총 섭취 영양소 조회 성공");
        response.put("data", result);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/score")
    public ResponseEntity<?> getDietScore(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                          @RequestParam("date") String dateStr) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        log.info("[DietController] /api/diet/score GET 호출 - userId: {}, date: {}", user.getId(), dateStr);

        LocalDate date = LocalDate.parse(dateStr);
        var result = dietService.getDietScore(user, date);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "점수 계산 성공");
        response.put("data", result);
        return ResponseEntity.ok(response);
    }
}
