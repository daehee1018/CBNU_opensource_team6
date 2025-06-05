package com.example.opensource_team6.login.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.opensource_team6.MainActivity;
import com.example.opensource_team6.util.TokenManager;

public class OAuthRedirectActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri uri = getIntent().getData();
        if (uri != null && uri.getQueryParameter("token") != null) {
            String token = uri.getQueryParameter("token");
            TokenManager.saveToken(this, token);
            Toast.makeText(this, "구글 로그인 완료", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
        } else {
            Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
