package opensource_project_team6.recommend_diet.domain.userInfo.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import opensource_project_team6.recommend_diet.domain.user.entity.Gender;
import opensource_project_team6.recommend_diet.domain.user.entity.HealthConcern;
import opensource_project_team6.recommend_diet.domain.user.entity.Interest;
import opensource_project_team6.recommend_diet.domain.user.entity.User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserInfo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private Double height;
    private Double weight;
    private Double targetWeight;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private Interest interest;

    @Enumerated(EnumType.STRING)
    @Column(name = "healte_concern")
    private HealthConcern healthConcern;

}
