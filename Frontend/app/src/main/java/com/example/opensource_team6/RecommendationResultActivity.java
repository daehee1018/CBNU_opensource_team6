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
import java.util.List;

public class RecommendationResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation_result);

        TextView resultText = findViewById(R.id.recommendationText);

        // 전달된 벡터 및 식사 유형
        String vector = getIntent().getStringExtra("recommendationVector");
        String mealType = getIntent().getStringExtra("currentMeal");

        if (vector == null || mealType == null) {
            Toast.makeText(this, "입력 정보 부족", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ 추천 결과 문자열 작성
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(mealType).append(" 외 추천 식사]\n\n");
        sb.append("입력 벡터:\n").append(vector).append("\n\n");

        // 다음 식사 시간 예측
        String nextMeal = getNextMealSuggestion(mealType);
        sb.append("👉 다음 추천 식사 시간: ").append(nextMeal).append("\n\n");

        // 추천 음식 계산
        String[] foodList = recommendFoodBasedOnVector(vector);
        sb.append("추천 음식:\n");
        for (String food : foodList) {
            sb.append("- ").append(food).append("\n");
        }

        resultText.setText(sb.toString());
    }

    // ✅ 다음 식사 시간 추천 (예: 조식 → 중식)
    private String getNextMealSuggestion(String completedMeals) {
        String[] all = {"조식", "중식", "석식", "야식"};
        List<String> done = List.of(completedMeals.split(",\\s*"));
        for (String meal : all) {
            if (!done.contains(meal)) return meal;
        }
        return "모두 완료됨";
    }

    // ✅ Autoencoder 벡터 기반 음식 추천
    private String[] recommendFoodBasedOnVector(String vectorString) {
        String[] parts = vectorString.replace("[", "").replace("]", "").split(",");
        float[] target = new float[parts.length];
        for (int i = 0; i < parts.length; i++) {
            target[i] = Float.parseFloat(parts[i].trim());
        }

        FoodDao dao = new FoodDao(this);
        List<Food> allFoods = dao.searchFoodByName(""); // 전체 음식

        List<FoodDistance> distances = new ArrayList<>();
        for (Food food : allFoods) {
            float[] foodVector = new float[]{
                    (float) food.getEnergy(), (float) food.getCarbohydrate(), (float) food.getProtein(),
                    (float) food.getFat(), (float) food.getSugar(), (float) food.getFiber(),
                    (float) food.getCalcium(), (float) food.getPotassium(), (float) food.getSodium(),
                    (float) food.getCholesterol(), (float) food.getSaturated_fat(), (float) food.getTrans_fat()
            };

            float dist = 0;
            for (int i = 0; i < target.length; i++) {
                dist += Math.pow(target[i] - foodVector[i], 2);
            }

            distances.add(new FoodDistance(food.getName(), dist));
        }

        Collections.sort(distances);

        int count = Math.min(10, distances.size());
        String[] top10 = new String[count];
        for (int i = 0; i < count; i++) {
            top10[i] = distances.get(i).name;
        }

        return top10;
    }

    // ⬇️ 음식과 거리 비교 클래스
    private static class FoodDistance implements Comparable<FoodDistance> {
        String name;
        float distance;

        FoodDistance(String name, float distance) {
            this.name = name;
            this.distance = distance;
        }

        @Override
        public int compareTo(FoodDistance other) {
            return Float.compare(this.distance, other.distance);
        }
    }
}
