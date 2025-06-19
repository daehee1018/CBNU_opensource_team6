package opensource_project_team6.recommend_diet.domain.follow.controller;

import lombok.RequiredArgsConstructor;
import opensource_project_team6.recommend_diet.domain.follow.dto.UserSimpleResponse;
import opensource_project_team6.recommend_diet.domain.follow.service.FollowService;
import opensource_project_team6.recommend_diet.global.util.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/follow")
public class FollowController {
    private final FollowService followService;

    @PostMapping("/{targetUserId}")
    public ResponseEntity<?> follow(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                    @PathVariable Long targetUserId) {
        followService.follow(userPrincipal.getId(), targetUserId);

        // 대상 유저 이름 조회
        String targetUserName = followService.getUserName(targetUserId);

        return ResponseEntity.ok(Map.of(
                "message", "팔로우 성공",
                "targetUser", targetUserName
        ));
    }

    @DeleteMapping("/{targetUserId}")
    public ResponseEntity<?> unfollow(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                      @PathVariable Long targetUserId) {
        followService.unfollow(userPrincipal.getId(), targetUserId);

        String targetUserName = followService.getUserName(targetUserId);

        return ResponseEntity.ok(Map.of(
                "message", "언팔로우 성공",
                "targetUser", targetUserName
        ));
    }

    @GetMapping("/followers")
    public ResponseEntity<Map<String, Object>> followers(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<UserSimpleResponse> followers = followService.getFollowerList(userPrincipal.getId());
        return ResponseEntity.ok(Map.of("data", followers));
    }

    @GetMapping("/followings")
    public ResponseEntity<Map<String, Object>> followings(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<UserSimpleResponse> followings = followService.getFollowingList(userPrincipal.getId());
        return ResponseEntity.ok(Map.of("data", followings));
    }

    @GetMapping("/followers/{userId}")
    public ResponseEntity<Map<String, Object>> followersById(@PathVariable Long userId) {
        List<UserSimpleResponse> followers = followService.getFollowerList(userId);
        return ResponseEntity.ok(Map.of("data", followers));
    }

    @GetMapping("/followings/{userId}")
    public ResponseEntity<Map<String, Object>> followingsById(@PathVariable Long userId) {
        List<UserSimpleResponse> followings = followService.getFollowingList(userId);
        return ResponseEntity.ok(Map.of("data", followings));
    }
}
