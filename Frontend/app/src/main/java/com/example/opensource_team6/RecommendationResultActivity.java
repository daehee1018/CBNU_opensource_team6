package com.example.opensource_team6;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.opensource_team6.data.Food;
import com.example.opensource_team6.data.FoodDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecommendationResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation_result);

        TextView resultText = findViewById(R.id.recommendationText);

        String vector = getIntent().getStringExtra("recommendationVector");
        String mealType = getIntent().getStringExtra("currentMeal");
        String completed = getIntent().getStringExtra("completedMeals");
        String outputVector = getIntent().getStringExtra("recommendationVector");
        String totalVector = getIntent().getStringExtra("totalVector");
        String deficitVector = getIntent().getStringExtra("deficitVector");

        if (deficitVector == null || deficitVector.isEmpty() || mealType == null || mealType.isEmpty()) {
            Toast.makeText(this, "입력 정보 부족: 벡터 또는 식사 정보 없음", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String nextMeal = getNextMealSuggestion(completed);

        StringBuilder sb = new StringBuilder();
        sb.append("[").append(mealType).append(" 외 추천 식사]\n\n");
        sb.append("🔹 총합 벡터 (섭취량):\n").append(totalVector).append("\n\n");
        sb.append("🔸 결핍 벡터 (입력값):\n").append(deficitVector).append("\n\n");
        sb.append("✅ 출력 벡터 (모델 결과):\n").append(outputVector).append("\n\n");

        sb.append("👉 다음 추천 식사 시간: ").append(nextMeal).append("\n\n");

        String[] foodList = recommendFoodBasedOnVector(vector);
        sb.append("추천 음식:\n");
        for (String food : foodList) {
            sb.append("- ").append(food).append("\n");
        }

        resultText.setText(sb.toString());
    }

    private String getNextMealSuggestion(String completedMeals) {
        String[] all = {"조식", "중식", "석식", "야식"};

        if (completedMeals == null || completedMeals.trim().isEmpty()) return "중식";

        String[] meals = completedMeals.split(",\\s*");
        List<String> done = List.of(meals);

        for (String meal : all) {
            if (!done.contains(meal)) return meal;
        }
        return "모두 완료됨";
    }

    private String[] recommendFoodBasedOnVector(String vectorString) {
        // 정규화 범위 설정 (학습에 사용한 범위와 동일해야 함)
        float[] minValues = new float[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        float[] maxValues = new float[]{2500, 310, 55, 70, 100, 25, 700, 3500, 2000, 300, 20, 2};

        // 1. 문자열 → float[] 변환
        String[] parts = vectorString.replace("[", "").replace("]", "").split(",");
        float[] targetRaw = new float[parts.length];
        for (int i = 0; i < parts.length; i++) {
            targetRaw[i] = Float.parseFloat(parts[i].trim());
        }

        // 2. MinMax 정규화
        float[] target = new float[targetRaw.length];
        for (int i = 0; i < targetRaw.length; i++) {
            if (maxValues[i] - minValues[i] == 0) {
                target[i] = 0; // division by zero 방지
            } else {
                target[i] = (targetRaw[i] - minValues[i]) / (maxValues[i] - minValues[i]);
            }
        }

        // 3. 음식 DB 불러오기
        FoodDao dao = new FoodDao(this);
        List<Food> allFoods = dao.searchFoodByName("");

        List<FoodDistance> distances = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        for (Food food : allFoods) {
            if (seen.contains(food.getName())) continue;
            seen.add(food.getName());

            float[] foodVectorRaw = new float[]{
                    (float) food.getEnergy(), (float) food.getCarbohydrate(), (float) food.getProtein(),
                    (float) food.getFat(), (float) food.getSugar(), (float) food.getFiber(),
                    (float) food.getCalcium(), (float) food.getPotassium(), (float) food.getSodium(),
                    (float) food.getCholesterol(), (float) food.getSaturated_fat(), (float) food.getTrans_fat()
            };

            float[] foodVector = new float[foodVectorRaw.length];
            for (int i = 0; i < foodVectorRaw.length; i++) {
                if (maxValues[i] - minValues[i] == 0) {
                    foodVector[i] = 0;
                } else {
                    foodVector[i] = (foodVectorRaw[i] - minValues[i]) / (maxValues[i] - minValues[i]);
                }
            }

            float distance = cosineDistance(target, foodVector);
            distances.add(new FoodDistance(food.getName(), (int) food.getWeight(), distance));
        }

        Collections.sort(distances);
        
        int count = Math.min(10, distances.size());
        String[] top10 = new String[count];
        for (int i = 0; i < count; i++) {
            FoodDistance fd = distances.get(i);
            top10[i] = fd.name + " (" + fd.gram + "g)";
        }

        return top10;
    }


    private float cosineDistance(float[] a, float[] b) {
        float dot = 0, normA = 0, normB = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        if (normA == 0 || normB == 0) return Float.MAX_VALUE;
        return 1 - (dot / ((float) Math.sqrt(normA) * (float) Math.sqrt(normB)));
    }

    private static class FoodDistance implements Comparable<FoodDistance> {
        String name;
        int gram;
        float distance;

        FoodDistance(String name, int gram, float distance) {
            this.name = name;
            this.gram = gram;
            this.distance = distance;
        }

        @Override
        public int compareTo(FoodDistance other) {
            return Float.compare(this.distance, other.distance);
        }
    }
}
