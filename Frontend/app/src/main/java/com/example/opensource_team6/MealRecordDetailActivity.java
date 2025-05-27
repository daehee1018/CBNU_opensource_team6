package com.example.opensource_team6;

import java.util.Arrays;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.opensource_team6.data.Food;
import com.example.opensource_team6.data.FoodDao;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MealRecordDetailActivity extends AppCompatActivity {

    private AutoCompleteTextView autoFoodInput;
    private EditText inputAmount;
    private Button btnAddFood, btnFinish;
    private Spinner mealSpinner;
    private ListView addedFoodList;
    private TextView vectorTextView;

    private List<Food> foodList;
    private List<Food> addedFoods = new ArrayList<>();
    private ArrayAdapter<String> listAdapter;
    private List<String> addedFoodDisplayList = new ArrayList<>();
    private String selectedMeal;
    // 필드 상단에 추가
    private Map<String, List<Food>> mealMap = new HashMap<>();
    private static final String[] MEAL_TYPES = {"조식", "중식", "석식", "야식"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_record_detail);

        // UI 연결
        autoFoodInput = findViewById(R.id.autoFoodInput);
        inputAmount = findViewById(R.id.inputAmount);
        btnAddFood = findViewById(R.id.btnAddFood);
        btnFinish = findViewById(R.id.btnFinish);
        mealSpinner = findViewById(R.id.meal_spinner);
        addedFoodList = findViewById(R.id.addedFoodList);
        vectorTextView = findViewById(R.id.vectorTextView);

        // 자동완성 초기화
        FoodDao dao = new FoodDao(this);
        foodList = dao.searchFoodByName("");
        List<String> foodNames = new ArrayList<>();
        for (Food f : foodList) foodNames.add(f.getName());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, foodNames);
        autoFoodInput.setAdapter(adapter);
        autoFoodInput.setThreshold(1);
        SharedPreferences prefs = getSharedPreferences("Meals", MODE_PRIVATE);

        // 스피너 선택 시 저장
        mealSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMeal = parent.getItemAtPosition(position).toString();
                Log.d("MealRecord", "선택된 식사: " + selectedMeal);

                // ✅ 식사 시간 기록 (조식/중식/석식/야식 → true)
                prefs.edit().putBoolean(selectedMeal, true).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });



        // 리스트 어댑터
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, addedFoodDisplayList);
        addedFoodList.setAdapter(listAdapter);

        // 음식 추가
        btnAddFood.setOnClickListener(v -> {
            String foodName = autoFoodInput.getText().toString().trim();
            String amountStr = inputAmount.getText().toString().trim();

            if (foodName.isEmpty() || amountStr.isEmpty()) {
                Toast.makeText(this, "음식명과 섭취량을 입력하세요", Toast.LENGTH_SHORT).show();
                return;
            }

            Food matched = null;
            for (Food item : foodList) {
                if (item.getName().equals(foodName)) {
                    matched = item;
                    break;
                }
            }

            if (matched == null) {
                Toast.makeText(this, "음식이 데이터에 없습니다", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountStr);
            double ratio = amount / matched.getWeight();

            Food entry = new Food();
            entry.setName(foodName);
            entry.setWeight(amount);
            entry.setEnergy(matched.getEnergy() * ratio);
            entry.setCarbohydrate(matched.getCarbohydrate() * ratio);
            entry.setProtein(matched.getProtein() * ratio);
            entry.setFat(matched.getFat() * ratio);
            entry.setSugar(matched.getSugar() * ratio);
            entry.setFiber(matched.getFiber() * ratio);
            entry.setCalcium(matched.getCalcium() * ratio);
            entry.setPotassium(matched.getPotassium() * ratio);
            entry.setSodium(matched.getSodium() * ratio);
            entry.setCholesterol(matched.getCholesterol() * ratio);
            entry.setSaturated_fat(matched.getSaturated_fat() * ratio);
            entry.setTrans_fat(matched.getTrans_fat() * ratio);
            entry.setStandard_amount(matched.getStandard_amount());

            addedFoods.add(entry);
            addedFoodDisplayList.add(foodName + " (" + amount + "g)");
            listAdapter.notifyDataSetChanged();

            autoFoodInput.setText("");
            inputAmount.setText("");
            List<Food> currentMeal = mealMap.getOrDefault(selectedMeal, new ArrayList<>());
            currentMeal.add(entry);
            mealMap.put(selectedMeal, currentMeal);

            addedFoodDisplayList.add(selectedMeal + ": " + foodName + " (" + amount + "g)");
            listAdapter.notifyDataSetChanged();
        });

        // "완료" 버튼 클릭 → 벡터 계산 → 모델 실행 → 결과 화면으로 전송
        btnFinish.setOnClickListener(v -> {
            // 1. 입력된 식사 시간 확인
            List<String> completedMeals = new ArrayList<>(mealMap.keySet());

            // 2. 아직 입력 안한 식사 후보 (예: 조식 입력 → 중식/석식/야식 후보)
            List<String> remainingMeals = new ArrayList<>();
            for (String type : MEAL_TYPES) {
                if (!completedMeals.contains(type)) remainingMeals.add(type);
            }

            // 3. 현재까지 입력된 모든 영양소 합산
            double[] totalVector = new double[12];
            for (List<Food> mealFoods : mealMap.values()) {
                for (Food food : mealFoods) {
                    totalVector[0] += food.getEnergy();
                    totalVector[1] += food.getCarbohydrate();
                    totalVector[2] += food.getProtein();
                    totalVector[3] += food.getFat();
                    totalVector[4] += food.getSugar();
                    totalVector[5] += food.getFiber();
                    totalVector[6] += food.getCalcium();
                    totalVector[7] += food.getPotassium();
                    totalVector[8] += food.getSodium();
                    totalVector[9] += food.getCholesterol();
                    totalVector[10] += food.getSaturated_fat();
                    totalVector[11] += food.getTrans_fat();
                }
            }

            // 4. 권장 섭취량 기준 벡터 만들기 (남성 예시, 여성일 경우 따로 만들기)
            float[] recommendedVector = new float[]{
                    2500, 310, 55, 70, 100, 25, 700, 3500, 2000, 300, 20, 2
            };

            // 5. 부족한 영양소 벡터 = 권장 - 현재 총합
            float[] inputVector = new float[12];
            for (int i = 0; i < 12; i++) {
                inputVector[i] = Math.max(recommendedVector[i] - (float) totalVector[i], 0);
            }

            // 6. 모델 실행
            float[] output = runTFLiteModel(inputVector);
            if (output == null) return;

            // 7. 결과 전달
            String vectorString = Arrays.toString(output);
            String remainingMealStr = String.join(", ", remainingMeals); // 추천 대상 식사 이름

            Intent intent = new Intent(this, RecommendationResultActivity.class);
            intent.putExtra("recommendationVector", vectorString);
            intent.putExtra("currentMeal", remainingMealStr); // 조식 외 중식/석식/야식
            startActivity(intent);
        });







    }
    // TFLite 모델 실행
    private float[] runTFLiteModel(float[] inputVector) {
        try {
            Interpreter tflite = new Interpreter(loadModelFile("nutrition_autoencoder.tflite"));
            float[][] input = new float[1][12];
            float[][] output = new float[1][12];
            input[0] = inputVector;
            tflite.run(input, output);
            return output[0];
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // assets에서 모델 로드
    private MappedByteBuffer loadModelFile(String filename) throws IOException {
        AssetFileDescriptor fileDescriptor = getAssets().openFd(filename);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,
                fileDescriptor.getStartOffset(), fileDescriptor.getDeclaredLength());
    }
}

