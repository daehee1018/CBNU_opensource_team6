package opensource_project_team6.recommend_diet.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Gender {
    MALE("남성"),
    FEMALE("여성"),
    OTHER("기타"),
    NONE("선택 안 함");

    private final String value;
    Gender(String value) { this.value = value; }
    @JsonValue
    public String getValue() { return value; }

    //프론트 문자열 → enum으로 변환용 메서드 추가
    @JsonCreator
    public static Gender from(String value) {
        for (Gender g : Gender.values()) {
            if (g.getValue().equals(value)) return g;
        }
        throw new IllegalArgumentException("지원하지 않는 성별: " + value);
    }
}
