package com.example.opensource_team6;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.example.opensource_team6.model.Food;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends AppCompatActivity {

    private List<Food> foodList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        TextView resultText = findViewById(R.id.resultTextView);
        LinearLayout resultContainer = findViewById(R.id.resultContainer);

        // 검색어 받기
        String keyword = getIntent().getStringExtra("search_keyword");
        resultText.setText("‘" + keyword + "’에 대한 검색 결과:");

        // JSON 데이터 로드
        try {
            AssetManager assetManager = getAssets();
            InputStream is = assetManager.open("food_db.json");
            InputStreamReader reader = new InputStreamReader(is, "UTF-8");
            Type listType = new TypeToken<List<Food>>() {}.getType();
            foodList = new Gson().fromJson(reader, listType);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "음식 데이터를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 검색어 필터링 후 리스트에 항목 추가
        boolean hasResult = false;
        for (Food item : foodList) {
            if (item.getName().toLowerCase().contains(keyword.toLowerCase())) {
                hasResult = true;

                TextView textView = new TextView(this);
                textView.setText(item.getName() + " - " + item.getKcal() + " kcal");
                textView.setPadding(20, 20, 20, 20);
                textView.setTextSize(16);
                textView.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);
                textView.setOnClickListener(v -> {
                    Intent intent = new Intent(SearchResultActivity.this, FoodDetailActivity.class);
                    intent.putExtra("food_name", item.getName());
                    intent.putExtra("food_kcal", item.getKcal());
                    intent.putExtra("food_carbs", item.getCarbs());
                    intent.putExtra("food_protein", item.getProtein());
                    intent.putExtra("food_fat", item.getFat());
                    startActivity(intent);
                });

                resultContainer.addView(textView);
            }
        }

        if (!hasResult) {
            TextView item = new TextView(this);
            item.setText("검색 결과가 없습니다.");
            item.setTextSize(16);
            item.setPadding(8, 8, 8, 8);
            resultContainer.addView(item);
        }
    }
}