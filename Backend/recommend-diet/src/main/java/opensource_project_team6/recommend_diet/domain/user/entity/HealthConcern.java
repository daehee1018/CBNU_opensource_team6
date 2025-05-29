package opensource_project_team6.recommend_diet.domain.user.entity;

public enum HealthConcern {
    STRESS("스트레스 관리"),
    SLEEP("수면 개선"),
    DIGESTION("소화 개선"),
    NONE("없음");

    private final String displayName;
    HealthConcern(String displayName) { this.displayName = displayName; }
    public String getDisplayName() { return displayName; }
}
