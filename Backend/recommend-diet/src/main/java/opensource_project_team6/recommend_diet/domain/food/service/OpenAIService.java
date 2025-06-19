package opensource_project_team6.recommend_diet.domain.food.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
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
public class OpenAIService {
    private final FoodRepository foodRepository;

    @Value("${openai.api.key:}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public Food analyzeFoodImage(MultipartFile file) throws IOException {
        Food food = previewFoodImage(file);
        return foodRepository.save(food);
    }

    public Food previewFoodImage(MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        String base64 = Base64.getEncoder().encodeToString(bytes);

        Map<String, Object> imageUrl = Map.of("url", "data:image/jpeg;base64," + base64);
        Map<String, Object> userContent = Map.of(
                "role", "user",
                "content", List.of(
                        Map.of("type", "text", "text", "사진 속 음식의 이름과 열량(kcal), 탄수화물(g), 단백질(g), 지방(g)을 JSON으로 답하세요. 키는 name, energy, carbohydrate, protein, fat 입니다."),
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

        String response = restTemplate.postForObject(
                "https://api.openai.com/v1/chat/completions",
                request,
                String.class
        );

        if (response == null) {
            throw new IOException("OpenAI response is null");
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response);
        String content = root.path("choices").get(0).path("message").path("content").asText();

        JsonNode result = mapper.readTree(content);
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
