package com.example.opensource_team6.model;

public class Food {
    private String name;
    private String weight; // 원래 문자열로 저장됨 (예: "250g")
    private float kcal;
    private float carbs;
    private float protein;
    private float fat;

    // Getter & Setter
    public String getName() {
        return name;
    }

    public String getWeightRaw() {
        return weight;
    }

    // ✅ 숫자만 파싱해서 반환
    public float getWeight() {
        try {
            return Float.parseFloat(weight.replaceAll("[^\\d.]", ""));
        } catch (Exception e) {
            return 0f;
        }
    }

    public float getKcal() {
        return kcal;
    }

    public float getCarbs() {
        return carbs;
    }

    public float getProtein() {
        return protein;
    }

    public float getFat() {
        return fat;
    }
}

