package com.example.opensource_team6.register;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.opensource_team6.R;
import com.example.opensource_team6.login.ui.LoginActivity;

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
            String height = heightInput.getText().toString().trim();
            String weight = weightInput.getText().toString().trim();
            String target = targetWeightInput.getText().toString().trim();
            String gender = genderSpinner.getSelectedItem().toString();
            String interest = interestSpinner.getSelectedItem().toString();
            String concern = concernSpinner.getSelectedItem().toString();

            if (height.isEmpty() || weight.isEmpty() || target.isEmpty()) {
                Toast.makeText(this, "모든 항목을 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 회원가입 완료 처리 (ex: 서버 전송)
            Toast.makeText(this, "회원가입이 완료되었습니다!", Toast.LENGTH_LONG).show();

            // 로그인 화면으로 이동
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
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
