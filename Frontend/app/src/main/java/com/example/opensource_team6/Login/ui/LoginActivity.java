package com.example.opensource_team6.Login.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.example.opensource_team6.MainActivity;
import com.example.opensource_team6.R;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private Button loginButton;
    private CheckBox keepLogin;
    private TextView findPassword, register;
    private ImageButton googleLogin;
    private ImageView togglePassword;

    private boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 연결
        inputEmail = findViewById(R.id.input_email);
        inputPassword = findViewById(R.id.input_password);
        loginButton = findViewById(R.id.login);
        keepLogin = findViewById(R.id.keep_login);
        findPassword = findViewById(R.id.find_password);
        register = findViewById(R.id.register);
        googleLogin = findViewById(R.id.google_login);
        togglePassword = findViewById(R.id.toggle_password_visibility);

        // 로그인 버튼 클릭 시
        loginButton.setOnClickListener(v -> {
            String email = inputEmail.getText().toString();
            String password = inputPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "이메일과 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 임시 로그인 처리 (정상 로그인 시 MainActivity로 이동)
            Toast.makeText(this, "로그인 성공!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        // 비밀번호 보기 토글
        togglePassword.setOnClickListener(v -> {
            passwordVisible = !passwordVisible;
            if (passwordVisible) {
                inputPassword.setInputType(EditorInfo.TYPE_CLASS_TEXT);
                togglePassword.setImageResource(R.drawable.ic_visibility_off); // 보이면 가리는 아이콘
            } else {
                inputPassword.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
                togglePassword.setImageResource(R.drawable.ic_visibility); // 안 보이면 보는 아이콘
            }
            inputPassword.setSelection(inputPassword.getText().length()); // 커서 유지
        });

        // 회원가입 버튼
        register.setOnClickListener(v -> {
            Toast.makeText(this, "회원가입 화면으로 이동 예정", Toast.LENGTH_SHORT).show();
            // startActivity(new Intent(this, RegisterActivity.class));
        });

        // 비밀번호 찾기
        findPassword.setOnClickListener(v -> {
            Toast.makeText(this, "비밀번호 찾기 기능은 아직 준비 중입니다.", Toast.LENGTH_SHORT).show();
        });

        // 구글 로그인 버튼 클릭
        googleLogin.setOnClickListener(v -> {
            Toast.makeText(this, "구글 로그인은 추후 연동됩니다.", Toast.LENGTH_SHORT).show();
        });

        // 텍스트 변경 감지 (예시)
        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                loginButton.setEnabled(!inputEmail.getText().toString().isEmpty()
                        && !inputPassword.getText().toString().isEmpty());
            }
        };
        inputEmail.addTextChangedListener(watcher);
        inputPassword.addTextChangedListener(watcher);
    }

    private void showLoginFailed(@StringRes int messageResId) {
        Toast.makeText(this, getString(messageResId), Toast.LENGTH_SHORT).show();
    }
}
