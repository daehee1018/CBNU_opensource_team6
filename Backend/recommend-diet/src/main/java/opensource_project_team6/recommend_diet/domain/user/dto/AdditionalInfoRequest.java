package opensource_project_team6.recommend_diet.domain.user.dto;

import opensource_project_team6.recommend_diet.domain.user.entity.Gender;
import opensource_project_team6.recommend_diet.domain.user.entity.HealthConcern;
import opensource_project_team6.recommend_diet.domain.user.entity.Interest;

import java.time.LocalDate;

public record AdditionalInfoRequest(
   String name,
   LocalDate birthDate,
   Double height,
   Double weight,
   Double targetWeight,
   Gender gender,
   Interest interest,
   HealthConcern healthConcern
) {}
