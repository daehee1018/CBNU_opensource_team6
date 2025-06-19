package com.example.opensource_team6;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.opensource_team6.network.ApiConfig;
import com.example.opensource_team6.util.TokenManager;
import androidx.appcompat.app.AppCompatActivity;
import com.example.opensource_team6.data.Food;
import com.example.opensource_team6.data.FoodDao;
import org.json.JSONObject;
import java.util.Map;
import java.util.HashMap;

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

        // ============================
        // 🔄 추천/검색 방식 둘 다 처리
        // ============================
        selectedFood = null;

        // 1. Serializable 객체로 전달된 경우 (추천 결과)
        Food foodFromIntent = (Food) getIntent().getSerializableExtra("food");
        if (foodFromIntent != null) {
            selectedFood = foodFromIntent;
        }

        // 2. 이름으로 전달된 경우 (검색 결과)
        if (selectedFood == null) {
            String foodName = getIntent().getStringExtra("food_name");
            if (foodName != null) {
                FoodDao dao = new FoodDao(this);
                selectedFood = dao.getFoodByName(foodName);
            }
        }

        // 3. UI 세팅
        if (selectedFood != null) {
            tvName.setText(selectedFood.getName());
            tvEnergy.setText("에너지: " + selectedFood.getEnergy() + " kcal");
            tvCarbs.setText("탄수화물: " + selectedFood.getCarbohydrate() + " g");
            tvProtein.setText("단백질: " + selectedFood.getProtein() + " g");
            tvFat.setText("지방: " + selectedFood.getFat() + " g");
        } else {
            Toast.makeText(this, "음식 정보를 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
            finish();
            return;
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

            sendDietToServer(amount, today);
        });
    }

    private void sendDietToServer(float amount, String date) {
        String token = TokenManager.getToken(this);
        if (token == null) {
            Toast.makeText(this, "로그인이 필요합니다", Toast.LENGTH_SHORT).show();
            return;
        }

        String meal = convertMeal(mealType);

        String url = ApiConfig.BASE_URL + "/api/diet";
        JSONObject body = new JSONObject();
        try {
            body.put("foodName", selectedFood.getName());
            body.put("amount", amount);
            body.put("mealTime", meal);
            body.put("date", date);
        } catch (Exception e) {
            Toast.makeText(this, "데이터 생성 실패", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                response -> {
                    Toast.makeText(this, "식단에 추가되었습니다", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> Toast.makeText(this, "서버 오류", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private String convertMeal(String type) {
        switch (type) {
            case "조식":
                return "아침";
            case "중식":
                return "점심";
            case "석식":
            case "야식":
                return "저녁";
            default:
                return "아침";
        }
    }
}
