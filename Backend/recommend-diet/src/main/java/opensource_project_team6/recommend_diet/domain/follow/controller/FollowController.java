package opensource_project_team6.recommend_diet.domain.follow.controller;

import lombok.RequiredArgsConstructor;
import opensource_project_team6.recommend_diet.domain.follow.dto.UserSimpleResponse;
import opensource_project_team6.recommend_diet.domain.follow.service.FollowService;
import opensource_project_team6.recommend_diet.global.util.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/follow")
@RequiredArgsConstructor
public class FollowController {
    private final FollowService followService;

    @PostMapping("/{targetId}")
    public ResponseEntity<Map<String, Object>> follow(@AuthenticationPrincipal UserPrincipal user,
                                                      @PathVariable Long targetId) {
        followService.follow(user.getId(), targetId);
        return ResponseEntity.ok(Map.of("status", 200, "message", "followed"));
    }

    @DeleteMapping("/{targetId}")
    public ResponseEntity<Map<String, Object>> unfollow(@AuthenticationPrincipal UserPrincipal user,
                                                        @PathVariable Long targetId) {
        followService.unfollow(user.getId(), targetId);
        return ResponseEntity.ok(Map.of("status", 200, "message", "unfollowed"));
    }

    @GetMapping("/followers/{userId}")
    public ResponseEntity<Map<String, Object>> followers(@PathVariable Long userId) {
        List<UserSimpleResponse> list = followService.getFollowers(userId);
        Map<String, Object> res = new HashMap<>();
        res.put("status", 200);
        res.put("data", list);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/followings/{userId}")
    public ResponseEntity<Map<String, Object>> followings(@PathVariable Long userId) {
        List<UserSimpleResponse> list = followService.getFollowings(userId);
        Map<String, Object> res = new HashMap<>();
        res.put("status", 200);
        res.put("data", list);
        return ResponseEntity.ok(res);
    }
}
