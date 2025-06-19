package opensource_project_team6.recommend_diet.domain.user.controller;

import lombok.RequiredArgsConstructor;
import opensource_project_team6.recommend_diet.domain.follow.dto.UserSimpleResponse;
import opensource_project_team6.recommend_diet.domain.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserRepository userRepository;

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> search(@RequestParam String name) {
        List<UserSimpleResponse> list = userRepository.findByNameContaining(name).stream()
                .map(u -> new UserSimpleResponse(u.getId(), u.getName()))
                .collect(Collectors.toList());
        Map<String, Object> res = new HashMap<>();
        res.put("status", 200);
        res.put("data", list);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(u -> ResponseEntity.ok(Map.of(
                        "status", 200,
                        "data", new UserSimpleResponse(u.getId(), u.getName())
                )))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("status", 404)));
    }
}
