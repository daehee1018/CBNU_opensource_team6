// MealRecordDetailActivity.java
package com.example.opensource_team6;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.opensource_team6.data.Food;
import com.example.opensource_team6.data.FoodDao;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

public class MealRecordDetailActivity extends AppCompatActivity {

    private AutoCompleteTextView autoFoodInput;
    private EditText inputAmount;
    private Button btnAddFood, btnFinish;
    private Spinner mealSpinner;
    private ListView addedFoodList;

    private List<Food> foodList;
    private List<Food> addedFoods = new ArrayList<>();
    private ArrayAdapter<String> listAdapter;
    private List<String> addedFoodDisplayList = new ArrayList<>();
    private String selectedMeal;
    private static final String[] MEAL_TYPES = {"조식", "중식", "석식"};
    private static final String NIGHT_MEAL = "야식";
    private Map<String, List<Food>> mealMap = new HashMap<>();

    // min-max 값 (Python 훈련 코드에서 추출하여 하드코딩 또는 npy 로딩)
    private final float[] min = {0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
    private final float[] max = {595f, 104f, 41.55f, 60.25f, 70f, 7.402f, 0.68662f, 25.6f};
    private final float[] recommended = {2500f, 310f, 55f, 70f, 100f, 2f, 0.3f, 20f};

// sodium, cholesterol → mg → g로 바뀐 상태

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_record_detail);

        autoFoodInput = findViewById(R.id.autoFoodInput);
        inputAmount = findViewById(R.id.inputAmount);
        btnAddFood = findViewById(R.id.btnAddFood);
        btnFinish = findViewById(R.id.btnFinish);
        mealSpinner = findViewById(R.id.meal_spinner);
        addedFoodList = findViewById(R.id.addedFoodList);

        FoodDao dao = new FoodDao(this);
        foodList = dao.searchFoodByName("");
        List<String> foodNames = new ArrayList<>();
        for (Food f : foodList) foodNames.add(f.getName());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, foodNames);
        autoFoodInput.setAdapter(adapter);
        autoFoodInput.setThreshold(1);

        SharedPreferences prefs = getSharedPreferences("Meals", MODE_PRIVATE);
        mealSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                selectedMeal = MEAL_TYPES[pos];
                prefs.edit().putBoolean(selectedMeal, true).apply();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, addedFoodDisplayList);
        addedFoodList.setAdapter(listAdapter);

        btnAddFood.setOnClickListener(v -> {
            String name = autoFoodInput.getText().toString().trim();
            String amountStr = inputAmount.getText().toString().trim();

            if (name.isEmpty() || amountStr.isEmpty()) {
                Toast.makeText(this, "음식명과 섭취량을 입력하세요", Toast.LENGTH_SHORT).show();
                return;
            }

            Food matched = null;
            for (Food f : foodList) {
                if (f.getName().equals(name)) {
                    matched = f;
                    break;
                }
            }
            if (matched == null) {
                Toast.makeText(this, "음식이 데이터에 없습니다", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountStr);
            double weight = matched.getStandard_amount();
            if (weight <= 0) {
                Toast.makeText(this, "해당 음식의 기준량 정보가 올바르지 않습니다", Toast.LENGTH_SHORT).show();
                return;
            }

            double ratio = amount / weight;

            Food entry = new Food();
            entry.setName(name);
            entry.setWeight(amount);
            entry.setEnergy(matched.getEnergy() * ratio);
            entry.setCarbohydrate(matched.getCarbohydrate() * ratio);
            entry.setProtein(matched.getProtein() * ratio);
            entry.setFat(matched.getFat() * ratio);
            entry.setSugar(matched.getSugar() * ratio);
            entry.setSodium(matched.getSodium() * ratio);
            entry.setCholesterol(matched.getCholesterol() * ratio);
            entry.setSaturated_fat(matched.getSaturated_fat() * ratio);
            entry.setStandard_amount(weight);

            if (!mealMap.containsKey(selectedMeal)) mealMap.put(selectedMeal, new ArrayList<>());
            List<Food> mealFoods = mealMap.get(selectedMeal);
            for (Food f : mealFoods) {
                if (f.getName().equals(name)) {
                    Toast.makeText(this, "이미 추가된 음식입니다", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            mealFoods.add(entry);
            addedFoods.add(entry);
            addedFoodDisplayList.add(selectedMeal + ": " + name + " (" + amount + "g)");
            listAdapter.notifyDataSetChanged();
            autoFoodInput.setText("");
            inputAmount.setText("");
        });

        btnFinish.setOnClickListener(v -> {
            // 현재까지 섭취한 영양소 총합
            double[] total = computeTotalVector();

            // 완료된 식사 수
            List<String> completedMeals = new ArrayList<>();
            for (String meal : MEAL_TYPES) {
                if (prefs.getBoolean(meal, false)) completedMeals.add(meal);
            }
            int remainingMeals = 3 - completedMeals.size();
            if (remainingMeals <= 0) remainingMeals = 1;

            // 결핍 벡터 계산 → 정규화 (0~1로 클리핑)
            float[] inputVector = new float[8];
            for (int i = 0; i < 8; i++) {
                float deficit = Math.max(recommended[i] - (float) total[i], 0f);
                float avgDeficit = deficit / remainingMeals;
                float raw = (avgDeficit - min[i]) / (max[i] - min[i]);
                inputVector[i] = Math.min(1f, Math.max(0f, raw));  // ✅ 클리핑
            }


            float[] output = runTFLiteModel(inputVector);
            if (output == null) {
                Toast.makeText(this, "모델 실행 실패", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean suggestNightMeal = completedMeals.contains("조식") &&
                    completedMeals.contains("중식") &&
                    completedMeals.contains("석식");

            if (!suggestNightMeal) {
                Toast.makeText(this, "야식 추천은 조/중/석식 입력 후 가능합니다", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, RecommendationResultActivity.class);
            intent.putExtra("recommendationVector", Arrays.toString(output));
            intent.putExtra("currentMeal", String.join(", ", completedMeals));
            intent.putExtra("totalVector", Arrays.toString(total));
            intent.putExtra("deficitVector", Arrays.toString(inputVector));
            intent.putExtra("suggestedMeal", NIGHT_MEAL);
            startActivity(intent);

            float[] output_ = runTFLiteModel(inputVector);
            if (output_ == null) {
                Toast.makeText(this, "모델 실행 실패", Toast.LENGTH_SHORT).show();
                return;
            }

// ✅ 역정규화된 벡터 출력 (g 단위)
            float[] denormalizedOutput = new float[8];
            for (int i = 0; i < 8; i++) {
                denormalizedOutput[i] = output[i] * (max[i] - min[i]) + min[i];
            }
            Log.d("DEBUG", "입력 벡터 (정규화): " + Arrays.toString(inputVector));
            Log.d("DEBUG", "모델 출력 벡터 (정규화): " + Arrays.toString(output));
            Log.d("DEBUG", "역정규화된 출력 벡터 (g 단위): " + Arrays.toString(denormalizedOutput));

        });
    }

    private double[] computeTotalVector() {
        double[] total = new double[8];
        for (List<Food> mealFoods : mealMap.values()) {
            for (Food f : mealFoods) {
                total[0] += f.getEnergy();
                total[1] += f.getCarbohydrate();
                total[2] += f.getProtein();
                total[3] += f.getFat();
                total[4] += f.getSugar();
                total[5] += f.getSodium()/1000.0;
                total[6] += f.getCholesterol()/1000.0;
                total[7] += f.getSaturated_fat();
            }
        }
        return total;
    }

    private float[] runTFLiteModel(float[] inputVector) {
        try {
            Interpreter tflite = new Interpreter(loadModelFile("nutrition_autoencoder.tflite"));
            float[][] input = new float[1][8];
            float[][] output = new float[1][8];
            input[0] = inputVector;
            tflite.run(input, output);
            return output[0];
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private MappedByteBuffer loadModelFile(String filename) throws IOException {
        AssetFileDescriptor fd = getAssets().openFd(filename);
        FileInputStream fis = new FileInputStream(fd.getFileDescriptor());
        FileChannel channel = fis.getChannel();
        return channel.map(FileChannel.MapMode.READ_ONLY, fd.getStartOffset(), fd.getDeclaredLength());
    }
}

