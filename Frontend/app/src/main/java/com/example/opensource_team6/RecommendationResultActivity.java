package com.example.opensource_team6;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.opensource_team6.data.Food;
import com.example.opensource_team6.data.FoodDao;
import com.example.opensource_team6.data.NutritionDBHelper;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.*;

public class RecommendationResultActivity extends AppCompatActivity {

    private Spinner categorySpinner, subcategorySpinner;
    private TextView headerText, descriptionText;
    private RecyclerView recyclerView;
    private BarChart deficitChart;
    private NutritionDBHelper dbHelper;

    private final float[] recommended = {2500f, 310f, 55f, 70f, 100f, 2f, 0.3f, 20f}; // 나트륨, 콜레스테롤은 g 기준
    private final String[] labels = {"에너지", "탄수화물", "단백질", "지방", "당류", "나트륨(g)", "콜레스테롤(g)", "포화지방"};

    private String vector, totalVector, deficitVector, mealType;
    private List<FoodDistance> allRecommendations;
    private RecommendationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation_result);

        headerText = findViewById(R.id.headerText);
        descriptionText = findViewById(R.id.descriptionText);
        categorySpinner = findViewById(R.id.categorySpinner);
        subcategorySpinner = findViewById(R.id.subcategorySpinner);
        deficitChart = findViewById(R.id.deficitChart);
        recyclerView = findViewById(R.id.recommendationRecycler);

        dbHelper = new NutritionDBHelper(this);

        vector = getIntent().getStringExtra("recommendationVector");
        totalVector = getIntent().getStringExtra("totalVector");
        deficitVector = getIntent().getStringExtra("deficitVector");
        mealType = getIntent().getStringExtra("currentMeal");

        allRecommendations = getRecommendations(vector, deficitVector);

        setupChart(parseVector(totalVector));


        // 어댑터 생성
        adapter = new RecommendationAdapter(allRecommendations, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);



        headerText.setText("✨ 추천 음식 목록");
        descriptionText.setText("AI가 당신의 식단을 기반으로 추천합니다.");

        List<String> categories = dbHelper.getAllCategories();
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        subcategorySpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>()));

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                List<String> sub = dbHelper.getSubcategoriesByCategory(categories.get(pos));
                ArrayAdapter<String> subAdapter = new ArrayAdapter<>(RecommendationResultActivity.this, android.R.layout.simple_spinner_item, sub);
                subAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                subcategorySpinner.setAdapter(subAdapter);
                filterRecommendations();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        subcategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                filterRecommendations();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void filterRecommendations() {
        String cat = (String) categorySpinner.getSelectedItem();
        String sub = (String) subcategorySpinner.getSelectedItem();
        if (cat == null || sub == null) return;

        List<FoodDistance> filtered = new ArrayList<>();
        for (FoodDistance fd : allRecommendations) {
            if (fd.food.getCategory().equals(cat) && fd.food.getSubcategory().equals(sub)) {
                filtered.add(fd);
            }
        }
        adapter.updateList(filtered);
    }

    private void setupChart(float[] current) {
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < current.length; i++) {
            float percent = Math.min(current[i] / recommended[i] * 100f, 100f);
            entries.add(new BarEntry(i, percent));
        }

        BarDataSet dataSet = new BarDataSet(entries, "섭취 비율 (%)");
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f);
        deficitChart.setData(barData);
        deficitChart.setFitBars(true);
        deficitChart.getDescription().setEnabled(false);

        XAxis xAxis = deficitChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        deficitChart.getAxisRight().setEnabled(false);
        deficitChart.invalidate();
    }

    private List<FoodDistance> getRecommendations(String vectorString, String deficitString) {
        float[] vector = parseVector(vectorString);
        float[] weight = parseVector(deficitString);
        float[] min = {0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
        float[] max = {595f, 104f, 41.55f, 60.25f, 70f, 7.402f, 0.68662f, 25.6f};

        FoodDao dao = new FoodDao(this);
        List<Food> foods = dao.searchFoodByName("");
        List<FoodDistance> result = new ArrayList<>();

        for (Food food : foods) {
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

            float[] norm = new float[vec.length];
            for (int i = 0; i < vec.length; i++) {
                norm[i] = (vec[i] - min[i]) / (max[i] - min[i]);
            }

            float satisfaction = 0f;
            int count = 0;
            for (int i = 0; i < vec.length; i++) {
                if (weight[i] > 0) {
                    satisfaction += Math.min(1f, norm[i] / weight[i]);
                    count++;
                }
            }
            satisfaction = (count > 0) ? satisfaction / count : 0f;

            float cosine = cosineSimilarity(norm, vector, weight);
            float score = (satisfaction + cosine) / 2f;

            result.add(new FoodDistance(food.getName(), 100, score, food));
        }

        result.sort(Collections.reverseOrder());
        return result;
    }

    private float cosineSimilarity(float[] a, float[] b, float[] w) {
        float dot = 0, normA = 0, normB = 0;
        for (int i = 0; i < a.length; i++) {
            dot += w[i] * a[i] * b[i];
            normA += w[i] * a[i] * a[i];
            normB += w[i] * b[i] * b[i];
        }
        return (normA == 0 || normB == 0) ? 0f : dot / ((float) Math.sqrt(normA) * (float) Math.sqrt(normB));
    }

    private float[] parseVector(String str) {
        String[] parts = str.replace("[", "").replace("]", "").split(",");
        float[] v = new float[parts.length];
        for (int i = 0; i < parts.length; i++) {
            v[i] = Float.parseFloat(parts[i].trim());
        }
        return v;
    }

    // 추천 음식 정렬용 클래스
    static class FoodDistance implements Comparable<FoodDistance> {
        String name;
        int gram;
        float score;
        Food food;

        FoodDistance(String name, int gram, float score, Food food) {
            this.name = name;
            this.gram = gram;
            this.score = score;
            this.food = food;
        }

        @Override
        public int compareTo(FoodDistance o) {
            return Float.compare(this.score,o.score);
        }
    }
}
