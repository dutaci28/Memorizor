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
    public RecyclerView rv_courses_user;

    private List<String> accountCourses = new ArrayList<>();
    private List<Course> mCourses = new ArrayList<>();
    private User currentUser;
    private int noPublished = 0;
    private SimpleCourseAdapter courseAdapter;


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
        rv_courses_user = findViewById(R.id.rv_courses_user);

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

                    rv_courses_user = findViewById(R.id.rv_courses_user);
                    rv_courses_user.setHasFixedSize(true);
                    rv_courses_user.setLayoutManager(new LinearLayoutManager(getBaseContext()));

                    mCourses = new ArrayList<>();
                    courseAdapter = new SimpleCourseAdapter(getBaseContext(), mCourses, true);
                    rv_courses_user.setAdapter(courseAdapter);

                    FirebaseDatabase.getInstance().getReference().child("Courses").orderByChild("publisher").startAt(currentUser.getId()).endAt(currentUser.getId() + "\uf8ff").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            accountCourses.clear();
                            for (DataSnapshot snap : snapshot.getChildren()) {
                                accountCourses.add(snap.getKey());
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
                            mCourses.clear();
                            for (DataSnapshot snap : snapshot.getChildren()) {
                                for (String s : accountCourses) {
                                    Course course = snap.getValue(Course.class);
                                    if (course.getCourseId().equals(s)) {
                                        mCourses.add(course);
                                    }
                                }
                            }
                            String published = "Published courses: " + mCourses.size();
                            tv_published_courses.setText(published);
                            courseAdapter.notifyDataSetChanged();
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