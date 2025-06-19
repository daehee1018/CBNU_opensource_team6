package com.example.opensource_team6;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal_photo, container, false);

        // 이미지 뷰 초기화
        imgBreakfast = view.findViewById(R.id.imgBreakfast);
        imgLunch = view.findViewById(R.id.imgLunch);
        imgDinner = view.findViewById(R.id.imgDinner);
        imgSnack = view.findViewById(R.id.imgSnack);

        // 버튼 초기화
        Button btnBreakfastHistory = view.findViewById(R.id.btnBreakfastHistory);
        Button btnLunchHistory = view.findViewById(R.id.btnLunchHistory);
        Button btnDinnerHistory = view.findViewById(R.id.btnDinnerHistory);
        Button btnSnackHistory = view.findViewById(R.id.btnSnackHistory);
        Button btnBreakfastAnalyze = view.findViewById(R.id.btnBreakfastAnalyze);
        Button btnLunchAnalyze = view.findViewById(R.id.btnLunchAnalyze);
        Button btnDinnerAnalyze = view.findViewById(R.id.btnDinnerAnalyze);

        // 런처 등록
        registerLauncher("breakfast", imgBreakfast);
        registerLauncher("lunch", imgLunch);
        registerLauncher("dinner", imgDinner);
        registerLauncher("snack", imgSnack);

        // 버튼 이벤트
        btnBreakfastHistory.setOnClickListener(v -> openHistory("breakfast"));
        btnLunchHistory.setOnClickListener(v -> openHistory("lunch"));
        btnDinnerHistory.setOnClickListener(v -> openHistory("dinner"));
        btnSnackHistory.setOnClickListener(v -> openHistory("snack"));

        btnBreakfastAnalyze.setOnClickListener(v -> analyzeMeal("breakfast", "아침"));
        btnLunchAnalyze.setOnClickListener(v -> analyzeMeal("lunch", "점심"));
        btnDinnerAnalyze.setOnClickListener(v -> analyzeMeal("dinner", "저녁"));

        // 오늘 사진 불러오기
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
        ActivityResultLauncher<Uri> launcher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
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

        return FileProvider.getUriForFile(
                requireContext(),
                requireContext().getPackageName() + ".fileprovider",
                file
        );
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

    private File getPhotoFile(String meal) {
        File dir = new File(requireContext().getExternalFilesDir(null), "meal_photos");
        String date = dateFormat.format(new Date());
        return new File(dir, date + "_" + meal + ".jpg");
    }

    private void analyzeMeal(String mealKey, String mealTime) {
        File file = getPhotoFile(mealKey);
        if (!file.exists()) {
            android.widget.Toast.makeText(getContext(), "사진이 없습니다", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
        okhttp3.RequestBody imageBody = okhttp3.RequestBody.create(file, okhttp3.MediaType.parse("image/jpeg"));
        okhttp3.MultipartBody requestBody = new okhttp3.MultipartBody.Builder()
                .setType(okhttp3.MultipartBody.FORM)
                .addFormDataPart("image", file.getName(), imageBody)
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(com.example.opensource_team6.network.ApiConfig.BASE_URL + "/api/diet/image/preview")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, java.io.IOException e) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> android.widget.Toast.makeText(getContext(), "분석 실패", android.widget.Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws java.io.IOException {
                if (!response.isSuccessful()) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> android.widget.Toast.makeText(getContext(), "분석 실패", android.widget.Toast.LENGTH_SHORT).show());
                    }
                    return;
                }
                String body = response.body().string();
                try {
                    org.json.JSONObject obj = new org.json.JSONObject(body);
                    org.json.JSONObject data = obj.optJSONObject("data");
                    if (data == null) throw new Exception();
                    String name = data.optString("name");
                    double kcal = data.optDouble("energy");
                    double carb = data.optDouble("carbohydrate");
                    double protein = data.optDouble("protein");
                    double fat = data.optDouble("fat");

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> showAnalysisDialog(name, kcal, carb, protein, fat, file, mealTime));
                    }
                } catch (Exception e) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> android.widget.Toast.makeText(getContext(), "분석 실패", android.widget.Toast.LENGTH_SHORT).show());
                    }
                }
            }
        });
    }

    private void showAnalysisDialog(String name, double kcal, double carb, double protein, double fat, File file, String mealTime) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("분석 결과")
                .setMessage(name + "\n열량: " + kcal + "kcal\n탄수화물: " + carb + "g\n단백질: " + protein + "g\n지방: " + fat + "g")
                .setPositiveButton("등록", (d, which) -> registerAnalyzedMeal(file, mealTime))
                .setNegativeButton("취소", null)
                .show();
    }

    private void registerAnalyzedMeal(File file, String mealTime) {
        String token = com.example.opensource_team6.util.TokenManager.getToken(requireContext());
        if (token == null) {
            android.widget.Toast.makeText(getContext(), "로그인이 필요합니다", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
        okhttp3.RequestBody imageBody = okhttp3.RequestBody.create(file, okhttp3.MediaType.parse("image/jpeg"));
        okhttp3.MultipartBody requestBody = new okhttp3.MultipartBody.Builder()
                .setType(okhttp3.MultipartBody.FORM)
                .addFormDataPart("image", file.getName(), imageBody)
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(com.example.opensource_team6.network.ApiConfig.BASE_URL + "/api/diet/image")
                .addHeader("Authorization", "Bearer " + token)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, java.io.IOException e) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> android.widget.Toast.makeText(getContext(), "업로드 실패", android.widget.Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws java.io.IOException {
                if (getActivity() == null) return;
                if (!response.isSuccessful()) {
                    getActivity().runOnUiThread(() -> android.widget.Toast.makeText(getContext(), "업로드 실패", android.widget.Toast.LENGTH_SHORT).show());
                    return;
                }

                String body = response.body().string();
                try {
                    org.json.JSONObject obj = new org.json.JSONObject(body);
                    org.json.JSONObject data = obj.optJSONObject("data");
                    if (data == null) throw new Exception();

                    com.example.opensource_team6.data.Food food = new com.example.opensource_team6.data.Food();
                    food.setName(data.optString("name"));
                    food.setEnergy(data.optDouble("energy"));
                    food.setCarbohydrate(data.optDouble("carbohydrate"));
                    food.setProtein(data.optDouble("protein"));
                    food.setFat(data.optDouble("fat"));

                    Intent intent = new Intent(getContext(), FoodDetailActivity.class);
                    intent.putExtra("food", food);
                    intent.putExtra("meal_time", mealTime);
                    startActivity(intent);
                } catch (Exception e) {
                    getActivity().runOnUiThread(() -> android.widget.Toast.makeText(getContext(), "업로드 실패", android.widget.Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
