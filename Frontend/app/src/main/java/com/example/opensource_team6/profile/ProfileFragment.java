// ProfileFragment.java
package com.example.opensource_team6.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.opensource_team6.R;
import com.example.opensource_team6.network.ApiConfig;
import com.example.opensource_team6.util.TokenManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private TextView profileName;
    private TextView profileTag;
    private TextView profileDesc;
    private ProgressBar progressCarb;
    private ProgressBar progressProtein;
    private ProgressBar progressFat;
    private TextView finalScoreText;
    private TextView scoreMessage;

    public ProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);

        profileName = view.findViewById(R.id.profile_name);
        profileTag = view.findViewById(R.id.profile_tag);
        profileDesc = view.findViewById(R.id.profile_desc);
        progressCarb = view.findViewById(R.id.progress_carb);
        progressProtein = view.findViewById(R.id.progress_protein);
        progressFat = view.findViewById(R.id.progress_fat);
        finalScoreText = view.findViewById(R.id.final_score_text);
        scoreMessage = view.findViewById(R.id.score_message);
        ImageButton settingsBtn = view.findViewById(R.id.btn_settings);
        settingsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SettingsActivity.class);
            startActivity(intent);
        });

        fetchProfile();
        fetchScore();

        return view;
    }

    private void fetchProfile() {
        String token = TokenManager.getToken(requireContext());
        if (token == null) {
            Toast.makeText(getContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = ApiConfig.BASE_URL + "/api/user/mypage";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> updateProfile(response),
                error -> Toast.makeText(getContext(), "프로필 정보를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        queue.add(request);
    }

    private void updateProfile(JSONObject response) {
        String name = response.optString("name");
        String gender = response.optString("gender");
        String birthDate = response.optString("birthDate");
        int age = response.optInt("age", -1);

        profileName.setText(name);
        profileTag.setText("성별: " + gender);
        if (age >= 0) {
            profileDesc.setText("생년월일: " + birthDate + " (" + age + "세)");
        } else {
            profileDesc.setText("생년월일: " + birthDate);
        }
    }

    private void fetchScore() {
        String token = TokenManager.getToken(requireContext());
        if (token == null) return;

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        String today = sdf.format(new java.util.Date());
        String url = ApiConfig.BASE_URL + "/api/diet/score?date=" + today;

        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    JSONObject data = response.optJSONObject("data");
                    if (data != null) updateScore(data);
                },
                error -> {}) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        queue.add(req);
    }

    private void updateScore(JSONObject data) {
        int carb = (int) Math.round(data.optDouble("carbScore", 0));
        int protein = (int) Math.round(data.optDouble("proteinScore", 0));
        int fat = (int) Math.round(data.optDouble("fatScore", 0));
        int finalScore = (int) Math.round(data.optDouble("finalScore", 0));
        String message = data.optString("message", "");

        progressCarb.setProgress(carb);
        progressProtein.setProgress(protein);
        progressFat.setProgress(fat);
        finalScoreText.setText("점수: " + finalScore);
        scoreMessage.setText(message);
    }
}
