package com.example.memorizor.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.memorizor.Adapter.CourseAdapter;
import com.example.memorizor.Adapter.SimpleUserAdapter;
import com.example.memorizor.Model.Course;
import com.example.memorizor.Model.User;
import com.example.memorizor.R;
import com.example.memorizor.StartActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ModeratorCoursesFragment extends Fragment {

    private RecyclerView rv_courses_moderator;
    private List<Course> mCourses;
    private CourseAdapter courseAdapter;
    private EditText et_search_courses_moderator;
    private TextView tv_total_courses_moderator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_moderator_courses, container, false);

        rv_courses_moderator = view.findViewById(R.id.rv_courses_moderator);
        et_search_courses_moderator = view.findViewById(R.id.et_search_courses_moderator);
        tv_total_courses_moderator = view.findViewById(R.id.tv_total_courses_moderator);

        mCourses = new ArrayList<>();

        rv_courses_moderator.setHasFixedSize(true);
        rv_courses_moderator.setLayoutManager(new LinearLayoutManager(getContext()));
        courseAdapter = new CourseAdapter(getContext(), mCourses, true);
        rv_courses_moderator.setAdapter(courseAdapter);

        readCourses();

        et_search_courses_moderator.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchCourse(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return view;
    }

    private void readCourses() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Courses");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (TextUtils.isEmpty(et_search_courses_moderator.getText().toString())) {

                    mCourses.clear();
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        Course course = snap.getValue(Course.class);
                        mCourses.add(course);
                    }
                    String total = "Total courses: " + mCourses.size();
                    tv_total_courses_moderator.setText(total);
                    courseAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchCourse(String s) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Courses");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mCourses.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Course course = snap.getValue(Course.class);
                    if(course.getTitle().toLowerCase().contains(s.toLowerCase()) || course.getDescription().toLowerCase().contains(s.toLowerCase())){
                        mCourses.add(course);
                    }
                    String total = "Searched courses: " + mCourses.size();
                    tv_total_courses_moderator.setText(total);
                    courseAdapter.notifyDataSetChanged();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }



}