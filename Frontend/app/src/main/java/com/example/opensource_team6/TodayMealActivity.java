package com.example.opensource_team6;

import android.content.Intent;
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
        CalendarView calendarView = findViewById(R.id.calendarView);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String dateString = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);

            Intent intent = new Intent(TodayMealActivity.this, MealRecordDetailActivity.class);
            intent.putExtra("selectedDate", dateString); // 필요 시 날짜 전달
            startActivity(intent);
        });



    }
}