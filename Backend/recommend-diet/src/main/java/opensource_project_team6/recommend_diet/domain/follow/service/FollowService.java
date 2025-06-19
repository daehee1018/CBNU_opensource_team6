package opensource_project_team6.recommend_diet.domain.follow.service;

import lombok.RequiredArgsConstructor;
import opensource_project_team6.recommend_diet.domain.follow.entity.Follow;
import opensource_project_team6.recommend_diet.domain.follow.repository.FollowRepository;
import opensource_project_team6.recommend_diet.domain.user.entity.User;
import opensource_project_team6.recommend_diet.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    public void follow(Long followerId, Long followingId) {
        User follower = userRepository.findById(followerId).orElseThrow();
        User following = userRepository.findById(followingId).orElseThrow();

        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new IllegalArgumentException("이미 팔로우 중입니다.");
        }

        Follow follow = Follow.builder().follower(follower).following(following).build();
        followRepository.save(follow);
    }

    public void unfollow(Long followerId, Long followingId) {
        User follower = userRepository.findById(followerId).orElseThrow();
        User following = userRepository.findById(followingId).orElseThrow();

        Follow follow = followRepository.findByFollowerAndFollowing(follower, following)
                .orElseThrow(() -> new IllegalArgumentException("팔로우 중이 아닙니다."));
        followRepository.delete(follow);
    }

    public long getFollowerCount(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return followRepository.countByFollowing(user);
    }

    public long getFollowingCount(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return followRepository.countByFollower(user);
    }

    public List<String> getFollowerList(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return followRepository.findByFollowing(user)
                .stream().map(f -> f.getFollower().getName()).collect(Collectors.toList());
    }

    public List<String> getFollowingList(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return followRepository.findByFollower(user)
                .stream().map(f -> f.getFollowing().getName()).collect(Collectors.toList());
    }

    public String getUserName(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return user.getName();
    }
}
