package com.example.opensource_team6;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnInputFood, btnViewFood, btnRecommendSupplements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnInputFood = findViewById(R.id.btnInputFood);
        btnViewFood = findViewById(R.id.btnViewFood);
        btnRecommendSupplements = findViewById(R.id.btnRecommendSupplements);

        btnInputFood.setOnClickListener(v -> {
            // 예: 음식 입력 액티비티로 이동
            startActivity(new Intent(this, MainActivity.class));
        });

        btnViewFood.setOnClickListener(v -> {
            // 예: 음식 리스트 액티비티로 이동
            startActivity(new Intent(this, MainActivity.class));
        });

        btnRecommendSupplements.setOnClickListener(v -> {
            // 예: 추천 화면으로 이동
            startActivity(new Intent(this, MainActivity.class));
        });
    }
}
