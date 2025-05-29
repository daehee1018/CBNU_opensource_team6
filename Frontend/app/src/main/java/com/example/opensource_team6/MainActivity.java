package com.example.opensource_team6;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.opensource_team6.home.HomeFragment;
import com.example.opensource_team6.profile.profileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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
                startActivity(new Intent(this, TodayMealActivity.class));
                return true;
            } else if (id == R.id.nav_scan) {
                Toast.makeText(this, "식단 사진 클릭됨", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_profile) {
                selected = new profileFragment();
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
