/*package com.example.opensource_team6;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.opensource_team6.data.Food;
import com.example.opensource_team6.data.FoodDao;

import java.util.ArrayList;
import java.util.List;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.opensource_team6.data.Food;
import com.example.opensource_team6.data.FoodDao;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_record_detail);

        // ⬇️ UI 연결
        autoFoodInput = findViewById(R.id.autoFoodInput);
        inputAmount = findViewById(R.id.inputAmount);
        btnAddFood = findViewById(R.id.btnAddFood);
        btnFinish = findViewById(R.id.btnFinish);
        mealSpinner = findViewById(R.id.meal_spinner);
        addedFoodList = findViewById(R.id.addedFoodList);
        vectorTextView = findViewById(R.id.vectorTextView);

        // ⬇️ 자동완성 초기화
        FoodDao dao = new FoodDao(this);
        foodList = dao.searchFoodByName(""); // 전체 이름 조회
        List<String> foodNames = new ArrayList<>();
        for (Food f : foodList) foodNames.add(f.getName());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, foodNames);
        autoFoodInput.setAdapter(adapter);
        autoFoodInput.setThreshold(1);

        // ⬇️ 식사 시간 선택
        mealSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMeal = parent.getItemAtPosition(position).toString();
                Log.d("MealRecord", "선택된 식사: " + selectedMeal);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        // ⬇️ 추가된 음식 표시용 리스트 어댑터
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, addedFoodDisplayList);
        addedFoodList.setAdapter(listAdapter);

        // ⬇️ 음식 추가 버튼 이벤트
        btnAddFood.setOnClickListener(v -> {
            String foodName = autoFoodInput.getText().toString().trim();
            String amountStr = inputAmount.getText().toString().trim();

            if (foodName.isEmpty() || amountStr.isEmpty()) {
                Toast.makeText(this, "음식명과 섭취량을 입력하세요", Toast.LENGTH_SHORT).show();
                return;
            }

            // 음식 탐색
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

            // 새 Food 객체 생성 (복사)
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
        });

        // ⬇️ 벡터 계산 버튼
        btnFinish.setOnClickListener(v -> {
            double[] vector = new double[15];
            for (Food food : addedFoods) {
                double r = (food.getStandard_amount() > 0) ? (food.getWeight() / food.getStandard_amount()) : 1;

                vector[0] += food.getEnergy();
                vector[1] += food.getCarbohydrate();
                vector[2] += food.getProtein();
                vector[3] += food.getFat();
                vector[4] += food.getSugar();
                vector[5] += food.getFiber();
                vector[6] += food.getCalcium();
                vector[7] += food.getPotassium();
                vector[8] += food.getSodium();
                vector[9] += food.getCholesterol();
                vector[10] += food.getSaturated_fat();
                vector[11] += food.getTrans_fat();
                // 아래는 식단 정보를 위한 자리수 채움 (예: 비타민 A, C, D 등으로 확장 가능)
                vector[12] += 0;
                vector[13] += 0;
                vector[14] += 0;

            }
            private float[] runTFLiteModel(float[] inputVector) {
                try {
                    Interpreter tflite = new Interpreter(loadModelFile("nutrition_autoencoder.tflite"));
                    float[][] output = new float[1][12];  // 12차원 출력
                    float[][] input = new float[1][12];
                    input[0] = inputVector;
                    tflite.run(input, output);
                    return output[0];
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            // assets 폴더에서 모델을 읽어오는 헬퍼
            private MappedByteBuffer loadModelFile(String modelFilename) throws IOException {
                AssetFileDescriptor fileDescriptor = getAssets().openFd(modelFilename);
                FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
                FileChannel fileChannel = inputStream.getChannel();
                long startOffset = fileDescriptor.getStartOffset();
                long declaredLength = fileDescriptor.getDeclaredLength();
                return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
            }
            // 추천 결과 (String) 예시
            String recommendationOutput = "추천 음식:\n- 두부 100g\n- 사과 150g\n- 닭가슴살 120g";

           // 인텐트로 결과 페이지로 이동
            Intent intent = new Intent(MealRecordDetailActivity.this, RecommendationResultActivity.class);
            intent.putExtra("recommendationResult", recommendationOutput);
            startActivity(intent);

            StringBuilder sb = new StringBuilder("벡터:\n[");
            for (int i = 0; i < vector.length; i++) {
                sb.append(String.format("%.2f", vector[i]));
                if (i < vector.length - 1) sb.append(", ");
            }
            sb.append("]");
            vectorTextView.setText(sb.toString());
            Log.d("VectorOutput", sb.toString());
        });
    }
}*/
package com.example.opensource_team6;

import java.util.Arrays;
import android.content.Intent;
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
import java.util.List;

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

        // 식사 선택
        mealSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMeal = parent.getItemAtPosition(position).toString();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
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
        });

        // "완료" 버튼 클릭 → 벡터 계산 → 모델 실행 → 결과 화면으로 전송
        btnFinish.setOnClickListener(v -> {
            double[] vector = new double[12];  // 비타민 제외
            for (Food food : addedFoods) {
                double r = (food.getStandard_amount() > 0) ? (food.getWeight() / food.getStandard_amount()) : 1;

                vector[0] += food.getEnergy();
                vector[1] += food.getCarbohydrate();
                vector[2] += food.getProtein();
                vector[3] += food.getFat();
                vector[4] += food.getSugar();
                vector[5] += food.getFiber();
                vector[6] += food.getCalcium();
                vector[7] += food.getPotassium();
                vector[8] += food.getSodium();
                vector[9] += food.getCholesterol();
                vector[10] += food.getSaturated_fat();
                vector[11] += food.getTrans_fat();
            }

            // 문자열로 벡터 변환
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < vector.length; i++) {
                sb.append(String.format("%.2f", vector[i]));
                if (i < vector.length - 1) sb.append(", ");
            }

            Log.d("추천벡터 입력", sb.toString());

            // 결과 페이지로 이동 (벡터도 전달)
            Intent intent = new Intent(MealRecordDetailActivity.this, RecommendationResultActivity.class);
            intent.putExtra("recommendationVector", sb.toString());
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

