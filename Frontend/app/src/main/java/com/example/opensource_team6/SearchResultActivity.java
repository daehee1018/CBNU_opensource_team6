package com.example.opensource_team6;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.opensource_team6.data.Food;
import com.example.opensource_team6.data.FoodAdapter;
import com.example.opensource_team6.data.FoodDao;

import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends AppCompatActivity {

    private List<Food> foodList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        TextView resultText = findViewById(R.id.resultTextView);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewResults);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2열 그리드

        // 검색어 받기
        String keyword = getIntent().getStringExtra("search_keyword");
        resultText.setText("‘" + keyword + "’에 대한 검색 결과:");

        // DB 검색
        FoodDao dao = new FoodDao(this);
        List<Food> results = dao.searchFoodByName(keyword);

        // 필터링
        List<Food> filtered = new ArrayList<>();
        for (Food food : results) {
            if (food.getName().toLowerCase().contains(keyword.toLowerCase())) {
                filtered.add(food);
            }
        }
        recyclerView.addItemDecoration(new SpaceItemDecoration(8)); // 항목 간 간격 8dp


        if (filtered.isEmpty()) {
            Toast.makeText(this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
        }

        // RecyclerView에 어댑터 연결
        recyclerView.setAdapter(new FoodAdapter(this, filtered));

    }
    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        private final int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space / 2;
            outRect.right = space / 2;
            outRect.bottom = space;

            // 첫 줄에는 위쪽 간격도 줌
            if (parent.getChildAdapterPosition(view) < 2) {
                outRect.top = space;
            }
        }
    }

}
