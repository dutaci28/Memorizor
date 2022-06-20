package com.example.memorizor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.memorizor.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountSettingsActivity extends AppCompatActivity {

    private CircleImageView profileImage;
    private EditText email;
    private EditText fullname;
    private EditText et_password_reset;
    private Button btn_modify;

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        profileImage = findViewById(R.id.image_profile);
        email = findViewById(R.id.et_email);
        fullname = findViewById(R.id.et_fullname);
        btn_modify = findViewById(R.id.btn_modify);
        et_password_reset = findViewById(R.id.et_password_reset);

        fullname.setTranslationX(800);
        email.setTranslationX(800);
        et_password_reset.setTranslationX(800);
        btn_modify.setTranslationX(800);

        fullname.setAlpha(0);
        email.setAlpha(0);
        et_password_reset.setAlpha(0);
        btn_modify.setAlpha(0);

        fullname.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(100).start();
        email.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(200).start();
        et_password_reset.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(300).start();
        btn_modify.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(400).start();

        readUser();

        btn_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(fullname.getText().toString()) || !TextUtils.isEmpty(email.getText().toString()) || et_password_reset.getText().toString().length() > 6) {
                    String password = et_password_reset.getText().toString();

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(currentUser.getEmail(), password);
                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getBaseContext(), "User re-authenticated.", Toast.LENGTH_SHORT).show();
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        user.updateEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getBaseContext(), "User email address updated.", Toast.LENGTH_SHORT).show();

                                                    String userId = user.getUid();
                                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                                                    HashMap<String, Object> map = new HashMap<>();
                                                    map.put("name", fullname.getText().toString());
                                                    map.put("email", email.getText().toString());
                                                    map.put("id", userId);
                                                    map.put("permissions", "user");
                                                    map.put("profileImageUrl", currentUser.getProfileImageUrl());

                                                    ref.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(getBaseContext(), "Details updated", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(getBaseContext(), "Details failed to update", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });

                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(getBaseContext(), "Error re-authenticating.", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }
            }
        });
    }

    private void readUser() {
        Query query1 = FirebaseDatabase.getInstance().getReference().child("Users")
                .orderByChild("id").startAt(FirebaseAuth.getInstance().getCurrentUser().getUid()).endAt(FirebaseAuth.getInstance().getCurrentUser().getUid() + "\uf8ff");

        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    currentUser = snap.getValue(User.class);
                    email.setText(currentUser.getEmail());
                    fullname.setText(currentUser.getName());
                    if (currentUser.getProfileImageUrl().equals("")) {
                        Toast.makeText(getBaseContext(), "You can upload a profile image by clicking on the circle view.", Toast.LENGTH_SHORT).show();
                    } else {
                        Picasso.get().load(currentUser.getProfileImageUrl()).into(profileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}