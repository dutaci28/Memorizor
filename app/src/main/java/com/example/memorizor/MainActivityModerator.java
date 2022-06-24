package com.example.memorizor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.MenuItem;

import com.example.memorizor.Fragments.AccountFragment;
import com.example.memorizor.Fragments.BookmarksFragment;
import com.example.memorizor.Fragments.HomeFragment;
import com.example.memorizor.Fragments.ModeratorCoursesFragment;
import com.example.memorizor.Fragments.ModeratorUsersFragment;
import com.example.memorizor.Fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivityModerator extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Fragment selectorFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_moderator);

        getSupportActionBar().hide();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        bottomNavigationView = findViewById(R.id.bottom_navigation_moderator);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_courses:
                        selectorFragment = new ModeratorCoursesFragment();
                        break;

                    case R.id.nav_users:
                        selectorFragment = new ModeratorUsersFragment();
                        break;
                }

                if (selectorFragment != null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_moderator , selectorFragment).commit();
                }

                return  true;

            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_moderator , new ModeratorCoursesFragment()).commit();
    }
}