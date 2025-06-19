package com.example.opensource_team6;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.opensource_team6.home.HomeFragment;
import com.example.opensource_team6.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.opensource_team6.MealPhotoFragment;
import com.example.opensource_team6.diet.DietCalcFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);

        // 앱 시작 시 HomeFragment 표시
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment selected = null;

            if (id == R.id.nav_home) {
                selected = new HomeFragment();
            } else if (id == R.id.nav_diary) {
                selected = new DietCalcFragment();
            } else if (id == R.id.nav_scan) {
                selected = new MealPhotoFragment();
            } else if (id == R.id.nav_profile) {
                selected = new ProfileFragment();
            }

            if (selected != null) {
                loadFragment(selected);
                return true;
            }

            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
