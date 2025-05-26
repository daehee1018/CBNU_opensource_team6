package opensource_project_team6.recommend_diet.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String lastName; // 성

    @Column(nullable = false)
    private String firstName; // 이름

    @Column(nullable = false, unique = true)
    private String email; // 이메일(로그인 ID)

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private LocalDate birthDate; // 생년월일
    @Builder
    public User(String lastName, String firstName, String email, String password, LocalDate birthDate) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
        this.password = password;
        this.birthDate = birthDate;
    }
}
