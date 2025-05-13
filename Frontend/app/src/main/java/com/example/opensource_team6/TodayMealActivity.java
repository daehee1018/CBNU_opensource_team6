package com.example.opensource_team6;

import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TodayMealActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private TextView mealInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_meal);

        calendarView = findViewById(R.id.calendarView);
        mealInfo = findViewById(R.id.mealInfo);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String date = year + "년 " + (month + 1) + "월 " + dayOfMonth + "일";
            mealInfo.setText(date + " 식단 정보 없음 (샘플 텍스트)");
        });
    }
}