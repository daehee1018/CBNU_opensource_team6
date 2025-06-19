package com.example.opensource_team6;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MealPhotoFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal_photo, container, false);

        Button btnSnackHistory = view.findViewById(R.id.btnSnackHistory);
        btnSnackHistory.setOnClickListener(v -> openHistory("snack"));

        return view;
    }

    private void openHistory(String meal) {
        if (getActivity() == null) return;
        Intent intent = new Intent(getActivity(), MealPhotoHistoryActivity.class);
        intent.putExtra("meal", meal);
        startActivity(intent);
    }
}
