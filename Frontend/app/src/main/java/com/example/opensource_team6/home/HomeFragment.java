/*package com.example.opensource_team6.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CalendarView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.opensource_team6.R;
import com.example.opensource_team6.data.Food;
import com.example.opensource_team6.data.FoodDao;
import com.example.opensource_team6.data.FoodAdapter;
import com.example.opensource_team6.network.ApiConfig;
import com.example.opensource_team6.util.TokenManager;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private List<Food> foodList;
    private RecyclerView searchResults;
    private FoodAdapter searchAdapter;
    private LinearLayout calendarLayout;
    private LinearLayout mealButtonsLayout;
    private LinearLayout foodListLayout;

    private LinearLayout groupBreakfast;
    private LinearLayout groupLunch;
    private LinearLayout groupDinner;
    private LinearLayout groupSnack;

    private int currentMeal = 0;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        final TextView resultText = view.findViewById(R.id.resultText);
        CalendarView calendarView = view.findViewById(R.id.calendar);

        calendarLayout = view.findViewById(R.id.CalenderView);
        mealButtonsLayout = view.findViewById(R.id.mealButtons);
        foodListLayout = view.findViewById(R.id.foodList);

        groupBreakfast = view.findViewById(R.id.groupBreakfast);
        groupLunch = view.findViewById(R.id.groupLunch);
        groupDinner = view.findViewById(R.id.groupDinner);
        groupSnack = view.findViewById(R.id.groupSnack);

        LinearLayout listBreakfast = view.findViewById(R.id.listBreakfast);
        LinearLayout listLunch = view.findViewById(R.id.listLunch);
        LinearLayout listDinner = view.findViewById(R.id.listDinner);
        LinearLayout listSnack = view.findViewById(R.id.listSnack);

        long todayMillis = System.currentTimeMillis();
        calendarView.setDate(todayMillis, false, true);
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(todayMillis));
        fetchDietData(today, listBreakfast, listLunch, listDinner, listSnack, resultText);

        Button btnBreakfast = view.findViewById(R.id.btnBreakfast);
        Button btnLunch = view.findViewById(R.id.btnLunch);
        Button btnDinner = view.findViewById(R.id.btnDinner);
        Button btnSnack = view.findViewById(R.id.btnSnack);

        updateMealVisibility();

        btnBreakfast.setOnClickListener(v -> {
            currentMeal = 0;
            updateMealVisibility();
        });

        btnLunch.setOnClickListener(v -> {
            currentMeal = 1;
            updateMealVisibility();
        });

        btnDinner.setOnClickListener(v -> {
            currentMeal = 2;
            updateMealVisibility();
        });

        btnSnack.setOnClickListener(v -> {
            currentMeal = 3;
            updateMealVisibility();
        });

        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            String date = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
            fetchDietData(date, listBreakfast, listLunch, listDinner, listSnack, resultText);
        });

        final float[] totalKcal = {0};
        final float[] totalCarbs = {0};
        final float[] totalProtein = {0};
        final float[] totalFat = {0};

        FoodDao dao = new FoodDao(requireContext());
        foodList = dao.searchFoodByName("");


        EditText searchEditText = view.findViewById(R.id.searchEditText);
        searchResults = view.findViewById(R.id.searchResults);
        searchResults.setLayoutManager(new GridLayoutManager(getContext(), 2));
        searchAdapter = new FoodAdapter(getContext(), new ArrayList<>());
        searchResults.setAdapter(searchAdapter);
        searchResults.setVisibility(View.GONE);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    searchResults.setVisibility(View.GONE);
                    searchAdapter.updateData(new ArrayList<>());
                    calendarLayout.setVisibility(View.VISIBLE);
                    mealButtonsLayout.setVisibility(View.VISIBLE);
                    resultText.setVisibility(View.VISIBLE);
                    foodListLayout.setVisibility(View.VISIBLE);
                    updateMealVisibility();
                    return;
                }

                List<Food> filtered = new ArrayList<>();
                for (Food item : foodList) {
                    if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                        filtered.add(item);
                    }
                }
                searchAdapter.updateData(filtered);
                searchResults.setVisibility(View.VISIBLE);
                calendarLayout.setVisibility(View.GONE);
                mealButtonsLayout.setVisibility(View.GONE);
                groupBreakfast.setVisibility(View.GONE);
                groupLunch.setVisibility(View.GONE);
                groupDinner.setVisibility(View.GONE);
                groupSnack.setVisibility(View.GONE);
                resultText.setVisibility(View.GONE);
                foodListLayout.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        List<String> foodNames = new ArrayList<>();
        for (Food item : foodList) {
            foodNames.add(item.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, foodNames);

        return view;
    }

    private void updateMealVisibility() {
        groupBreakfast.setVisibility(currentMeal == 0 ? View.VISIBLE : View.GONE);
        groupLunch.setVisibility(currentMeal == 1 ? View.VISIBLE : View.GONE);
        groupDinner.setVisibility(currentMeal == 2 ? View.VISIBLE : View.GONE);
        groupSnack.setVisibility(currentMeal == 3 ? View.VISIBLE : View.GONE);
    }

    private void fetchDietData(String date, LinearLayout breakfastList, LinearLayout lunchList,
                               LinearLayout dinnerList, LinearLayout snackList, TextView result) {
        String token = TokenManager.getToken(requireContext());
        if (token == null) {
            Toast.makeText(getContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        breakfastList.removeAllViews();
        lunchList.removeAllViews();
        dinnerList.removeAllViews();
        snackList.removeAllViews();

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        String[] meals = {"아침", "점심", "저녁", "간식"};
        LinearLayout[] groups = {breakfastList, lunchList, dinnerList, snackList};

        for (int i = 0; i < meals.length; i++) {
            String url = ApiConfig.BASE_URL + "/api/diet?date=" + date + "&mealTime=" + meals[i];
            int index = i;
            JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        org.json.JSONArray data = response.optJSONArray("data");
                        if (data == null) return;
                        for (int j = 0; j < data.length(); j++) {
                            org.json.JSONObject obj = data.optJSONObject(j);
                            if (obj == null) continue;
                            String name = obj.optString("foodName");
                            double carbs = obj.optDouble("carbohydrate");
                            double protein = obj.optDouble("protein");
                            double fat = obj.optDouble("fat");
                            double amount = obj.optDouble("amount");

                            TextView tv = new TextView(getContext());
                            tv.setText(name + "\n" +
                                    String.format("탄 %.1fg 단 %.1fg 지 %.1fg (%.1fg)", carbs, protein, fat, amount));
                            groups[index].addView(tv);
                        }
                    }, error -> {} ) {
                @Override
                public java.util.Map<String, String> getHeaders() {
                    java.util.Map<String, String> headers = new java.util.HashMap<>();
                    headers.put("Authorization", "Bearer " + token);
                    return headers;
                }
            };
            queue.add(req);
        }

        String totalUrl = ApiConfig.BASE_URL + "/api/diet/total?date=" + date;
        JsonObjectRequest totalReq = new JsonObjectRequest(Request.Method.GET, totalUrl, null,
                resp -> {
                    org.json.JSONObject data = resp.optJSONObject("data");
                    if (data == null) return;
                    double c = data.optDouble("totalCarbohydrate");
                    double p = data.optDouble("totalProtein");
                    double f = data.optDouble("totalFat");
                    result.setText(String.format("총 탄 %.1fg 단 %.1fg 지 %.1fg", c, p, f));
                }, error -> result.setText("데이터를 불러오지 못했습니다.")) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                java.util.Map<String, String> headers = new java.util.HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        queue.add(totalReq);
    }*/

package com.example.opensource_team6.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.opensource_team6.R;
import com.example.opensource_team6.RecommendationResultActivity;
import com.example.opensource_team6.data.Food;
import com.example.opensource_team6.data.FoodAdapter;
import com.example.opensource_team6.data.FoodDao;
import com.example.opensource_team6.network.ApiConfig;
import com.example.opensource_team6.util.TokenManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;


public class HomeFragment extends Fragment {

    private List<Food> foodList;
    private RecyclerView searchResults;
    private FoodAdapter searchAdapter;
    private LinearLayout calendarLayout, mealButtonsLayout, foodListLayout;
    private LinearLayout groupBreakfast, groupLunch, groupDinner, groupSnack;
    private TextView resultText;
    private int currentMeal = 0;
    private String selectedDate;

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        resultText = view.findViewById(R.id.resultText);
        CalendarView calendarView = view.findViewById(R.id.calendar);

        calendarLayout = view.findViewById(R.id.CalenderView);
        mealButtonsLayout = view.findViewById(R.id.mealButtons);
        foodListLayout = view.findViewById(R.id.foodList);

        groupBreakfast = view.findViewById(R.id.groupBreakfast);
        groupLunch = view.findViewById(R.id.groupLunch);
        groupDinner = view.findViewById(R.id.groupDinner);
        groupSnack = view.findViewById(R.id.groupSnack);

        LinearLayout listBreakfast = view.findViewById(R.id.listBreakfast);
        LinearLayout listLunch = view.findViewById(R.id.listLunch);
        LinearLayout listDinner = view.findViewById(R.id.listDinner);
        LinearLayout listSnack = view.findViewById(R.id.listSnack);

        long todayMillis = System.currentTimeMillis();
        calendarView.setDate(todayMillis, false, true);
        selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(todayMillis));
        fetchDietData(selectedDate, listBreakfast, listLunch, listDinner, listSnack, resultText);

        view.findViewById(R.id.btnBreakfast).setOnClickListener(v -> { currentMeal = 0; updateMealVisibility(); });
        view.findViewById(R.id.btnLunch).setOnClickListener(v -> { currentMeal = 1; updateMealVisibility(); });
        view.findViewById(R.id.btnDinner).setOnClickListener(v -> { currentMeal = 2; updateMealVisibility(); });
        view.findViewById(R.id.btnSnack).setOnClickListener(v -> { currentMeal = 3; updateMealVisibility(); });
        view.findViewById(R.id.analyzeBtn).setOnClickListener(v -> analyzeDayMeals(selectedDate));

        updateMealVisibility();

        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
            fetchDietData(selectedDate, listBreakfast, listLunch, listDinner, listSnack, resultText);
        });

        FoodDao dao = new FoodDao(requireContext());
        foodList = dao.searchFoodByName("");

        EditText searchEditText = view.findViewById(R.id.searchEditText);
        searchResults = view.findViewById(R.id.searchResults);
        searchResults.setLayoutManager(new GridLayoutManager(getContext(), 2));
        searchAdapter = new FoodAdapter(getContext(), new ArrayList<>());
        searchResults.setAdapter(searchAdapter);
        searchResults.setVisibility(View.GONE);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    searchResults.setVisibility(View.GONE);
                    calendarLayout.setVisibility(View.VISIBLE);
                    mealButtonsLayout.setVisibility(View.VISIBLE);
                    resultText.setVisibility(View.VISIBLE);
                    foodListLayout.setVisibility(View.VISIBLE);
                    updateMealVisibility();
                    return;
                }
                List<Food> filtered = new ArrayList<>();
                for (Food item : foodList) {
                    if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                        filtered.add(item);
                    }
                }
                searchAdapter.updateData(filtered);
                searchResults.setVisibility(View.VISIBLE);
                calendarLayout.setVisibility(View.GONE);
                mealButtonsLayout.setVisibility(View.GONE);
                resultText.setVisibility(View.GONE);
                foodListLayout.setVisibility(View.GONE);
                groupBreakfast.setVisibility(View.GONE);
                groupLunch.setVisibility(View.GONE);
                groupDinner.setVisibility(View.GONE);
                groupSnack.setVisibility(View.GONE);
            }
        });

        return view;
    }

    private void updateMealVisibility() {
        groupBreakfast.setVisibility(currentMeal == 0 ? View.VISIBLE : View.GONE);
        groupLunch.setVisibility(currentMeal == 1 ? View.VISIBLE : View.GONE);
        groupDinner.setVisibility(currentMeal == 2 ? View.VISIBLE : View.GONE);
        groupSnack.setVisibility(currentMeal == 3 ? View.VISIBLE : View.GONE);
    }

    private void fetchDietData(String date, LinearLayout breakfastList, LinearLayout lunchList,
                               LinearLayout dinnerList, LinearLayout snackList, TextView result) {
        String token = TokenManager.getToken(requireContext());
        if (token == null) {
            Toast.makeText(getContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        breakfastList.removeAllViews();
        lunchList.removeAllViews();
        dinnerList.removeAllViews();
        snackList.removeAllViews();

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        String[] meals = {"아침", "점심", "저녁", "간식"};
        LinearLayout[] groups = {breakfastList, lunchList, dinnerList, snackList};

        for (int i = 0; i < meals.length; i++) {
            String url = ApiConfig.BASE_URL + "/api/diet?date=" + date + "&mealTime=" + meals[i];
            int index = i;
            JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        JSONArray data = response.optJSONArray("data");
                        if (data == null) return;
                        for (int j = 0; j < data.length(); j++) {
                            JSONObject obj = data.optJSONObject(j);
                            if (obj == null) continue;
                            String name = obj.optString("foodName");
                            double carbs = obj.optDouble("carbohydrate");
                            double protein = obj.optDouble("protein");
                            double fat = obj.optDouble("fat");
                            double amount = obj.optDouble("amount");

                            TextView tv = new TextView(getContext());
                            tv.setText(name + "\n" +
                                    String.format("탄 %.1fg 단 %.1fg 지 %.1fg (%.1fg)", carbs, protein, fat, amount));
                            groups[index].addView(tv);
                        }
                    }, error -> {}) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + token);
                    return headers;
                }
            };
            queue.add(req);
        }

        String totalUrl = ApiConfig.BASE_URL + "/api/diet/total?date=" + date;
        JsonObjectRequest totalReq = new JsonObjectRequest(Request.Method.GET, totalUrl, null,
                resp -> {
                    JSONObject data = resp.optJSONObject("data");
                    if (data == null) return;
                    double c = data.optDouble("totalCarbohydrate");
                    double p = data.optDouble("totalProtein");
                    double f = data.optDouble("totalFat");
                    result.setText(String.format("총 탄 %.1fg 단 %.1fg 지 %.1fg", c, p, f));
                }, error -> result.setText("데이터를 불러오지 못했습니다.")) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        queue.add(totalReq);
    }

    private void analyzeDayMeals(String date) {
        String token = TokenManager.getToken(requireContext());
        if (token == null) {
            Toast.makeText(getContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] meals = {"아침", "점심", "저녁", "간식"};
        float[] total = new float[8];
        final int[] remaining = {meals.length};

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        for (String meal : meals) {
            String url = ApiConfig.BASE_URL + "/api/diet?date=" + date + "&mealTime=" + meal;
            JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        JSONArray data = response.optJSONArray("data");
                        if (data != null) {
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.optJSONObject(i);
                                if (obj == null) continue;
                                total[0] += obj.optDouble("energy");
                                total[1] += obj.optDouble("carbohydrate");
                                total[2] += obj.optDouble("protein");
                                total[3] += obj.optDouble("fat");
                                total[4] += obj.optDouble("sugar");
                                total[5] += obj.optDouble("sodium") / 1000f;
                                total[6] += obj.optDouble("cholesterol") / 1000f;
                                total[7] += obj.optDouble("saturatedFat");
                                Log.d("analyze", "energy: " + obj.optDouble("energy", 0.0));
                            }
                        }
                        if (--remaining[0] == 0) finalizeAnalysis(total);
                    },
                    error -> {
                        if (--remaining[0] == 0) finalizeAnalysis(total);
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + token);
                    return headers;
                }
            };
            queue.add(req);
        }
    }

    private void finalizeAnalysis(float[] total) {
            float[] recommended = {2500f, 310f, 55f, 70f, 100f, 2f, 0.3f, 20f};
            float[] deficit = new float[8];
            float[] normalized = new float[8];

            // ✅ 모델 학습 시 사용된 정규화 max 기준
            float[] maxVals = {595f, 104f, 41.55f, 60.25f, 70f, 7.402f, 0.68662f, 25.6f};

            for (int i = 0; i < 8; i++) {
                deficit[i] = Math.max(0f, recommended[i] - total[i]);
                normalized[i] = deficit[i] / maxVals[i];
                normalized[i] = Math.max(0f, Math.min(1f, normalized[i]));  // [0, 1] 클리핑
            }
        }

}
