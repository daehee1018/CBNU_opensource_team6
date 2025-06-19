package com.example.opensource_team6.profile;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.opensource_team6.R;
import com.example.opensource_team6.network.ApiConfig;
import com.example.opensource_team6.util.TokenManager;

import org.json.JSONException;
import org.json.JSONObject;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ImageButton backButton = findViewById(R.id.back_button);
        EditText inputCarb = findViewById(R.id.input_carb);
        EditText inputProtein = findViewById(R.id.input_protein);
        EditText inputFat = findViewById(R.id.input_fat);
        Button btnSave = findViewById(R.id.btn_save);

        backButton.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> {
            String token = TokenManager.getToken(this);
            if (token == null) {
                Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            Double carb = parseDouble(inputCarb.getText().toString());
            Double protein = parseDouble(inputProtein.getText().toString());
            Double fat = parseDouble(inputFat.getText().toString());

            JSONObject data = new JSONObject();
            try {
                if (carb != null) data.put("carbRatio", carb / 100.0);
                if (protein != null) data.put("proteinRatio", protein / 100.0);
                if (fat != null) data.put("fatRatio", fat / 100.0);
            } catch (JSONException e) {
                Toast.makeText(this, "데이터 오류", Toast.LENGTH_SHORT).show();
                return;
            }

            String url = ApiConfig.BASE_URL + "/api/user/macro-ratio";
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, data,
                    response -> {
                        String status = response.optString("status", "");
                        if ("ok".equals(status)) {
                            Toast.makeText(this, "저장되었습니다", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "저장 실패", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> Toast.makeText(this, "저장 실패", Toast.LENGTH_SHORT).show()) {
                @Override
                public java.util.Map<String, String> getHeaders() {
                    java.util.Map<String, String> headers = new java.util.HashMap<>();
                    headers.put("Authorization", "Bearer " + token);
                    return headers;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);
        });
    }

    private Double parseDouble(String s) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
