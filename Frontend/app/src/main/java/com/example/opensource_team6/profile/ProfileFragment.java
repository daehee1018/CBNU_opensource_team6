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
import java.util.ArrayList;
import java.util.List;

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
    private TextView textCarbAmount;
    private TextView textProteinAmount;
    private TextView textFatAmount;
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
    private List<User> followerList = new ArrayList<>();
    private List<User> followingList = new ArrayList<>();

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
        textCarbAmount = view.findViewById(R.id.text_carb_amount);
        textProteinAmount = view.findViewById(R.id.text_protein_amount);
        textFatAmount = view.findViewById(R.id.text_fat_amount);
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

        searchButton.setOnClickListener(v -> searchUsers());
        Bundle args = getArguments();
        if (args != null) userId = args.getInt("userId", 1);
        if (userId == 1) {
            followButton.setVisibility(View.GONE);
        } else {
            followButton.setVisibility(View.VISIBLE);
            updateFollowButton();
            followButton.setOnClickListener(v -> toggleFollow());
        }
        fetchFollowInfo();
        ImageButton settingsBtn = view.findViewById(R.id.btn_settings);
        settingsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SettingsActivity.class);
            startActivity(intent);
        });

        fetchProfile();
        fetchScore();
        fetchNutrients();

        return view;
    }

    private void fetchProfile() {
        if (userId != 1) {
            String url = ApiConfig.BASE_URL + "/api/user/" + userId;
            JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                    res -> {
                        JSONObject data = res.optJSONObject("data");
                        if (data != null) {
                            profileName.setText(data.optString("name"));
                            profileTag.setText("");
                            profileDesc.setText("");
                        }
                        fetchFollowInfo();
                        updateFollowButton();
                    },
                    e -> {}) ;
            Volley.newRequestQueue(requireContext()).add(req);
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

    private void fetchNutrients() {
        String token = TokenManager.getToken(requireContext());
        if (token == null) return;

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        String today = sdf.format(new java.util.Date());
        String url = ApiConfig.BASE_URL + "/api/diet/total?date=" + today;

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    JSONObject data = response.optJSONObject("data");
                    if (data != null) updateNutrients(data);
                }, error -> {}) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> h = new HashMap<>();
                h.put("Authorization", "Bearer " + token);
                return h;
            }
        };
        Volley.newRequestQueue(requireContext()).add(req);
    }

    private void updateNutrients(JSONObject data) {
        double tCarb = data.optDouble("totalCarbohydrate", 0);
        double tProtein = data.optDouble("totalProtein", 0);
        double tFat = data.optDouble("totalFat", 0);
        double rCarb = data.optDouble("recommendedCarbohydrate", 0);
        double rProtein = data.optDouble("recommendedProtein", 0);
        double rFat = data.optDouble("recommendedFat", 0);

        textCarbAmount.setText(String.format("권장 %.0fg / 현재 %.0fg", rCarb, tCarb));
        textProteinAmount.setText(String.format("권장 %.0fg / 현재 %.0fg", rProtein, tProtein));
        textFatAmount.setText(String.format("권장 %.0fg / 현재 %.0fg", rFat, tFat));
    }

    private void updateFollowInfo() {
        followerText.setText(followerList.size() + "\n팔로워");
        followingText.setText(followingList.size() + "\n팔로잉");

        StringBuilder names = new StringBuilder();
        int count = 0;
        ImageView[] imgs = {followerImg1, followerImg2, followerImg3};
        for (ImageView img : imgs) img.setVisibility(View.GONE);
        for (User u : followerList) {
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
        String token = TokenManager.getToken(requireContext());
        if (token == null) return;
        String url = ApiConfig.BASE_URL + "/api/follow/followings/" + 1; // 내 팔로잉
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                res -> {
                    boolean following = false;
                    for (int i = 0; i < res.optJSONArray("data").length(); i++) {
                        if (res.optJSONArray("data").optJSONObject(i).optLong("id") == userId) {
                            following = true;
                            break;
                        }
                    }
                    followButton.setText(following ? "팔로잉" : "팔로우");
                },
                error -> {}) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> h = new HashMap<>();
                h.put("Authorization", "Bearer " + token);
                return h;
            }
        };
        Volley.newRequestQueue(requireContext()).add(req);
    }

    private void toggleFollow() {
        String token = TokenManager.getToken(requireContext());
        if (token == null) return;
        int method;
        String url = ApiConfig.BASE_URL + "/api/follow/" + userId;
        boolean currentlyFollowing = followButton.getText().toString().equals("팔로잉");
        if (currentlyFollowing) {
            method = Request.Method.DELETE;
        } else {
            method = Request.Method.POST;
        }
        JsonObjectRequest req = new JsonObjectRequest(method, url, null,
                r -> {
                    fetchFollowInfo();
                    updateFollowButton();
                },
                e -> {}) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> h = new HashMap<>();
                h.put("Authorization", "Bearer " + token);
                return h;
            }
        };
        Volley.newRequestQueue(requireContext()).add(req);
    }

    private void fetchFollowInfo() {
        String urlF = ApiConfig.BASE_URL + "/api/follow/followers/" + userId;
        String urlG = ApiConfig.BASE_URL + "/api/follow/followings/" + userId;
        RequestQueue q = Volley.newRequestQueue(requireContext());

        JsonObjectRequest req1 = new JsonObjectRequest(Request.Method.GET, urlF, null,
                r -> {
                    followerList.clear();
                    var arr = r.optJSONArray("data");
                    if (arr != null) {
                        for (int i = 0; i < arr.length(); i++) {
                            var o = arr.optJSONObject(i);
                            followerList.add(new User(o.optInt("id"), o.optString("name")));
                        }
                    }
                    updateFollowInfo();
                },
                e -> {});
        JsonObjectRequest req2 = new JsonObjectRequest(Request.Method.GET, urlG, null,
                r -> {
                    followingList.clear();
                    var arr = r.optJSONArray("data");
                    if (arr != null) {
                        for (int i = 0; i < arr.length(); i++) {
                            var o = arr.optJSONObject(i);
                            followingList.add(new User(o.optInt("id"), o.optString("name")));
                        }
                    }
                    updateFollowInfo();
                },
                e -> {});
        q.add(req1);
        q.add(req2);

        followerText.setOnClickListener(v -> showUserList(followerList));
        followingText.setOnClickListener(v -> showUserList(followingList));
    }

    private void showUserList(List<User> list) {
        if (list.isEmpty()) return;
        CharSequence[] items = new CharSequence[list.size()];
        for (int i = 0; i < list.size(); i++) items[i] = list.get(i).getName();
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setItems(items, (d, which) -> openProfile(list.get(which).getId()))
                .show();
    }

    private void searchUsers() {
        String query = userSearch.getText().toString();
        String url = ApiConfig.BASE_URL + "/api/user/search?name=" + query;
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                r -> {
                    List<User> result = new ArrayList<>();
                    var arr = r.optJSONArray("data");
                    if (arr != null) {
                        for (int i = 0; i < arr.length(); i++) {
                            var o = arr.optJSONObject(i);
                            result.add(new User(o.optInt("id"), o.optString("name")));
                        }
                    }
                    if (result.isEmpty()) {
                        Toast.makeText(getContext(), "검색 결과가 없습니다", Toast.LENGTH_SHORT).show();
                    } else {
                        CharSequence[] items = new CharSequence[result.size()];
                        for (int i = 0; i < result.size(); i++) items[i] = result.get(i).getName();
                        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                                .setTitle("검색 결과")
                                .setItems(items, (d, which) -> openProfile(result.get(which).getId()))
                                .show();
                    }
                },
                e -> Toast.makeText(getContext(), "검색 실패", Toast.LENGTH_SHORT).show());
        Volley.newRequestQueue(requireContext()).add(req);
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
