package com.example.opensource_team6;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MealPhotoFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal_photo, container, false);

        Button btnSnackHistory = view.findViewById(R.id.btnSnackHistory);
        btnSnackHistory.setOnClickListener(v -> openHistory("snack"));
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MealPhotoFragment extends Fragment {
    private ImageView imgBreakfast, imgLunch, imgDinner, imgSnack;
    private final Map<String, ActivityResultLauncher<Uri>> launchers = new HashMap<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal_photo, container, false);

        imgBreakfast = view.findViewById(R.id.imgBreakfast);
        imgLunch = view.findViewById(R.id.imgLunch);
        imgDinner = view.findViewById(R.id.imgDinner);
        imgSnack = view.findViewById(R.id.imgSnack);
        Button btnBreakfastHistory = view.findViewById(R.id.btnBreakfastHistory);
        Button btnLunchHistory = view.findViewById(R.id.btnLunchHistory);
        Button btnDinnerHistory = view.findViewById(R.id.btnDinnerHistory);

        registerLauncher("breakfast", imgBreakfast);
        registerLauncher("lunch", imgLunch);
        registerLauncher("dinner", imgDinner);
        registerLauncher("snack", imgSnack);

        btnBreakfastHistory.setOnClickListener(v -> openHistory("breakfast"));
        btnLunchHistory.setOnClickListener(v -> openHistory("lunch"));
        btnDinnerHistory.setOnClickListener(v -> openHistory("dinner"));

        loadTodayPhotos();
        return view;
    }

    private void openHistory(String meal) {
        if (getActivity() == null) return;
        Intent intent = new Intent(getActivity(), MealPhotoHistoryActivity.class);
        intent.putExtra("meal", meal);
        startActivity(intent);
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
        File dir = new File(requireContext().getExternalFilesDir(null), "meal_photos");
        if (!dir.exists()) dir.mkdirs();
        String date = dateFormat.format(new Date());
        File file = new File(dir, date + "_" + meal + ".jpg");
        return FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".fileprovider", file);
    }

    private void loadTodayPhotos() {
        String date = dateFormat.format(new Date());
        File dir = new File(requireContext().getExternalFilesDir(null), "meal_photos");
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
