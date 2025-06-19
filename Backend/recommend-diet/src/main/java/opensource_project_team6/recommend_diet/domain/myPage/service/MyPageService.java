package opensource_project_team6.recommend_diet.domain.myPage.service;

import lombok.RequiredArgsConstructor;
import opensource_project_team6.recommend_diet.domain.follow.repository.FollowRepository;
import opensource_project_team6.recommend_diet.domain.myPage.dto.MyPageResponse;
import opensource_project_team6.recommend_diet.domain.myPage.dto.MacroRatioRequest;
import opensource_project_team6.recommend_diet.domain.user.entity.User;
import opensource_project_team6.recommend_diet.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    public MyPageResponse getMyPage(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("user not found"));

        int age = Period.between(user.getBirthDate(), LocalDate.now()).getYears();

        String imageUrl = user.getProfileImage();
        if (imageUrl != null && !imageUrl.startsWith("http")) {
            imageUrl = "/images/" + imageUrl; // 로컬 이미지라면 prefix 추가
        }

        long followerCount = followRepository.countByFollowing(user);
        long followingCount = followRepository.countByFollower(user);

        return new MyPageResponse(
                user.getName(),
                user.getGender(),
                user.getBirthDate(),
                age,
                user.getHeight() != null ? user.getHeight().intValue() : null,
                user.getWeight(),
                user.getTargetWeight(),
                imageUrl,
                followerCount,
                followingCount
        );
    }

    public void uploadProfileImage(Long userId, String filename) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));
        user.setProfileImage(filename);
        userRepository.save(user);
    }

    public void updateMacroRatio(Long userId, MacroRatioRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));
        user.updateMacroRatio(request.carbRatio(), request.proteinRatio(), request.fatRatio());
        userRepository.save(user);
    }
}
