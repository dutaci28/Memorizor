package com.example.memorizor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountSettingsActivity extends AppCompatActivity {

    private CircleImageView profileImage;
    private EditText email;
    private EditText fullname;
    private EditText et_password_reset;
    private Button btn_modify;
    private Button btn_delete_account;

    private User currentUser;
    private List<String> mCourseKeys = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        profileImage = findViewById(R.id.image_profile);
        email = findViewById(R.id.et_email);
        fullname = findViewById(R.id.et_fullname);
        btn_modify = findViewById(R.id.btn_modify);
        et_password_reset = findViewById(R.id.et_password_reset);
        btn_delete_account = findViewById(R.id.btn_delete_account);

        fullname.setTranslationX(800);
        email.setTranslationX(800);
        et_password_reset.setTranslationX(800);
        btn_modify.setTranslationX(800);
        btn_delete_account.setTranslationX(800);

        fullname.setAlpha(0);
        email.setAlpha(0);
        et_password_reset.setAlpha(0);
        btn_modify.setAlpha(0);
        btn_delete_account.setAlpha(0);

        fullname.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(100).start();
        email.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(200).start();
        et_password_reset.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(300).start();
        btn_modify.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(400).start();
        btn_delete_account.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(400).start();

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

        btn_delete_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:

                                //CITIRE CURSURI PUBLICATE
                                FirebaseDatabase.getInstance().getReference().child("Courses").orderByChild("publisher").startAt(currentUser.getId()).endAt(currentUser.getId() + "\uf8ff").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        mCourseKeys.clear();
                                        for (DataSnapshot snap : snapshot.getChildren()) {
                                            mCourseKeys.add(snap.getKey());
                                        }

                                        for (String key : mCourseKeys) {

                                            //DELETE PENTRU UN CURS
                                            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("Courses");
                                            Query query = dbref.child(key);

                                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    dataSnapshot.getRef().removeValue();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                }
                                            });

                                            DatabaseReference dbref1 = FirebaseDatabase.getInstance().getReference().child("Videos");
                                            Query query1 = dbref1.orderByChild("hostCourseId").startAt(key).endAt(key + "\uf8ff");

                                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                                        snap.getRef().removeValue();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                }
                                            });

                                            DatabaseReference dbref2 = FirebaseDatabase.getInstance().getReference().child("Ratings");
                                            Query query2 = dbref2.orderByChild("courseId").startAt(key).endAt(key + "\uf8ff");

                                            query2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                                        snap.getRef().removeValue();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                }
                                            });

                                            DatabaseReference dbref3 = FirebaseDatabase.getInstance().getReference().child("Bookmarks");
                                            Query query3 = dbref3;

                                            query3.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                                        if (snap.child("Bookmarked").child(key).exists()) {
                                                            snap.child("Bookmarked").child(key).getRef().removeValue();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                }
                                            });

                                            DatabaseReference dbref4 = FirebaseDatabase.getInstance().getReference().child("Purchases");
                                            Query query4 = dbref4;

                                            query4.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                                        if (snap.child("Purchased").child(key).exists()) {
                                                            snap.child("Purchased").child(key).getRef().removeValue();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                }
                                            });

                                            DatabaseReference dbref5 = FirebaseDatabase.getInstance().getReference().child("Hashtags");
                                            Query query5 = dbref5;

                                            query5.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                                        if (snap.child(key).exists()) {
                                                            snap.child(key).getRef().removeValue();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                }
                                            });

                                            //System.out.println("Course" + key);

                                        }

                                        //DELETE PENTRU USER
                                        Query queryUser = FirebaseDatabase.getInstance().getReference().child("Users")
                                                .orderByChild("id").startAt(getIntent().getStringExtra("userId")).endAt(getIntent().getStringExtra("userId") + "\uf8ff");

                                        queryUser.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot snap : snapshot.getChildren()) {

                                                    snap.getRef().removeValue();
                                                    //System.out.println("User" + snap.getRef().getKey());
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                        //REDIRECT INAPOI
                                        FirebaseAuth.getInstance().signOut();
                                        Intent intent = new Intent(AccountSettingsActivity.this, StartActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        getBaseContext().startActivity(intent);
                                        finish();

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });


                                break;

                            case DialogInterface.BUTTON_NEGATIVE:

                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(AccountSettingsActivity.this);
                builder.setMessage("Are you sure you want to delete your account? This action will also delete all of your published courses.").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
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