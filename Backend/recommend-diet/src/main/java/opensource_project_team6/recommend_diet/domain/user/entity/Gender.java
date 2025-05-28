package opensource_project_team6.recommend_diet.domain.user.entity;

public enum Gender {
    MALE("남성"),
    FEMALE("여성");

    private final String value;
    Gender(String value) {this.value = value;}
    public String getValue() {return value;}
}
