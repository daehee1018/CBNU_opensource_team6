package com.example.opensource_team6;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.opensource_team6.data.Food;
import com.example.opensource_team6.data.FoodDao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FoodDetailActivity extends AppCompatActivity {

    private TextView tvName, tvEnergy, tvCarbs, tvProtein, tvFat;
    private EditText etAmount;
    private Spinner spinnerMealType;
    private Button btnAddToMeal;

    private Food selectedFood;
    private String mealType = "조식"; // 기본값

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        // 뷰 초기화
        tvName = findViewById(R.id.tvFoodName);
        tvEnergy = findViewById(R.id.tvEnergy);
        tvCarbs = findViewById(R.id.tvCarbs);
        tvProtein = findViewById(R.id.tvProtein);
        tvFat = findViewById(R.id.tvFat);
        etAmount = findViewById(R.id.etAmount);
        spinnerMealType = findViewById(R.id.spinnerMealType);
        btnAddToMeal = findViewById(R.id.btnAddToMeal);

        // 스피너 설정
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.meal_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMealType.setAdapter(adapter);
        spinnerMealType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mealType = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // 음식 정보 받기
        String foodName = getIntent().getStringExtra("food_name");
        if (foodName != null) {
            FoodDao dao = new FoodDao(this);
            selectedFood = dao.getFoodByName(foodName);
            if (selectedFood != null) {
                tvName.setText(selectedFood.getName());
                tvEnergy.setText("에너지: " + selectedFood.getEnergy() + " kcal");
                tvCarbs.setText("탄수화물: " + selectedFood.getCarbohydrate() + " g");
                tvProtein.setText("단백질: " + selectedFood.getProtein() + " g");
                tvFat.setText("지방: " + selectedFood.getFat() + " g");
            }
        }

        btnAddToMeal.setOnClickListener(v -> {
            String amountStr = etAmount.getText().toString();
            if (amountStr.isEmpty()) {
                Toast.makeText(this, "섭취량을 입력하세요", Toast.LENGTH_SHORT).show();
                return;
            }

            float amount = Float.parseFloat(amountStr);
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            String key = today + "_" + mealType;

            String value = selectedFood.getName() + ":" + amount + "g";

            SharedPreferences prefs = getSharedPreferences("MealRecords", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            String existing = prefs.getString(key, "");
            if (!existing.isEmpty()) {
                value = existing + "," + value;
            }
            editor.putString(key, value);
            editor.apply();

            Toast.makeText(this, "식단에 추가되었습니다", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
