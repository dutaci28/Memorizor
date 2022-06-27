package com.example.memorizor.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.memorizor.Model.Course;
import com.example.memorizor.Model.Rating;
import com.example.memorizor.Model.User;
import com.example.memorizor.R;
import com.example.memorizor.StartActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
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


public class ModeratorStatsFragment extends Fragment {

    private Button btn_moderator_sign_out;
    private PieChart piechart_moderator_all_categories;
    private PieChart piechart_moderator_profit_categories;
    private BarChart barchart_moderator_profit_per_course;

    private List<User> allUsers = new ArrayList<>();
    private List<Course> allCourses = new ArrayList<>();
    private Map<String, List<String>> allHashtags = new HashMap<>();
    private List<Rating> allRatings = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_moderator_stats, container, false);

        btn_moderator_sign_out = view.findViewById(R.id.btn_moderator_sign_out);
        piechart_moderator_all_categories = view.findViewById(R.id.piechart_moderator_all_categories);
        piechart_moderator_profit_categories = view.findViewById(R.id.piechart_moderator_profit_categories);
        barchart_moderator_profit_per_course = view.findViewById(R.id.barchart_moderator_profit_per_course);

        btn_moderator_sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(getContext(), StartActivity.class));
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:

                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });

        FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    allUsers.add(snap.getValue(User.class));
                }

                FirebaseDatabase.getInstance().getReference().child("Courses").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            allCourses.add(snap.getValue(Course.class));
                        }

                        FirebaseDatabase.getInstance().getReference().child("Ratings").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot snap : snapshot.getChildren()) {
                                    allRatings.add(snap.getValue(Rating.class));
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

                                        //CHART TOATE CATEGORIILE
                                        ArrayList<PieEntry> entries;
                                        PieDataSet pieDataSet;
                                        PieData pieData;
                                        entries = new ArrayList<>();
                                        for (String key : allHashtags.keySet()) {
                                            int count = 0;
                                            for (String courseId : allHashtags.get(key)) {
                                                count += 1;
                                            }
                                            entries.add(new PieEntry(count, key));
                                        }
                                        pieDataSet = new PieDataSet(entries, "");
                                        pieData = new PieData(pieDataSet);
                                        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                                        piechart_moderator_all_categories.setData(pieData);
                                        Description desc = new Description();
                                        desc.setText("");
                                        piechart_moderator_all_categories.setDescription(desc);
                                        piechart_moderator_all_categories.animateY(500);

                                        //CHART CATEGORII SI PROFIT AFERENT
                                        Map<String, Float> coursesTotalSalesEach = new HashMap<>();
                                        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("Purchases");
                                        dbref.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                                    for (DataSnapshot snap1 : snap.child("Purchased").getChildren()) {
                                                        Course searchedCourse = new Course();
                                                        for (Course c : allCourses) {
                                                            if (c.getCourseId().equals(snap1.getKey())) {
                                                                searchedCourse = c;
                                                            }
                                                        }

                                                        if (coursesTotalSalesEach.get(snap1.getKey()) == null) {
                                                            coursesTotalSalesEach.put(snap1.getKey(), Float.valueOf(searchedCourse.getPrice()).floatValue());
                                                        } else {
                                                            float previousValue = coursesTotalSalesEach.get(snap1.getKey());
                                                            coursesTotalSalesEach.put(snap1.getKey(), previousValue + Float.valueOf(searchedCourse.getPrice()).floatValue());
                                                        }

                                                    }
                                                }

                                                //AICI SUNT TOATE RESURSELE INCARCATE.............................................

                                                System.out.println(coursesTotalSalesEach);

                                                ArrayList<PieEntry> entries1;
                                                PieDataSet pieDataSet1;
                                                PieData pieData1;
                                                entries1 = new ArrayList<>();
                                                for (String category : allHashtags.keySet()) {
                                                    float sum = 0;
                                                    for (String courseId : allHashtags.get(category)) {
                                                        for (String key1 : coursesTotalSalesEach.keySet()) {
                                                            if (courseId.equals(key1)) {
                                                                for (Course c : allCourses) {
                                                                    if (c.getCourseId().equals(courseId)) {
                                                                        sum += Float.valueOf(c.getPrice()).floatValue();
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                    entries1.add(new PieEntry(sum, category));
                                                }
                                                pieDataSet1 = new PieDataSet(entries1, "");
                                                pieData1 = new PieData(pieDataSet1);
                                                pieDataSet1.setColors(ColorTemplate.COLORFUL_COLORS);
                                                piechart_moderator_profit_categories.setData(pieData1);
                                                Description desc1 = new Description();
                                                desc1.setText("");
                                                piechart_moderator_profit_categories.setDescription(desc1);
                                                piechart_moderator_profit_categories.animateY(500);

                                                //BARCHART PROFIT PER CURS
                                                List<BarEntry> entries2 = new ArrayList();
                                                float poz = 0;
                                                for (String key : coursesTotalSalesEach.keySet()) {
                                                    float f = coursesTotalSalesEach.get(key);
                                                    String result = null;
                                                    for (Course c : allCourses) {
                                                        if (c.getCourseId().equals(key)) {
                                                            result = c.getTitle();
                                                        }
                                                    }
                                                    if(result != null){
                                                        poz += 3;
                                                        entries2.add(new BarEntry(poz, f, result));
                                                    }

                                                }

                                                BarDataSet bardataset = new BarDataSet(entries2, "Individual profits");
                                                barchart_moderator_profit_per_course.animateY(500);
                                                BarData data = new BarData(bardataset);
                                                bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
                                                barchart_moderator_profit_per_course.setDescription(desc1);
                                                barchart_moderator_profit_per_course.setData(data);

                                                barchart_moderator_profit_per_course.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                                                    @Override
                                                    public void onValueSelected(Entry e, Highlight h) {
                                                        Toast.makeText(getContext(), e.getData().toString(), Toast.LENGTH_SHORT).show();
                                                    }

                                                    @Override
                                                    public void onNothingSelected() {

                                                    }
                                                });

                                                //ISTORIC PURCHASE URI



                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                            }
                                        });
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

        return view;
    }

}