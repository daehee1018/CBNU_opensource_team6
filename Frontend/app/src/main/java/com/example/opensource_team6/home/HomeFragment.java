package com.example.opensource_team6.home;

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
    private LinearLayout inputContainerLayout;
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

        AutoCompleteTextView foodInput = view.findViewById(R.id.foodInput);
        EditText foodAmount = view.findViewById(R.id.foodAmount);
        Button addFoodBtn = view.findViewById(R.id.addFoodBtn);
        final TextView resultText = view.findViewById(R.id.resultText);
        CalendarView calendarView = view.findViewById(R.id.calendar);

        calendarLayout = view.findViewById(R.id.CalenderView);
        mealButtonsLayout = view.findViewById(R.id.mealButtons);
        inputContainerLayout = view.findViewById(R.id.inputContainer);
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

        addFoodBtn.setOnClickListener(v -> {
            String foodName = foodInput.getText().toString().trim();
            String amountStr = foodAmount.getText().toString().trim();

            if (foodName.isEmpty() || amountStr.isEmpty()) {
                Toast.makeText(getContext(), "음식 이름과 섭취량을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            float amount = Float.parseFloat(amountStr);
            Food matched = null;
            for (Food item : foodList) {
                if (item.getName().contains(foodName)) {
                    matched = item;
                    break;
                }
            }

            if (matched == null) {
                Toast.makeText(getContext(), "해당 음식이 DB에 없습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            double baseWeight = matched.getWeight();
            if (baseWeight == 0) {
                Toast.makeText(getContext(), "해당 음식의 기준량이 없습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            totalKcal[0] += matched.getEnergy() / baseWeight * amount;
            totalCarbs[0] += matched.getCarbohydrate() / baseWeight * amount;
            totalProtein[0] += matched.getProtein() / baseWeight * amount;
            totalFat[0] += matched.getFat() / baseWeight * amount;

            resultText.setText(
                    String.format("총 섭취량:\n칼로리: %.1f kcal\n탄수화물: %.1fg\n단백질: %.1fg\n지방: %.1fg",
                            totalKcal[0], totalCarbs[0], totalProtein[0], totalFat[0])
            );

            foodInput.setText("");
            foodAmount.setText("");
        });

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
                    inputContainerLayout.setVisibility(View.VISIBLE);
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
                inputContainerLayout.setVisibility(View.GONE);
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
        foodInput.setAdapter(adapter);

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
    }
}
