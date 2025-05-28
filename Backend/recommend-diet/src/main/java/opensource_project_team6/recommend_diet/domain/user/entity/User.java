package opensource_project_team6.recommend_diet.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import opensource_project_team6.recommend_diet.domain.userInfo.entity.UserInfo;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @OneToOne(mappedBy ="user")
    private UserInfo userInfo;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email; // 이메일(로그인 ID)

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private LocalDate birthDate; // 생년월일

    @Column(nullable = false)
    private Double height;

    @Column(nullable = false)
    private Double weight;

    @Column(nullable = false)
    private Double targetWeight;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private Interest interest;

    @Enumerated(EnumType.STRING)
    @Column(name = "healte_concern")
    private HealthConcern healthConcern;

    @Builder
    public User(String name, String email, String password, LocalDate birthDate) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.birthDate = birthDate;
    }
}
