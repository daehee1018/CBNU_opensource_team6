package com.example.opensource_team6.register;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.opensource_team6.R;

import java.util.Calendar;

public class SignupStep1Activity extends AppCompatActivity {

    private EditText nameInput, emailInput, birthInput, passwordInput, confirmPasswordInput;
    private ImageView togglePassword, toggleConfirmPassword;
    private Button nextButton;

    private boolean isPasswordVisible = false;
    private boolean isConfirmVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_step1);

        // View 연결
        nameInput = findViewById(R.id.edit_name);
        emailInput = findViewById(R.id.edit_email);
        birthInput = findViewById(R.id.edit_birth);
        passwordInput = findViewById(R.id.edit_password);
        confirmPasswordInput = findViewById(R.id.edit_password_confirm);
        togglePassword = findViewById(R.id.toggle_password);
        toggleConfirmPassword = findViewById(R.id.toggle_password_confirm);
        nextButton = findViewById(R.id.btn_next);

        // 생년월일 입력 시 날짜 선택기 열기
        birthInput.setInputType(InputType.TYPE_NULL); // 키보드 안뜨게
        birthInput.setOnClickListener(v -> showDatePicker());

        // 비밀번호 보기 토글
        togglePassword.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            setPasswordVisibility(passwordInput, isPasswordVisible, togglePassword);
        });

        toggleConfirmPassword.setOnClickListener(v -> {
            isConfirmVisible = !isConfirmVisible;
            setPasswordVisibility(confirmPasswordInput, isConfirmVisible, toggleConfirmPassword);
        });

        // 다음 버튼 클릭
        nextButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String birth = birthInput.getText().toString().trim();
            String password = passwordInput.getText().toString();
            String confirm = confirmPasswordInput.getText().toString();

            if (name.isEmpty() || email.isEmpty() || birth.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "모든 항목을 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirm)) {
                Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 유효성 통과 → 2단계로 이동
            Intent intent = new Intent(this, SignupStep2Activity.class);
            intent.putExtra("name", name);
            intent.putExtra("email", email);
            intent.putExtra("birth", birth);
            intent.putExtra("password", password);
            startActivity(intent);
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            String birth = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
            birthInput.setText(birth);
        }, year, month, day).show();
    }

    private void setPasswordVisibility(EditText editText, boolean visible, ImageView toggleIcon) {
        if (visible) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
            toggleIcon.setImageResource(R.drawable.ic_visibility_off);
        } else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            toggleIcon.setImageResource(R.drawable.ic_visibility);
        }
        editText.setSelection(editText.getText().length()); // 커서 유지
    }
}
