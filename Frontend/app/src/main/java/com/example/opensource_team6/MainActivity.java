package com.example.opensource_team6;

import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.content.Intent;

import com.example.opensource_team6.data.Food;
import com.example.opensource_team6.data.FoodDao;
import com.example.opensource_team6.profile.*;

public class MainActivity extends AppCompatActivity {

    private List<Food> foodList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AutoCompleteTextView foodInput = findViewById(R.id.foodInput);
        EditText foodAmount = findViewById(R.id.foodAmount);
        Button addFoodBtn = findViewById(R.id.addFoodBtn);
        TextView resultText = findViewById(R.id.resultText);

        LinearLayout groupBreakfast = findViewById(R.id.groupBreakfast);
        LinearLayout groupLunch = findViewById(R.id.groupLunch);
        LinearLayout groupDinner = findViewById(R.id.groupDinner);

        Button btnBreakfast = findViewById(R.id.btnBreakfast);
        Button btnLunch = findViewById(R.id.btnLunch);
        Button btnDinner = findViewById(R.id.btnDinner);

        btnBreakfast.setOnClickListener(v -> {
            groupBreakfast.setVisibility(View.VISIBLE);
            groupLunch.setVisibility(View.GONE);
            groupDinner.setVisibility(View.GONE);
        });

        btnLunch.setOnClickListener(v -> {
            groupBreakfast.setVisibility(View.GONE);
            groupLunch.setVisibility(View.VISIBLE);
            groupDinner.setVisibility(View.GONE);
        });

        btnDinner.setOnClickListener(v -> {
            groupBreakfast.setVisibility(View.GONE);
            groupLunch.setVisibility(View.GONE);
            groupDinner.setVisibility(View.VISIBLE);
        });

        final float[] totalKcal = {0};
        final float[] totalCarbs = {0};
        final float[] totalProtein = {0};
        final float[] totalFat = {0};

        // SQLite DB에서 데이터 가져오기
        FoodDao dao = new FoodDao(this);
        foodList = dao.searchFoodByName(""); // 전체 데이터 불러오기용

        addFoodBtn.setOnClickListener(v -> {
            String foodName = foodInput.getText().toString().trim();
            String amountStr = foodAmount.getText().toString().trim();

            if (foodName.isEmpty() || amountStr.isEmpty()) {
                Toast.makeText(this, "음식 이름과 섭취량을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            float amount = Float.parseFloat(amountStr);
            Food matched = null;
            for (Food item : foodList) {
                if (item.getName().contains(foodName)) {
                    matched = item;
                    break;
                }
            }

            if (matched == null) {
                Toast.makeText(this, "해당 음식이 DB에 없습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            double baseWeight = matched.getWeight();
            if (baseWeight == 0) {
                Toast.makeText(this, "해당 음식의 기준량이 없습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            totalKcal[0] += matched.getEnergy() / baseWeight * amount;
            totalCarbs[0] += matched.getCarbohydrate() / baseWeight * amount;
            totalProtein[0] += matched.getProtein() / baseWeight * amount;
            totalFat[0] += matched.getFat() / baseWeight * amount;

            totalKcal[0] += matched.getEnergy() / baseWeight * amount;
            totalCarbs[0] += matched.getCarbohydrate() / baseWeight * amount;
            totalProtein[0] += matched.getProtein() / baseWeight * amount;
            totalFat[0] += matched.getFat() / baseWeight * amount;

            resultText.setText(
                    String.format("총 섭취량:\n칼로리: %.1f kcal\n탄수화물: %.1fg\n단백질: %.1fg\n지방: %.1fg",
                            totalKcal[0], totalCarbs[0], totalProtein[0], totalFat[0])
            );

            foodInput.setText("");
            foodAmount.setText("");
        });

        AutoCompleteTextView searchEditText = findViewById(R.id.searchEditText);
        searchEditText.setFocusable(false);
        searchEditText.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        });

        List<String> foodNames = new ArrayList<>();
        for (Food item : foodList) {
            foodNames.add(item.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, foodNames);
        foodInput.setAdapter(adapter);

        searchEditText = findViewById(R.id.searchEditText);
        searchEditText.setAdapter(adapter);
        searchEditText.setThreshold(1);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                Toast.makeText(this, "Home 클릭됨", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_diary) {
                Intent intent = new Intent(MainActivity.this, TodayMealActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_scan) {
                Toast.makeText(this, "식단 사진 클릭됨", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_profile) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new profileFragment()) // 여기에 사용할 container ID
                        .commit();
                return true;
            }

            return false;
        });
    }
}
