package com.example.opensource_team6;

import com.example.opensource_team6.data.FoodDao;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.example.opensource_team6.data.Food;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
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


        FoodDao dao = new FoodDao(this);
        List<Food> foodList = dao.searchFoodByName(keyword);

        // 검색어 필터링 후 리스트에 항목 추가
        boolean hasResult = false;
        for (Food item : foodList) {
            if (item.getName().toLowerCase().contains(keyword.toLowerCase())) {
                hasResult = true;

                TextView textView = new TextView(this);
                textView.setText(item.getName() + " - " + item.getEnergy() + " kcal");
                textView.setPadding(20, 20, 20, 20);
                textView.setTextSize(16);
                textView.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);
                textView.setOnClickListener(v -> {
                    Intent intent = new Intent(SearchResultActivity.this, FoodDetailActivity.class);
                    intent.putExtra("food_name", item.getName());
                    intent.putExtra("food_kcal", item.getEnergy());
                    intent.putExtra("food_carbs", item.getCarbohydrate());
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