package opensource_project_team6.recommend_diet.domain.userInfo.repository;

import opensource_project_team6.recommend_diet.domain.userInfo.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
    Optional<UserInfo> findByUserId(Long userId);
}
