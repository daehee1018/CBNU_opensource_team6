package com.example.opensource_team6.home;

import android.content.Intent;
import android.os.Bundle;
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

import androidx.fragment.app.Fragment;

import com.example.opensource_team6.R;
import com.example.opensource_team6.SearchActivity;
import com.example.opensource_team6.data.Food;
import com.example.opensource_team6.data.FoodDao;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private List<Food> foodList;

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
        TextView resultText = view.findViewById(R.id.resultText);

        LinearLayout groupBreakfast = view.findViewById(R.id.groupBreakfast);
        LinearLayout groupLunch = view.findViewById(R.id.groupLunch);
        LinearLayout groupDinner = view.findViewById(R.id.groupDinner);

        Button btnBreakfast = view.findViewById(R.id.btnBreakfast);
        Button btnLunch = view.findViewById(R.id.btnLunch);
        Button btnDinner = view.findViewById(R.id.btnDinner);

        btnBreakfast.setOnClickListener(v -> {
            groupBreakfast.setVisibility(View.VISIBLE);
            groupLunch.setVisibility(View.GONE);
            groupDinner.setVisibility(View.GONE);
        });

        btnLunch.setOnClickListener(v -> {
            groupBreakfast.setVisibility(View.GONE);
            groupLunch.setVisibility(View.VISIBLE);
            groupDinner.setVisibility(View.GONE);
        });

        btnDinner.setOnClickListener(v -> {
            groupBreakfast.setVisibility(View.GONE);
            groupLunch.setVisibility(View.GONE);
            groupDinner.setVisibility(View.VISIBLE);
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

        AutoCompleteTextView searchEditText = view.findViewById(R.id.searchEditText);
        searchEditText.setFocusable(false);
        searchEditText.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SearchActivity.class);
            startActivity(intent);
        });

        List<String> foodNames = new ArrayList<>();
        for (Food item : foodList) {
            foodNames.add(item.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, foodNames);
        foodInput.setAdapter(adapter);
        searchEditText.setAdapter(adapter);
        searchEditText.setThreshold(1);

        return view;
    }
}
