package com.example.opensource_team6;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MealPhotoActivity extends AppCompatActivity {
    private ImageView imgBreakfast, imgLunch, imgDinner, imgSnack;
    private final Map<String, ActivityResultLauncher<Uri>> launchers = new HashMap<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_photo);

        imgBreakfast = findViewById(R.id.imgBreakfast);
        imgLunch = findViewById(R.id.imgLunch);
        imgDinner = findViewById(R.id.imgDinner);
        imgSnack = findViewById(R.id.imgSnack);
        Button btnHistory = findViewById(R.id.btnHistory);

        registerLauncher("breakfast", imgBreakfast);
        registerLauncher("lunch", imgLunch);
        registerLauncher("dinner", imgDinner);
        registerLauncher("snack", imgSnack);

        btnHistory.setOnClickListener(v -> startActivity(new Intent(this, MealPhotoHistoryActivity.class)));

        loadTodayPhotos();
    }

    private void registerLauncher(String meal, ImageView view) {
        ActivityResultLauncher<Uri> launcher = registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
            if (success) loadTodayPhotos();
        });
        launchers.put(meal, launcher);
        view.setOnClickListener(v -> {
            Uri uri = getPhotoUri(meal);
            launcher.launch(uri);
        });
    }

    private Uri getPhotoUri(String meal) {
        File dir = new File(getExternalFilesDir(null), "meal_photos");
        if (!dir.exists()) dir.mkdirs();
        String date = dateFormat.format(new Date());
        File file = new File(dir, date + "_" + meal + ".jpg");
        return FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
    }

    private void loadTodayPhotos() {
        String date = dateFormat.format(new Date());
        File dir = new File(getExternalFilesDir(null), "meal_photos");
        setImage(imgBreakfast, new File(dir, date + "_breakfast.jpg"));
        setImage(imgLunch, new File(dir, date + "_lunch.jpg"));
        setImage(imgDinner, new File(dir, date + "_dinner.jpg"));
        setImage(imgSnack, new File(dir, date + "_snack.jpg"));
    }

    private void setImage(ImageView view, File file) {
        if (file.exists()) {
            view.setImageURI(Uri.fromFile(file));
        } else {
            view.setImageResource(android.R.drawable.ic_menu_camera);
        }
    }
}
