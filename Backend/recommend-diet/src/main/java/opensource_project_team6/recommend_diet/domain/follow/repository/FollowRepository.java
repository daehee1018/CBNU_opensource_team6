package opensource_project_team6.recommend_diet.domain.follow.repository;

import opensource_project_team6.recommend_diet.domain.follow.entity.Follow;
import opensource_project_team6.recommend_diet.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByFollowerAndFollowing(User follower, User following);

    Optional<Follow> findByFollowerAndFollowing(User follower, User following);

    List<Follow> findByFollower(User follower);

    List<Follow> findByFollowing(User following);

    Long countByFollower(User follower);

    Long countByFollowing(User following);
}
