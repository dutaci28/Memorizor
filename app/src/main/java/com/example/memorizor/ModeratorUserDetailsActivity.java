package com.example.memorizor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.memorizor.Adapter.CourseAdapter;
import com.example.memorizor.Adapter.SimpleCourseAdapter;
import com.example.memorizor.Model.Course;
import com.example.memorizor.Model.Rating;
import com.example.memorizor.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ModeratorUserDetailsActivity extends AppCompatActivity {

    public TextView et_fullname_user_moderator;
    public TextView tv_email_user_moderator;
    //    public TextView tv_published_courses;
//    public TextView tv_bookmarked_courses;
//    public TextView tv_ratings_posted;
//    public TextView tv_purchased_courses;
    public ImageButton btn_delete_user;
    public CircleImageView image_profile_user_moderator;
    public RecyclerView rv_courses_user_published;
    public RecyclerView rv_courses_user_bookmarked;
    public RecyclerView rv_courses_user_purchased;

    public Button btn_see_more;

    private List<String> accountCoursesPublished = new ArrayList<>();
    private List<String> accountCoursesBookmarked = new ArrayList<>();
    private List<String> accountCoursesPurchased = new ArrayList<>();
    private List<Course> mCoursesPublished = new ArrayList<>();
    private List<Course> mCoursesBookmarked = new ArrayList<>();
    private List<Course> mCoursesPurchased = new ArrayList<>();
    private User currentUser;
    private List<String> mCourseKeys = new ArrayList<>();
    private CourseAdapter courseAdapterPublished;
    private SimpleCourseAdapter courseAdapterBookmarked;
    private SimpleCourseAdapter courseAdapterPurchased;
    private int ratingsMean = 0;
    private int ratingsTotal = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moderator_user_details);

        getSupportActionBar().hide();

        et_fullname_user_moderator = findViewById(R.id.tv_fullname_user_moderator);
        tv_email_user_moderator = findViewById(R.id.tv_email_user_moderator);
        btn_delete_user = findViewById(R.id.btn_delete_user);
        image_profile_user_moderator = findViewById(R.id.image_profile_user_moderator);
        rv_courses_user_published = findViewById(R.id.rv_courses_user_published);
        rv_courses_user_bookmarked = findViewById(R.id.rv_courses_user_bookmarked);
        rv_courses_user_purchased = findViewById(R.id.rv_courses_user_purchased);
        btn_see_more = findViewById(R.id.btn_see_more);

        Query query1 = FirebaseDatabase.getInstance().getReference().child("Users")
                .orderByChild("id").startAt(getIntent().getStringExtra("userId")).endAt(getIntent().getStringExtra("userId") + "\uf8ff");

        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    currentUser = snap.getValue(User.class);
                    et_fullname_user_moderator.setText(currentUser.getName());
                    tv_email_user_moderator.setText(currentUser.getEmail());
                    if (currentUser.getProfileImageUrl().equals("")) {

                    } else {
                        Picasso.get().load(currentUser.getProfileImageUrl()).placeholder(R.drawable.backgroudn).into(image_profile_user_moderator);
                    }

                    //CURSURI PUBLICATE
                    rv_courses_user_published.setHasFixedSize(true);
                    rv_courses_user_published.setLayoutManager(new LinearLayoutManager(getBaseContext()));

                    mCoursesPublished = new ArrayList<>();
                    courseAdapterPublished = new CourseAdapter(getBaseContext(), mCoursesPublished, true);
                    rv_courses_user_published.setAdapter(courseAdapterPublished);

                    FirebaseDatabase.getInstance().getReference().child("Courses").orderByChild("publisher").startAt(currentUser.getId()).endAt(currentUser.getId() + "\uf8ff").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            accountCoursesPublished.clear();
                            for (DataSnapshot snap : snapshot.getChildren()) {
                                accountCoursesPublished.add(snap.getKey());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Courses");
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            mCoursesPublished.clear();
                            for (DataSnapshot snap : snapshot.getChildren()) {
                                for (String s : accountCoursesPublished) {
                                    Course course = snap.getValue(Course.class);
                                    if (course.getCourseId().equals(s)) {
                                        mCoursesPublished.add(course);
                                    }
                                }
                            }
                            courseAdapterPublished.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                    //CURSURI CU BOOKMARK
                    rv_courses_user_bookmarked.setHasFixedSize(true);
                    rv_courses_user_bookmarked.setLayoutManager(new LinearLayoutManager(getBaseContext()));

                    mCoursesBookmarked = new ArrayList<>();
                    courseAdapterBookmarked = new SimpleCourseAdapter(getBaseContext(), mCoursesBookmarked, true);
                    rv_courses_user_bookmarked.setAdapter(courseAdapterBookmarked);

                    FirebaseDatabase.getInstance().getReference().child("Bookmarks").child(currentUser.getId()).child("Bookmarked").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            accountCoursesBookmarked.clear();
                            for (DataSnapshot snap : snapshot.getChildren()) {
                                accountCoursesBookmarked.add(snap.getKey());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Courses");
                    reference1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            mCoursesBookmarked.clear();
                            for (DataSnapshot snap : snapshot.getChildren()) {
                                for (String s : accountCoursesBookmarked) {
                                    Course course = snap.getValue(Course.class);
                                    if (course.getCourseId().equals(s)) {
                                        mCoursesBookmarked.add(course);
                                    }
                                }
                            }
                            courseAdapterBookmarked.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                    //RATING-URI SI MEDIA LOR

                    FirebaseDatabase.getInstance().getReference().child("Ratings").orderByChild("userId").startAt(currentUser.getId()).endAt(currentUser.getId() + "\uf8ff").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ratingsMean = 0;
                            ratingsTotal = 0;
                            for (DataSnapshot snap : snapshot.getChildren()) {
                                Rating rating = snap.getValue(Rating.class);
                                ratingsMean += rating.getValue();
                                ratingsTotal += 1;

                            }
                            if (ratingsTotal > 0) {
                                ratingsMean /= ratingsTotal;
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });


                    //CURSURI CUMPARATE
                    rv_courses_user_purchased.setHasFixedSize(true);
                    rv_courses_user_purchased.setLayoutManager(new LinearLayoutManager(getBaseContext()));

                    mCoursesPurchased = new ArrayList<>();
                    courseAdapterPurchased = new SimpleCourseAdapter(getBaseContext(), mCoursesPurchased, true);
                    rv_courses_user_purchased.setAdapter(courseAdapterPurchased);

                    FirebaseDatabase.getInstance().getReference().child("Purchases").child(currentUser.getId()).child("Purchased").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            accountCoursesPurchased.clear();
                            for (DataSnapshot snap : snapshot.getChildren()) {
                                accountCoursesPurchased.add(snap.getKey());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("Courses");
                    reference2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            mCoursesPurchased.clear();
                            for (DataSnapshot snap : snapshot.getChildren()) {
                                for (String s : accountCoursesPurchased) {
                                    Course course = snap.getValue(Course.class);
                                    if (course.getCourseId().equals(s)) {
                                        mCoursesPurchased.add(course);
                                    }
                                }
                            }
                            courseAdapterPurchased.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btn_see_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), UserStatsActivity.class);
                intent.putExtra("userId", currentUser.getId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getBaseContext().startActivity(intent);
            }
        });

        btn_delete_user.setOnClickListener(new View.OnClickListener() {
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
                AlertDialog.Builder builder = new AlertDialog.Builder(ModeratorUserDetailsActivity.this);
                builder.setMessage("Are you sure you want to delete this user? This action will also delete all of their respective courses.").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });

    }

}