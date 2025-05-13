package com.example.opensource_team6;

import com.example.opensource_team6.model.Food;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.content.Intent;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;
import android.content.Intent;

public class SearchActivity extends AppCompatActivity {

    private List<Food> foodList;
    private List<String> recentSearches = new ArrayList<>();
    private LinearLayout recentSearchContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_food);

        AutoCompleteTextView searchEditText = findViewById(R.id.searchEditText);
        Button btnSearch = findViewById(R.id.btnSearch);
        recentSearchContainer = findViewById(R.id.recentSearchContainer);

        // JSON 데이터 로드
        try {
            InputStream is = getAssets().open("food_db.json");
            InputStreamReader reader = new InputStreamReader(is, "UTF-8");
            Type listType = new TypeToken<List<Food>>() {}.getType();
            foodList = new Gson().fromJson(reader, listType);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "데이터 로드 실패", Toast.LENGTH_SHORT).show();
        }

        // 자동완성용 이름 리스트
        List<String> foodNames = new ArrayList<>();
        for (Food item : foodList) {
            foodNames.add(item.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, foodNames);
        searchEditText.setAdapter(adapter);
        searchEditText.setThreshold(1);

        // 검색 버튼 클릭 → 최근 검색 저장 + 결과 화면 이동 (임시로 토스트 처리)
        btnSearch.setOnClickListener(v -> {
            String query = searchEditText.getText().toString().trim();
            if (query.isEmpty()) {
                Toast.makeText(this, "검색어를 입력하세요", Toast.LENGTH_SHORT).show();
                return;
            }

            // 최근 검색 저장 (중복 제거)
            if (!recentSearches.contains(query)) {
                recentSearches.add(0, query); // 최근 검색은 위에 추가
                updateRecentSearchUI();
            }

            Intent intent = new Intent(SearchActivity.this, SearchResultActivity.class);
            intent.putExtra("search_keyword", query); // 검색어 전달
            startActivity(intent);

        });
    }

    private void updateRecentSearchUI() {
        recentSearchContainer.removeAllViews();
        for (String keyword : recentSearches) {
            TextView item = new TextView(this);
            item.setText(keyword);
            item.setPadding(8, 8, 8, 8);
            item.setTextSize(14);
            recentSearchContainer.addView(item);
        }
    }
}
