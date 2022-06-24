package com.example.memorizor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.memorizor.Adapter.CourseAdapter;
import com.example.memorizor.Adapter.SimpleCourseAdapter;
import com.example.memorizor.Model.Course;
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

    public EditText et_fullname_user_moderator;
    public TextView tv_email_user_moderator;
    public TextView tv_published_courses;
    public TextView tv_bookmarked_courses;
    public TextView tv_ratings_posted;
    public TextView tv_purchased_courses;
    public ImageButton btn_delete_user;
    public ImageButton btn_edit_user;
    public CircleImageView image_profile_user_moderator;
    public RecyclerView rv_courses_user_purchased;
    public RecyclerView rv_courses_user_bookmarked;

    private List<String> accountCoursesPublished = new ArrayList<>();
    private List<String> accountCoursesBookmarked = new ArrayList<>();
    private List<Course> mCoursesPublished = new ArrayList<>();
    private List<Course> mCoursesBookmarked = new ArrayList<>();
    private User currentUser;
    private int noPublished = 0;
    private SimpleCourseAdapter courseAdapterPublished;
    private SimpleCourseAdapter courseAdapterBookmarked;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moderator_user_details);

        et_fullname_user_moderator = findViewById(R.id.et_fullname_user_moderator);
        tv_email_user_moderator = findViewById(R.id.tv_email_user_moderator);
        tv_published_courses = findViewById(R.id.tv_published_courses);
        tv_bookmarked_courses = findViewById(R.id.tv_bookmarked_courses);
        tv_ratings_posted = findViewById(R.id.tv_ratings_posted);
        tv_purchased_courses = findViewById(R.id.tv_purchased_courses);
        btn_delete_user = findViewById(R.id.btn_delete_user);
        btn_edit_user = findViewById(R.id.btn_edit_user);
        image_profile_user_moderator = findViewById(R.id.image_profile_user_moderator);
        rv_courses_user_purchased = findViewById(R.id.rv_courses_user_purchased);
        rv_courses_user_bookmarked = findViewById(R.id.rv_courses_user_bookmarked);

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

                    rv_courses_user_purchased = findViewById(R.id.rv_courses_user_purchased);
                    rv_courses_user_purchased.setHasFixedSize(true);
                    rv_courses_user_purchased.setLayoutManager(new LinearLayoutManager(getBaseContext()));

                    mCoursesPublished = new ArrayList<>();
                    courseAdapterPublished = new SimpleCourseAdapter(getBaseContext(), mCoursesPublished, true);
                    rv_courses_user_purchased.setAdapter(courseAdapterPublished);

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
                            String published = "Published courses: " + mCoursesPublished.size();
                            tv_published_courses.setText(published);
                            courseAdapterPublished.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                    mCoursesBookmarked = new ArrayList<>();
                    courseAdapterBookmarked = new SimpleCourseAdapter(getBaseContext(), mCoursesBookmarked, true);
                    rv_courses_user_bookmarked.setAdapter(courseAdapterBookmarked);

                    FirebaseDatabase.getInstance().getReference().child("Bookmarks").child(currentUser.getId()).child("Bookmarked").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            accountCoursesBookmarked.clear();
                            for(DataSnapshot snap : snapshot.getChildren()){
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
                                for(String s : accountCoursesBookmarked){
                                    Course course = snap.getValue(Course.class);
                                    if(course.getCourseId().equals(s)){
                                        mCoursesBookmarked.add(course);
                                    }
                                }
                            }
                            String bookmarked = "Bookmarked courses: " + mCoursesBookmarked.size();
                            tv_bookmarked_courses.setText(bookmarked);
                            courseAdapterBookmarked.notifyDataSetChanged();
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


    }

}