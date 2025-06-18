// SignupStep2Activity.java
package com.example.opensource_team6.register;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.opensource_team6.R;
import com.example.opensource_team6.login.ui.LoginActivity;
import com.example.opensource_team6.network.ApiConfig;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.json.JSONException;
import org.json.JSONObject;

public class SignupStep2Activity extends AppCompatActivity {

    private EditText heightInput, weightInput, targetWeightInput;
    private Spinner genderSpinner, interestSpinner, concernSpinner;
    private Button createButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_step2);

        // View 연결
        heightInput = findViewById(R.id.input_height);
        weightInput = findViewById(R.id.input_weight);
        targetWeightInput = findViewById(R.id.input_target_weight);
        genderSpinner = findViewById(R.id.spinner_gender);
        interestSpinner = findViewById(R.id.spinner_interest);
        concernSpinner = findViewById(R.id.spinner_health_concern);
        createButton = findViewById(R.id.btn_create);

        // Spinner 어댑터 설정
        setupSpinner(genderSpinner, R.array.gender_options);
        setupSpinner(interestSpinner, R.array.interest_options);
        setupSpinner(concernSpinner, R.array.health_concern_options);

        // 생성 버튼 클릭 시
        createButton.setOnClickListener(v -> {
            String name = getIntent().getStringExtra("name");
            String email = getIntent().getStringExtra("email");
            String birthDate = getIntent().getStringExtra("birth");
            String formattedBirthDate = "";
            try {
                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-M-d");
                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate date = LocalDate.parse(birthDate, inputFormatter);
                formattedBirthDate = date.format(outputFormatter);
            } catch (Exception e) {
                Toast.makeText(this, "생년월일 형식이 잘못되었습니다 (예: 2002-03-03)", Toast.LENGTH_SHORT).show();
                return;
            }
            String password = getIntent().getStringExtra("password");

            String height = heightInput.getText().toString().trim();
            String weight = weightInput.getText().toString().trim();
            String target = targetWeightInput.getText().toString().trim();
            double heightVal, weightVal, targetVal;
            try {
                heightVal = Double.parseDouble(height);
                weightVal = Double.parseDouble(weight);
                targetVal = Double.parseDouble(target);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "키/몸무게/목표체중은 숫자만 입력 가능합니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            String gender = genderSpinner.getSelectedItem().toString();
            String interest = interestSpinner.getSelectedItem().toString();
            String concern = concernSpinner.getSelectedItem().toString();

            if (height.isEmpty() || weight.isEmpty() || target.isEmpty()) {
                Toast.makeText(this, "모든 항목을 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // JSON 데이터 구성
            JSONObject requestData = new JSONObject();
            try {
                requestData.put("name", name);
                requestData.put("email", email);
                requestData.put("birthDate", formattedBirthDate);
                requestData.put("password", password);
                requestData.put("height", heightVal);
                requestData.put("weight", weightVal);
                requestData.put("targetWeight", targetVal);
                requestData.put("gender", gender);
                requestData.put("interest", interest);
                requestData.put("concern", concern);
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }

            String url = ApiConfig.BASE_URL + "/api/auth/signup"; // 백엔드 엔드포인트

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    requestData,
                    response -> {
                        Toast.makeText(this, "회원가입이 완료되었습니다!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    },
                    error -> {
                        Toast.makeText(this, "에러 발생: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
            );

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);
        });
    }

    private void setupSpinner(Spinner spinner, int arrayResId) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                arrayResId,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}