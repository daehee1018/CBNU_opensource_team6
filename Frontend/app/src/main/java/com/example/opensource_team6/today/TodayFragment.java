package com.example.opensource_team6.today;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.opensource_team6.R;
import com.example.opensource_team6.network.ApiConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class  TodayFragment extends Fragment {

    private LinearLayout contentLayout;
    private LinearLayout emptyLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.today_fragment, container, false);

        contentLayout = view.findViewById(R.id.contentLayout);
        emptyLayout = view.findViewById(R.id.emptyLayout);

        TextView todayDateText = view.findViewById(R.id.todayDateText);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 M월 d일 (E)", Locale.KOREA);
        String today = sdf.format(new Date());
        todayDateText.setText(today);

        fetchData();

        return view;
    }


    private void fetchData() {
        String url = ApiConfig.BASE_URL + "/api/food/today"; // 오늘 식단 조회

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray breakfast = response.optJSONArray("breakfast");
                            JSONArray lunch = response.optJSONArray("lunch");
                            JSONArray dinner = response.optJSONArray("dinner");

                            if ((breakfast == null || breakfast.length() == 0) &&
                                    (lunch == null || lunch.length() == 0) &&
                                    (dinner == null || dinner.length() == 0)) {
                                showEmptyView();
                            } else {
                                showMealSection("아침", breakfast);
                                showMealSection("점심", lunch);
                                showMealSection("저녁", dinner);
                                emptyLayout.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            showEmptyView();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showEmptyView();
            }
        });

        queue.add(jsonRequest);
    }

    private void showEmptyView() {
        contentLayout.setVisibility(View.GONE);
        emptyLayout.setVisibility(View.VISIBLE);
    }

    private void showMealSection(String mealTitle, JSONArray mealData) throws JSONException {
        if (mealData == null || mealData.length() == 0) return;

        TextView mealTitleView = new TextView(requireContext());
        mealTitleView.setText(mealTitle);
        mealTitleView.setTextSize(18);
        mealTitleView.setPadding(0, 24, 0, 8);
        contentLayout.addView(mealTitleView);

        for (int i = 0; i < mealData.length(); i++) {
            JSONObject food = mealData.getJSONObject(i);
            String name = food.getString("name");
            double carbs = food.getDouble("carbs");
            double protein = food.getDouble("protein");
            double fat = food.getDouble("fat");
            double kcal = food.getDouble("kcal");

            TextView item = new TextView(requireContext());
            item.setText("- " + name + " (탄: " + carbs + ", 단: " + protein + ", 지: " + fat + ", 열: " + kcal + "kcal)");
            item.setTextSize(14);
            item.setPadding(16, 4, 0, 4);
            contentLayout.addView(item);
        }
    }
}
