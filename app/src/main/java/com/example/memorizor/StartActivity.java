package com.example.memorizor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.memorizor.Adapter.LoginAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class StartActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    float v = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        tabLayout.addTab(tabLayout.newTab().setText("Login"));
        tabLayout.addTab(tabLayout.newTab().setText("Register"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final LoginAdapter adapter = new LoginAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setTranslationY(300);
        tabLayout.setAlpha(v);
        tabLayout.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(100).start();

    }

//
//    private ImageView iconImage;
//    private LinearLayout linearLayout;
//    private Button register;
//    private Button login;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_start);
//
//        getSupportActionBar().hide();
//
//        iconImage = findViewById(R.id.icon_image);
//        linearLayout = findViewById(R.id.linear_layout);
//        register = findViewById(R.id.register);
//        login = findViewById(R.id.login);
//
//        linearLayout.animate().alpha(0f).setDuration(10);
//        TranslateAnimation animation = new TranslateAnimation(0 , 0 , 0 , -1500);
//        animation.setDuration(1000);
//        animation.setFillAfter(false);
//        animation.setAnimationListener(new MyAnimationListener());
//        iconImage.setAnimation(animation);
//
//        register.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(StartActivity.this , RegisterActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
//            }
//        });
//
//        login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(StartActivity.this , LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
//            }
//        });
//
//    }
//
//    private class MyAnimationListener implements Animation.AnimationListener {
//
//        @Override
//        public void onAnimationStart(Animation animation) {
//        }
//        @Override
//        public void onAnimationEnd(Animation animation) {
//            iconImage.clearAnimation();
//            iconImage.setVisibility(View.INVISIBLE);
//            linearLayout.animate().alpha(1f).setDuration(1000);
//        }
//        @Override
//        public void onAnimationRepeat(Animation animation) {
//        }
//    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            startActivity(new Intent(StartActivity.this , MainActivity.class));
            finish();
        }
    }
}
