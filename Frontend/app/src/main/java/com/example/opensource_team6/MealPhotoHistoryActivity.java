package com.example.opensource_team6;

import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.Arrays;

public class MealPhotoHistoryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_photo_history);

        LinearLayout historyList = findViewById(R.id.historyList);
        File dir = new File(getExternalFilesDir(null), "meal_photos");
        if (!dir.exists()) return;

        File[] files = dir.listFiles();
        if (files == null) return;
        Arrays.sort(files, (a, b) -> Long.compare(b.lastModified(), a.lastModified()));

        for (File f : files) {
            TextView tv = new TextView(this);
            tv.setText(f.getName());
            ImageView iv = new ImageView(this);
            iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            iv.setAdjustViewBounds(true);
            iv.setImageURI(Uri.fromFile(f));
            historyList.addView(tv);
            historyList.addView(iv);
        }
    }
}
