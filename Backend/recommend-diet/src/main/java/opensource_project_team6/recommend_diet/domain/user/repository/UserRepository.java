package opensource_project_team6.recommend_diet.domain.user.repository;

import opensource_project_team6.recommend_diet.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByGoogleId(String googleId);
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    java.util.List<User> findByNameContaining(String name);
}
