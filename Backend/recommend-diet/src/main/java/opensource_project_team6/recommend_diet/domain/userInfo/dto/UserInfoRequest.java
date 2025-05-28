package opensource_project_team6.recommend_diet.domain.userInfo.dto;


import org.antlr.v4.runtime.misc.NotNull;

public record UserInfoRequest(
        @NotNull Long userId,
        @NotNull Double height,
        @NotNull Double weight,
        @NotNull String gender,
        @NotNull Double targetWeight,
        @NotNull String interest,
        @NotNull String healthConcern
) {}
