package com.example.opensource_team6;

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

        // 검색어 기반 필터링
        List<Food> filteredList = new ArrayList<>();
        for (Food food : foodList) {
            if (food.getName().toLowerCase().contains(keyword.toLowerCase())) {
                filteredList.add(food);
            }
        }

        // 결과 출력
        if (filteredList.isEmpty()) {
            TextView item = new TextView(this);
            item.setText("검색 결과가 없습니다.");
            item.setTextSize(16);
            item.setPadding(8, 8, 8, 8);
            resultContainer.addView(item);
        } else {
            for (Food food : filteredList) {
                TextView item = new TextView(this);
                item.setText(food.getName() + " - " + food.getKcal() + " kcal");
                item.setTextSize(16);
                item.setPadding(8, 8, 8, 8);
                resultContainer.addView(item);
            }
        }
    }
}
