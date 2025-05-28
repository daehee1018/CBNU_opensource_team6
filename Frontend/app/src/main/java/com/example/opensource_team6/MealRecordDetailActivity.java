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
    private TextView vectorTextView;

    private List<Food> foodList;
    private List<Food> addedFoods = new ArrayList<>();
    private ArrayAdapter<String> listAdapter;
    private List<String> addedFoodDisplayList = new ArrayList<>();
    private String selectedMeal;

    private static final String[] MEAL_TYPES = {"조식", "중식", "석식", "야식"};
    private Map<String, List<Food>> mealMap = new HashMap<>();

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
        vectorTextView = findViewById(R.id.vectorTextView);

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
                Log.d("MealRecord", "선택된 식사: " + selectedMeal);
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
            double weight = matched.getWeight();
            if (weight <= 0) {
                Toast.makeText(this, "해당 음식의 무게 정보가 올바르지 않습니다", Toast.LENGTH_SHORT).show();
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
            entry.setFiber(matched.getFiber() * ratio);
            entry.setCalcium(matched.getCalcium() * ratio);
            entry.setPotassium(matched.getPotassium() * ratio);
            entry.setSodium(matched.getSodium() * ratio);
            entry.setCholesterol(matched.getCholesterol() * ratio);
            entry.setSaturated_fat(matched.getSaturated_fat() * ratio);
            entry.setTrans_fat(matched.getTrans_fat() * ratio);
            entry.setStandard_amount(matched.getStandard_amount());

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
            double[] total = computeTotalVector();
            float[] recommended = new float[]{2500, 310, 55, 70, 100, 25, 700, 3500, 2000, 300, 20, 2};
            float[] inputVector = new float[12];
            for (int i = 0; i < 12; i++) {
                if (recommended[i] == 0) {
                    inputVector[i] = 0;
                } else {
                    float deficit = Math.max(recommended[i] - (float) total[i], 0);
                    inputVector[i] = deficit / recommended[i];  // ⚠️ 정규화된 결핍 비율
                }
            }


            Log.d("VECTOR_INPUT", "총합 벡터: " + Arrays.toString(total));
            Log.d("VECTOR_INPUT", "입력 벡터: " + Arrays.toString(inputVector));

            float[] output = runTFLiteModel(inputVector);
            if (output == null) {
                Toast.makeText(this, "모델 실행 실패", Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> completedMeals = new ArrayList<>();
            for (String meal : MEAL_TYPES) {
                if (prefs.getBoolean(meal, false)) completedMeals.add(meal);
            }

            Intent intent = new Intent(this, RecommendationResultActivity.class);
            intent.putExtra("recommendationVector", Arrays.toString(output));
            intent.putExtra("currentMeal", String.join(", ", completedMeals));
            intent.putExtra("totalVector", Arrays.toString(total));       // 총합 벡터
            intent.putExtra("deficitVector", Arrays.toString(inputVector)); // 결핍 벡터

            startActivity(intent);
        });
    }

    // ✅ 총합 벡터 계산 함수
    private double[] computeTotalVector() {
        double[] total = new double[12];
        for (List<Food> mealFoods : mealMap.values()) {
            for (Food f : mealFoods) {
                total[0] += f.getEnergy();        total[1] += f.getCarbohydrate();  total[2] += f.getProtein();
                total[3] += f.getFat();           total[4] += f.getSugar();         total[5] += f.getFiber();
                total[6] += f.getCalcium();       total[7] += f.getPotassium();     total[8] += f.getSodium();
                total[9] += f.getCholesterol();   total[10] += f.getSaturated_fat(); total[11] += f.getTrans_fat();
            }
        }
        return total;
    }

    // ✅ TFLite 모델 실행 함수
    private float[] runTFLiteModel(float[] inputVector) {
        try {
            Interpreter tflite = new Interpreter(loadModelFile("nutrition_autoencoder.tflite"));
            float[][] input = new float[1][12];
            float[][] output = new float[1][12];
            input[0] = inputVector;  // ✅ 추가하세요
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
