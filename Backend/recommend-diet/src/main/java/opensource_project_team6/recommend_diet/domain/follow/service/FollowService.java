package opensource_project_team6.recommend_diet.domain.follow.service;

import lombok.RequiredArgsConstructor;
import opensource_project_team6.recommend_diet.domain.follow.dto.UserSimpleResponse;
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
    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public void follow(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) return;
        User follower = userRepository.findById(followerId).orElseThrow();
        User following = userRepository.findById(followingId).orElseThrow();
        followRepository.findByFollowerAndFollowing(follower, following)
                .orElseGet(() -> followRepository.save(Follow.builder()
                        .follower(follower)
                        .following(following)
                        .build()));
    }

    public void unfollow(Long followerId, Long followingId) {
        User follower = userRepository.findById(followerId).orElseThrow();
        User following = userRepository.findById(followingId).orElseThrow();
        followRepository.findByFollowerAndFollowing(follower, following)
                .ifPresent(followRepository::delete);
    }

    public List<UserSimpleResponse> getFollowers(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return followRepository.findAllByFollowing(user).stream()
                .map(f -> new UserSimpleResponse(f.getFollower().getId(), f.getFollower().getName()))
                .collect(Collectors.toList());
    }

    public List<UserSimpleResponse> getFollowings(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return followRepository.findAllByFollower(user).stream()
                .map(f -> new UserSimpleResponse(f.getFollowing().getId(), f.getFollowing().getName()))
                .collect(Collectors.toList());
    }
}
