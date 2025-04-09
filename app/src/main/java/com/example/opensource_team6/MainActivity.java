package com.example.opensource_team6;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.opensource_team6.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // ✅ 레이아웃 연결

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                Toast.makeText(this, "Home 클릭됨", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_diary) {
                Toast.makeText(this, "Diary 클릭됨", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_scan) {
                Toast.makeText(this, "Scan 클릭됨", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_profile) {
                Toast.makeText(this, "Profile 클릭됨", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }
}
