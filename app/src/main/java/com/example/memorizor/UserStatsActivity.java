package com.example.memorizor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.memorizor.Adapter.SimpleCourseAdapter;
import com.example.memorizor.Model.Course;
import com.example.memorizor.Model.Rating;
import com.example.memorizor.Model.User;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserStatsActivity extends AppCompatActivity {

    public TextView tv_published_courses;
    public TextView tv_bookmarked_courses;
    public TextView tv_ratings_posted;
    public TextView tv_purchased_courses;
    public PieChart piechart_user_relevant_categories;

    private List<String> accountCoursesPublished = new ArrayList<>();
    private List<String> accountCoursesBookmarked = new ArrayList<>();
    private List<String> accountCoursesPurchased = new ArrayList<>();
    private List<Course> mCoursesPublished = new ArrayList<>();
    private List<Course> mCoursesBookmarked = new ArrayList<>();
    private List<Course> mCoursesPurchased = new ArrayList<>();
    private User currentUser;
    private SimpleCourseAdapter courseAdapterPublished;
    private SimpleCourseAdapter courseAdapterBookmarked;
    private SimpleCourseAdapter courseAdapterPurchased;
    private int ratingsMean = 0;
    private int ratingsTotal = 0;

    private Map<String, List<String>> allHashtags = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_stats);

        getSupportActionBar().hide();

        tv_published_courses = findViewById(R.id.tv_published_courses);
        tv_bookmarked_courses = findViewById(R.id.tv_bookmarked_courses);
        tv_ratings_posted = findViewById(R.id.tv_ratings_posted);
        tv_purchased_courses = findViewById(R.id.tv_purchased_courses);
        piechart_user_relevant_categories = findViewById(R.id.piechart_user_relevant_categories);

        Query query1 = FirebaseDatabase.getInstance().getReference().child("Users")
                .orderByChild("id").startAt(getIntent().getStringExtra("userId")).endAt(getIntent().getStringExtra("userId") + "\uf8ff");

        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    currentUser = snap.getValue(User.class);


                    mCoursesPublished = new ArrayList<>();
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
//                            courseAdapterPublished.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                    //CURSURI CU BOOKMARK


                    mCoursesBookmarked = new ArrayList<>();

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
                            String bookmarked = "Bookmarked courses: " + mCoursesBookmarked.size();
                            tv_bookmarked_courses.setText(bookmarked);
//                            courseAdapterBookmarked.notifyDataSetChanged();
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
                            if(ratingsTotal > 0){
                                ratingsMean /= ratingsTotal;
                                String ratings = "Ratings given: " + ratingsTotal + " avg(" + ratingsMean + ")";
                                tv_ratings_posted.setText(ratings);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });


                    //CURSURI CUMPARATE

                    mCoursesPurchased = new ArrayList<>();

                    FirebaseDatabase.getInstance().getReference().child("Purchases").child(currentUser.getId()).child("Purchased").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            accountCoursesPurchased.clear();
                            for (DataSnapshot snap : snapshot.getChildren()) {
                                accountCoursesPurchased.add(snap.getKey());
                            }

                            FirebaseDatabase.getInstance().getReference().child("Hashtags").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot snap : snapshot.getChildren()) {
                                        if (allHashtags.get(snap.getKey()) == null) {
                                            allHashtags.put(snap.getKey(), new ArrayList<>());
                                        }
                                        for (DataSnapshot snap1 : snap.getChildren()) {
                                            allHashtags.get(snap.getKey()).add(snap1.getKey());
                                        }

                                    }

                                    //PRIMUL PIECHART
                                    ArrayList<PieEntry> entries;
                                    PieDataSet pieDataSet;
                                    PieData pieData;
                                    entries = new ArrayList<>();

                                    for (String key : allHashtags.keySet()) {
                                        int count = 0;
                                        for (String courseId : allHashtags.get(key)) {
                                            for(String courseIdPurchased : accountCoursesPurchased){
                                                if(courseId.equals(courseIdPurchased)){
                                                    count += 1;
                                                }
                                            }
                                        }
                                        entries.add(new PieEntry(count, key));
                                    }

                                    pieDataSet = new PieDataSet(entries, "");
                                    pieData = new PieData(pieDataSet);
                                    pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                                    piechart_user_relevant_categories.setData(pieData);
                                    Description desc = new Description();
                                    desc.setText("");
                                    piechart_user_relevant_categories.setDescription(desc);
                                    piechart_user_relevant_categories.animateY(500);
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
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
                            String purchased = "Purchased courses: " + mCoursesPurchased.size();
                            tv_purchased_courses.setText(purchased);
//                            courseAdapterPurchased.notifyDataSetChanged();
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