package com.example.memorizor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.memorizor.Adapter.LoginAdapter;
import com.example.memorizor.Model.User;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class StartActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;

    User currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
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
        tabLayout.setAlpha(0);
        tabLayout.animate().translationY(0).alpha(1).setDuration(500).setStartDelay(100).start();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null){

            ProgressDialog pd = new ProgressDialog(this);
            pd.setMessage("Logging in");
            pd.show();

            Query query1 = FirebaseDatabase.getInstance().getReference().child("Users")
                    .orderByChild("id").startAt(FirebaseAuth.getInstance().getCurrentUser().getUid()).endAt(FirebaseAuth.getInstance().getCurrentUser().getUid() + "\uf8ff");

            query1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        currentUser = snap.getValue(User.class);
                        if(currentUser.getPermissions().equals("user")){
                            pd.dismiss();
                            startActivity(new Intent(StartActivity.this , MainActivity.class));
                            finish();
                        }
                        if(currentUser.getPermissions().equals("moderator")){
                            pd.dismiss();
                            startActivity(new Intent(StartActivity.this , MainActivityModerator.class));
                            finish();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
    }
}
