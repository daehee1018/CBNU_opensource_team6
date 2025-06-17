package opensource_project_team6.recommend_diet.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    /*@OneToOne(mappedBy ="user")
    private UserInfo userInfo;*/

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email; // 이메일(로그인 ID)

    private String password; // 일반 회원가입 시 이용 구글 이용 시 null

    @Column(nullable = false)
    private LocalDate birthDate; // 생년월일

    @Column(nullable = false)
    private Double height;

    @Column(nullable = false)
    private Double weight;

    @Column(nullable = false)
    private Double targetWeight;

    private String gender;

    private String interest;

    @Column(name = "health_concern")
    private String healthConcern;

    private String googleId; // 구글 식별자 코드

    private String profileImage; // 구글 프로필 사진

    private boolean isProfileComplete = false;

    public void setIsProfileComplete(boolean isProfileComplete) {
        this.isProfileComplete = isProfileComplete;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void updateProfile(String name, LocalDate birthDate, Double height, Double weight, Double targetWeight, String gender, String interest, String healthConcern) {
        if (name != null) this.name = name;
        if(birthDate != null) this.birthDate = birthDate;
        if(height != null) this.height = height;
        if(weight != null) this.weight = weight;
        if(targetWeight != null) this.targetWeight = targetWeight;
        if(gender != null) this.gender = gender;
        if(interest != null) this.interest = interest;
        if(healthConcern != null) this.healthConcern = healthConcern;
        this.isProfileComplete = true;
    }

    @Builder
    public User(String name, String email, String password, LocalDate birthDate, Double height, Double weight, Double targetWeight, String gender, String interest, String healthConcern, String googleId, String profileImage) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.birthDate = birthDate;
        this.height = height;
        this.weight = weight;
        this.targetWeight = targetWeight;
        this.gender = gender;
        this.interest = interest;
        this.healthConcern = healthConcern;
        this.googleId = googleId;
        this.profileImage = profileImage;
    }
}
