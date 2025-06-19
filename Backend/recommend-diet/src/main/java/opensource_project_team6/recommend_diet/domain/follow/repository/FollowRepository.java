package opensource_project_team6.recommend_diet.domain.follow.repository;

import opensource_project_team6.recommend_diet.domain.follow.entity.Follow;
import opensource_project_team6.recommend_diet.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByFollowerAndFollowing(User follower, User following);
    List<Follow> findAllByFollower(User follower);
    List<Follow> findAllByFollowing(User following);
}
