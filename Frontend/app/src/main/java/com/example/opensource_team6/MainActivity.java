package com.example.opensource_team6;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.opensource_team6.model.Food;


public class MainActivity extends AppCompatActivity {

    private List<Food> foodList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadFoodData(); // ✅ JSON 파일에서 음식 리스트 로딩
        // onCreate 내부에 추가
        AutoCompleteTextView foodInput = findViewById(R.id.foodInput);
        EditText foodAmount = findViewById(R.id.foodAmount);
        Button addFoodBtn = findViewById(R.id.addFoodBtn);
        TextView resultText = findViewById(R.id.resultText);

// 누적된 섭취 영양소를 저장할 변수
        final float[] totalKcal = {0};
        final float[] totalCarbs = {0};
        final float[] totalProtein = {0};
        final float[] totalFat = {0};
        Button btnBreakfast = findViewById(R.id.btnBreakfast);
        btnBreakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "아침 클릭됨", Toast.LENGTH_SHORT).show();
            }
        });
        addFoodBtn.setOnClickListener(v -> {
            String foodName = foodInput.getText().toString().trim();
            String amountStr = foodAmount.getText().toString().trim();

            if (foodName.isEmpty() || amountStr.isEmpty()) {
                Toast.makeText(this, "음식 이름과 섭취량을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            float amount = Float.parseFloat(amountStr);

            // 음식 찾기
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

            float baseWeight = matched.getWeight(); // g
            totalKcal[0] += matched.getKcal() / baseWeight * amount;
            totalCarbs[0] += matched.getCarbs() / baseWeight * amount;
            totalProtein[0] += matched.getProtein() / baseWeight * amount;
            totalFat[0] += matched.getFat() / baseWeight * amount;

            resultText.setText(
                    String.format("총 섭취량:\n칼로리: %.1f kcal\n탄수화물: %.1fg\n단백질: %.1fg\n지방: %.1fg",
                            totalKcal[0], totalCarbs[0], totalProtein[0], totalFat[0])
            );

            foodInput.setText("");
            foodAmount.setText("");
        });

        // 음식 이름 리스트 생성
        List<String> foodNames = new ArrayList<>();
        for (Food item : foodList) {
            foodNames.add(item.getName());
        }

        // 어댑터 설정
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, foodNames);
        foodInput.setAdapter(adapter);
        if (foodList != null) {
            for (Food item : foodList) {
                Log.d("Food", item.getName() + " / " + item.getKcal() + " kcal");
            }
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                Toast.makeText(this, "Home 클릭됨", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_diary) {
                Toast.makeText(this, "Diary 클릭됨", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_scan) {
                Toast.makeText(this, "Scan 클릭됨", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_profile) {
                Toast.makeText(this, "Profile 클릭됨", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }

    private void loadFoodData() {
        try {
            AssetManager assetManager = getAssets();
            InputStream is = assetManager.open("food_db.json");
            InputStreamReader reader = new InputStreamReader(is, "UTF-8");

            Type listType = new TypeToken<List<Food>>() {}.getType();
            foodList = new Gson().fromJson(reader, listType);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "음식 데이터를 불러오지 못했습니다.", Toast.LENGTH_LONG).show();
        }
    }
}
