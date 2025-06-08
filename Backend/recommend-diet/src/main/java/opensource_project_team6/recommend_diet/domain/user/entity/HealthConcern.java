package opensource_project_team6.recommend_diet.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum HealthConcern {
    DIABETES("당뇨"),
    HYPERTENSION("고혈압"),
    HYPERLIPIDEMIA("고지혈증"),
    DIGESTION("소화 문제"),
    NONE("없음");

    private final String displayName;
    HealthConcern(String displayName) { this.displayName = displayName; }
    @JsonValue
    public String getDisplayName() { return displayName; }

    //프론트 문자열 → enum으로 변환용 메서드 추가
    @JsonCreator
    public static HealthConcern from(String value) {
        for (HealthConcern h : HealthConcern.values()) {
            if (h.getDisplayName().equals(value)) return h;
        }
        throw new IllegalArgumentException("지원하지 않는 건강 고민: " + value);
    }
}
