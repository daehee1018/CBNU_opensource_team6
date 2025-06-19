package opensource_project_team6.recommend_diet.domain.food.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import opensource_project_team6.recommend_diet.domain.food.entity.Food;
import opensource_project_team6.recommend_diet.domain.food.repository.FoodRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAIService {
    private final FoodRepository foodRepository;

    @Value("${openai.api.key:}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public Food analyzeFoodImage(MultipartFile file) throws IOException {
        log.info("[OpenAIService] analyzeFoodImage 호출");
        Food food = previewFoodImage(file);
        Food saved = foodRepository.save(food);
        log.info("[OpenAIService] 음식 저장 완료. id={}", saved.getId());
        return saved;
    }

    public Food previewFoodImage(MultipartFile file) throws IOException {
        log.info("[OpenAIService] previewFoodImage 호출");
        byte[] bytes = file.getBytes();
        String base64 = Base64.getEncoder().encodeToString(bytes);

        Map<String, Object> imageUrl = Map.of("url", "data:image/jpeg;base64," + base64);
        Map<String, Object> userContent = Map.of(
                "role", "user",
                "content", List.of(
                        Map.of("type", "text", "text", """
                                사진 속 음식의 이름과 열량(kcal), 탄수화물(g), 단백질(g), 지방(g)을 JSON 형식으로 알려줘.
                                마크다운 코드블럭 없이, 아래 형식 그대로 순수 JSON으로만 응답해줘.

                                {
                                  "name": "음식 이름",
                                  "energy": 숫자,
                                  "carbohydrate": 숫자,
                                  "protein": 숫자,
                                  "fat": 숫자
                                }
                                """),
                        Map.of("type", "image_url", "image_url", imageUrl)
                )
        );

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-4o");
        body.put("messages", Collections.singletonList(userContent));
        body.put("max_tokens", 300);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        log.debug("[OpenAIService] 요청 바디: {}", body);
        String response;
        try {
            response = restTemplate.postForObject(
                    "https://api.openai.com/v1/chat/completions",
                    request,
                    String.class
            );
        } catch (Exception e) {
            log.error("[OpenAIService] OpenAI API 호출 실패", e);
            throw e;
        }

        log.debug("[OpenAIService] 응답: {}", response);

        if (response == null) {
            throw new IOException("OpenAI response is null");
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response);
        String content = root.path("choices").get(0).path("message").path("content").asText();

        // 혹시 마크다운(```json\n...\n```) 형식으로 왔을 경우 제거
        if (content.startsWith("```")) {
            int first = content.indexOf('\n');
            if (first != -1) content = content.substring(first + 1);
            int last = content.lastIndexOf("```");
            if (last != -1) content = content.substring(0, last);
        }

        JsonNode result = mapper.readTree(content.trim());
        log.debug("[OpenAIService] 파싱 결과: {}", result.toString());

        Food food = Food.builder()
                .name(result.path("name").asText())
                .standardAmount("1 serving")
                .energy(result.path("energy").asInt())
                .carbohydrate(result.path("carbohydrate").asDouble())
                .protein(result.path("protein").asDouble())
                .fat(result.path("fat").asDouble())
                .build();

        return food;
    }
}
