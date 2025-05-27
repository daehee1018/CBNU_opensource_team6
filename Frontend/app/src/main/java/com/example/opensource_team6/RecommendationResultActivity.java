package com.example.opensource_team6;


import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
public class RecommendationResultActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation_result);

        TextView resultText = findViewById(R.id.recommendationText);
        String vector = getIntent().getStringExtra("recommendationVector");
        resultText.setText("입력된 벡터:\n" + vector);
    }
}

