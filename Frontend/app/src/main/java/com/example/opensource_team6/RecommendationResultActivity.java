// RecommendationResultActivity.java
package com.example.opensource_team6;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.opensource_team6.data.Food;
import com.example.opensource_team6.data.FoodDao;
import com.example.opensource_team6.data.NutritionDBHelper;

import java.util.*;

public class RecommendationResultActivity extends AppCompatActivity {

    private Spinner categorySpinner, subcategorySpinner;
    private ArrayAdapter<String> subcategoryAdapter;
    private List<FoodDistance> allRecommendations = new ArrayList<>();
    private String[] top10Foods;

    private TextView resultText;
    private NutritionDBHelper dbHelper;

    private String mealType, totalVector, deficitVector, vector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation_result);

        resultText = findViewById(R.id.recommendationText);
        categorySpinner = findViewById(R.id.categorySpinner);
        subcategorySpinner = findViewById(R.id.subcategorySpinner);
        dbHelper = new NutritionDBHelper(this);

        vector = getIntent().getStringExtra("recommendationVector");
        mealType = getIntent().getStringExtra("currentMeal");
        totalVector = getIntent().getStringExtra("totalVector");
        deficitVector = getIntent().getStringExtra("deficitVector");

        if (vector == null || totalVector == null || deficitVector == null ||
                vector.isEmpty() || totalVector.isEmpty() || deficitVector.isEmpty()) {
            Toast.makeText(this, "추천 결과 데이터가 부족합니다", Toast.LENGTH_LONG).show();
            resultText.setText("추천 결과 데이터를 불러올 수 없습니다.\n\n" +
                    "recommendationVector: " + vector + "\n" +
                    "totalVector: " + totalVector + "\n" +
                    "deficitVector: " + deficitVector);
            return;
        }


        allRecommendations = getRecommendations(vector, deficitVector);
        updateTop10();

        List<String> categories = dbHelper.getAllCategories();
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        subcategoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        subcategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subcategorySpinner.setAdapter(subcategoryAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = categories.get(position);
                List<String> subcategories = dbHelper.getSubcategoriesByCategory(selectedCategory);
                subcategoryAdapter.clear();
                subcategoryAdapter.addAll(subcategories);
                subcategoryAdapter.notifyDataSetChanged();
                updateFilteredRecommendations();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        subcategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateFilteredRecommendations();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        showDefaultRecommendations();
    }

    private void updateTop10() {
        int count = Math.min(10, allRecommendations.size());
        top10Foods = new String[count];
        for (int i = 0; i < count; i++) {
            FoodDistance fd = allRecommendations.get(i);
            top10Foods[i] = fd.name + " (" + fd.gram + "g, 추천점수: " + String.format("%.2f", fd.similarity) + ")";
        }
    }

    private void showDefaultRecommendations() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(mealType).append(" 외 추천 식사]\n\n");
        sb.append("🔹 총합 벡터:\n").append(totalVector).append("\n\n");
        sb.append("🔸 결핍 벡터:\n").append(deficitVector).append("\n\n");
        sb.append("✅ 출력 벡터:\n").append(vector).append("\n\n");

        sb.append("👉 기본 추천 음식 (상위 10개):\n");
        for (String food : top10Foods) sb.append("- ").append(food).append("\n");

        resultText.setText(sb.toString());
    }

    private void updateFilteredRecommendations() {
        String selectedCategory = (String) categorySpinner.getSelectedItem();
        String selectedSubcategory = (String) subcategorySpinner.getSelectedItem();

        if (selectedCategory == null || selectedSubcategory == null) return;

        List<String> filtered = new ArrayList<>();
        for (FoodDistance fd : allRecommendations) {
            Food food = fd.food;
            if (food.getCategory().equals(selectedCategory) &&
                    food.getSubcategory().equals(selectedSubcategory)) {
                filtered.add(fd.name + " (" + fd.gram + "g, 추천점수: " + String.format("%.2f", fd.similarity) + ")");
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[").append(mealType).append(" 외 추천 식사]\n\n");
        sb.append("🔹 총합 벡터:\n").append(totalVector).append("\n\n");
        sb.append("🔸 결핍 벡터:\n").append(deficitVector).append("\n\n");
        sb.append("✅ 출력 벡터:\n").append(vector).append("\n\n");

        sb.append("👉 기본 추천 음식 (상위 10개):\n");
        for (String food : top10Foods) sb.append("- ").append(food).append("\n");

        sb.append("\n🔍 필터링된 추천 음식:\n");
        if (filtered.isEmpty()) {
            sb.append("해당 범위에 음식이 없습니다.\n");
        } else {
            for (String line : filtered) {
                sb.append("- ").append(line).append("\n");
            }
        }

        resultText.setText(sb.toString());
    }

    private List<FoodDistance> getRecommendations(String vectorString, String deficitString) {
        float[] min = new float[]{0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
        float[] max = new float[]{595f, 104f, 41.55f, 60.25f, 70f, 7.402f, 0.68662f, 25.6f};

        float[] target = parseVector(vectorString);
        float[] weight = parseVector(deficitString);

        FoodDao dao = new FoodDao(this);
        List<Food> allFoods = dao.searchFoodByName("");
        List<FoodDistance> list = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        for (Food food : allFoods) {
            if (seen.contains(food.getName())) continue;
            seen.add(food.getName());

            float[] vec = new float[]{
                    (float) food.getEnergy(),
                    (float) food.getCarbohydrate(),
                    (float) food.getProtein(),
                    (float) food.getFat(),
                    (float) food.getSugar(),
                    (float) food.getSodium() / 1000f,
                    (float) food.getCholesterol() / 1000f,
                    (float) food.getSaturated_fat()
            };

            double weightRatio = food.getWeight() <= 0 ? 1.0 : food.getWeight() / 100.0;
            for (int i = 0; i < vec.length; i++) vec[i] *= weightRatio;

            float[] norm = new float[vec.length];
            for (int i = 0; i < vec.length; i++) {
                norm[i] = (max[i] - min[i] == 0) ? 0 : (vec[i] - min[i]) / (max[i] - min[i]);
            }

            float satisfaction = getSatisfactionScore(norm, weight);
            float cosine = cosineSimilarityWithWeight(norm, target, weight);
            float finalScore = (satisfaction + cosine) / 2f;

            list.add(new FoodDistance(food.getName(), (int) food.getWeight(), finalScore, food));
        }

        list.sort(Collections.reverseOrder());
        return list;
    }

    private float[] parseVector(String s) {
        String[] parts = s.replace("[", "").replace("]", "").split(",");
        float[] result = new float[parts.length];
        for (int i = 0; i < parts.length; i++) result[i] = Float.parseFloat(parts[i].trim());
        return result;
    }

    private float getSatisfactionScore(float[] foodVec, float[] deficitVec) {
        float scoreSum = 0f;
        int count = 0;
        for (int i = 0; i < foodVec.length; i++) {
            if (deficitVec[i] > 0f) {
                scoreSum += Math.min(1f, foodVec[i] / deficitVec[i]);
                count++;
            }
        }
        return (count > 0) ? scoreSum / count : 0f;
    }

    private float cosineSimilarityWithWeight(float[] a, float[] b, float[] w) {
        float dot = 0f, normA = 0f, normB = 0f;
        for (int i = 0; i < a.length; i++) {
            dot += w[i] * a[i] * b[i];
            normA += w[i] * a[i] * a[i];
            normB += w[i] * b[i] * b[i];
        }
        if (normA == 0 || normB == 0) return 0f;
        return dot / ((float) Math.sqrt(normA) * (float) Math.sqrt(normB));
    }

    private static class FoodDistance implements Comparable<FoodDistance> {
        String name;
        int gram;
        float similarity;
        Food food;

        FoodDistance(String name, int gram, float similarity, Food food) {
            this.name = name;
            this.gram = gram;
            this.similarity = similarity;
            this.food = food;
        }

        @Override
        public int compareTo(FoodDistance o) {
            return Float.compare(this.similarity, o.similarity);
        }
    }
}
