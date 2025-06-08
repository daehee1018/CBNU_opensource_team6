package opensource_project_team6.recommend_diet.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Interest {
    MUSCLE("근육 증가"),
    DIET("다이어트"),
    BALANCE("식단 균형"),
    VEGETARIAN("채식 식단"),
    ETC("기타");

    private final String displayName;
    Interest(String displayName) { this.displayName = displayName; }
    @JsonValue
    public String getDisplayName() { return displayName; }

    //프론트 문자열 → enum으로 변환용 메서드 추가
    @JsonCreator
    public static Interest from(String value) {
        for (Interest i : Interest.values()) {
            if (i.getDisplayName().equals(value)) return i;
        }
        throw new IllegalArgumentException("지원하지 않는 관심사: " + value);
    }
}
