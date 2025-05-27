package com.example.opensource_team6;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.content.Intent;

public class FoodDetailActivity extends AppCompatActivity {

    private TextView foodName, foodKcal;
    private ImageView foodImage;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        Intent intent = getIntent();
        foodName = findViewById(R.id.foodName);
        foodKcal = findViewById(R.id.foodKcal);
        foodImage = findViewById(R.id.foodImage);
        btnRegister = findViewById(R.id.btnRegister);
        LinearLayout foodDetail = findViewById(R.id.foodDetail);


        String name = intent.getStringExtra("food_name");
        float kcal = intent.getFloatExtra("food_kcal", 0);
        float carbs = intent.getFloatExtra("food_carbs", 0);
        float protein = intent.getFloatExtra("food_protein", 0);
        float fat = intent.getFloatExtra("food_fat", 0);

        foodName.setText(name);
        foodKcal.setText(String.format("%.1f kcal", kcal));

        // 나머지 텍스트뷰는 위 XML처럼 직접 작성 가능 (혹은 동적 생성도 가능)

        btnRegister.setOnClickListener(v -> {
            Toast.makeText(this, "식단에 추가되었습니다!", Toast.LENGTH_SHORT).show();
            // 혹시 추가 기능 원하면 여기에 로직 작성
        });
    }
}

