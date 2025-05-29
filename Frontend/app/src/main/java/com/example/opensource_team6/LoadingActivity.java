package com.example.opensource_team6;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.opensource_team6.Login.ui.LoginActivity;

public class LoadingActivity extends Activity {

    private ProgressBar progressBar;
    private TextView percentageText;
    private int progress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);  // XML 파일과 연결

        progressBar = findViewById(R.id.progress_bar);
        percentageText = findViewById(R.id.loading_percentage);

        startLoading();
    }

    private void startLoading() {
        new Thread(() -> {
            for (progress = 0; progress <= 100; progress++) {
                int currentProgress = progress;

                runOnUiThread(() -> {
                    progressBar.setProgress(currentProgress);
                    percentageText.setText(currentProgress + "%");
                });

                try {
                    Thread.sleep(30);  // 로딩 속도
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // 로딩 끝나면 다음 화면으로 이동 (예: MainActivity)
            runOnUiThread(() -> {
                Intent intent = new Intent(LoadingActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // 로딩화면 종료
            });
        }).start();
    }
}
