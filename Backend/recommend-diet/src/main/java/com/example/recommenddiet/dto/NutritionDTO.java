package com.example.recommenddiet.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NutritionDTO {
    private String name;
    private String category;
    private String repName;
    private String subcategory;
    private String standardAmount;
    private Integer energy;
    private Double protein;
    private Double fat;
    private Double carbohydrate;
    private Double sugar;
    private Double sodium;
    private Double cholesterol;
    private Double saturatedFat;
} 