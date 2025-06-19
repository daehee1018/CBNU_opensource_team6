package com.example.opensource_team6;

import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.Arrays;

public class MealPhotoHistoryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_photo_history);

        LinearLayout historyList = findViewById(R.id.historyList);
        String meal = getIntent().getStringExtra("meal");
        File dir = new File(getExternalFilesDir(null), "meal_photos");
        if (!dir.exists()) return;

        File[] files = dir.listFiles();
        if (files == null) return;
        Arrays.sort(files, (a, b) -> Long.compare(b.lastModified(), a.lastModified()));

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        long now = System.currentTimeMillis();
        long sevenDays = 7L * 24 * 60 * 60 * 1000;
        int count = 0;

        for (File f : files) {
            String name = f.getName();
            String[] parts = name.split("_");
            if (parts.length != 2) continue;
            String datePart = parts[0];
            String mealPart = parts[1].replace(".jpg", "");
            if (!mealPart.equals(meal)) continue;
            try {
                Date d = df.parse(datePart);
                if (d == null) continue;
                if (now - d.getTime() > sevenDays) continue;
            } catch (ParseException e) {
                continue;
            }
            TextView tv = new TextView(this);
            tv.setText(name);
            ImageView iv = new ImageView(this);
            iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            iv.setAdjustViewBounds(true);
            iv.setImageURI(Uri.fromFile(f));
            historyList.addView(tv);
            historyList.addView(iv);
            count++;
            if (count >= 7) break;
        }
    }
}
