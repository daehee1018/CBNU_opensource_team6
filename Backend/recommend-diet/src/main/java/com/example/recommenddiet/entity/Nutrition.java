package com.example.recommenddiet.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "nutrition")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Nutrition {
    @Id
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