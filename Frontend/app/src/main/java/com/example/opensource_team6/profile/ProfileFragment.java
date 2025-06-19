// ProfileFragment.java
package com.example.opensource_team6.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.opensource_team6.R;
import com.example.opensource_team6.network.ApiConfig;
import com.example.opensource_team6.util.TokenManager;
import com.example.opensource_team6.user.User;
import com.example.opensource_team6.user.UserRepository;
import com.example.opensource_team6.user.FollowManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ProfileFragment extends Fragment {

    private TextView profileName;
    private TextView profileTag;
    private TextView profileDesc;
    private ProgressBar progressCarb;
    private ProgressBar progressProtein;
    private ProgressBar progressFat;
    private TextView finalScoreText;
    private TextView scoreMessage;
    private TextView followerText;
    private TextView followingText;
    private Button followButton;
    private AutoCompleteTextView userSearch;
    private Button searchButton;
    private ImageView followerImg1;
    private ImageView followerImg2;
    private ImageView followerImg3;
    private TextView followerPlus;
    private TextView followerNames;
    private int userId = 1; // default current user

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
        followerText = view.findViewById(R.id.follower);
        followingText = view.findViewById(R.id.following);
        followButton = view.findViewById(R.id.btn_follow);
        userSearch = view.findViewById(R.id.user_search);
        searchButton = view.findViewById(R.id.user_search_button);
        followerImg1 = view.findViewById(R.id.follower_img1);
        followerImg2 = view.findViewById(R.id.follower_img2);
        followerImg3 = view.findViewById(R.id.follower_img3);
        followerPlus = view.findViewById(R.id.follower_plus);
        followerNames = view.findViewById(R.id.follower_names);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line);
        for (User u : UserRepository.getUsers()) adapter.add(u.getName());
        userSearch.setAdapter(adapter);
        userSearch.setOnItemClickListener((parent, v, position, id) -> {
            String name = adapter.getItem(position);
            User target = UserRepository.getUserByName(name);
            if (target != null) openProfile(target.getId());
        });
        searchButton.setOnClickListener(v -> {
            String query = userSearch.getText().toString();
            java.util.List<User> matches = new java.util.ArrayList<>();
            for (User u : UserRepository.getUsers()) {
                if (u.getName().contains(query)) matches.add(u);
            }
            if (matches.isEmpty()) {
                Toast.makeText(getContext(), "검색 결과가 없습니다", Toast.LENGTH_SHORT).show();
            } else {
                CharSequence[] items = new CharSequence[matches.size()];
                for (int i = 0; i < matches.size(); i++) items[i] = matches.get(i).getName();
                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle("검색 결과")
                        .setItems(items, (d, which) -> openProfile(matches.get(which).getId()))
                        .show();
            }
        });
        Bundle args = getArguments();
        if (args != null) userId = args.getInt("userId", 1);
        if (userId == 1) {
            followButton.setVisibility(View.GONE);
        } else {
            followButton.setVisibility(View.VISIBLE);
            updateFollowButton();
            followButton.setOnClickListener(v -> toggleFollow());
        }
        updateFollowInfo();
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
        if (userId != 1) {
            User u = UserRepository.getUserById(userId);
            if (u != null) {
                profileName.setText(u.getName());
                profileTag.setText("");
                profileDesc.setText("");
            }
            updateFollowInfo();
            updateFollowButton();
            return;
        }

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
        updateFollowInfo();
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

    private void updateFollowInfo() {
        Set<Integer> followers = FollowManager.getFollowers(requireContext(), userId);
        Set<Integer> followings = FollowManager.getFollowings(requireContext(), userId);
        followerText.setText(followers.size() + "\n팔로워");
        followingText.setText(followings.size() + "\n팔로잉");

        StringBuilder names = new StringBuilder();
        int count = 0;
        ImageView[] imgs = {followerImg1, followerImg2, followerImg3};
        for (ImageView img : imgs) img.setVisibility(View.GONE);
        for (int id : followers) {
            User u = UserRepository.getUserById(id);
            if (u == null) continue;
            if (count < 3) {
                if (names.length() > 0) names.append(", ");
                names.append(u.getName());
                imgs[count].setVisibility(View.VISIBLE);
            }
            count++;
        }
        followerNames.setText(names.toString());
        int extra = count - 3;
        if (extra > 0) {
            followerPlus.setText("+" + extra);
            followerPlus.setVisibility(View.VISIBLE);
        } else {
            followerPlus.setVisibility(View.GONE);
        }
    }

    private void updateFollowButton() {
        boolean following = FollowManager.isFollowing(requireContext(), 1, userId);
        followButton.setText(following ? "팔로잉" : "팔로우");
    }

    private void toggleFollow() {
        boolean following = FollowManager.isFollowing(requireContext(), 1, userId);
        if (following) {
            FollowManager.unfollow(requireContext(), 1, userId);
        } else {
            FollowManager.follow(requireContext(), 1, userId);
        }
        updateFollowButton();
        updateFollowInfo();
    }

    private void openProfile(int id) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle b = new Bundle();
        b.putInt("userId", id);
        fragment.setArguments(b);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
