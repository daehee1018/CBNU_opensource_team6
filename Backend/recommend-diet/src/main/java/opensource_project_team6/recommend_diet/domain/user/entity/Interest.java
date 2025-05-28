package opensource_project_team6.recommend_diet.domain.user.entity;

public enum Interest {
    DIET("식단 관리 & 다이어트"),
    NUTRITION("영양제 섭취 관리"),
    BLOOD("혈당 관리");

    private final String displayName;
    Interest(String displayName) { this.displayName = displayName; }
    public String getDisplayName() { return displayName; }
}
