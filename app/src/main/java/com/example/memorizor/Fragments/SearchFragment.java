package com.example.memorizor.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.memorizor.Adapter.CourseAdapter;
import com.example.memorizor.Model.Course;
import com.example.memorizor.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class SearchFragment extends Fragment {

    private RecyclerView rv_courses;
    private List<Course> mCourses;
    private CourseAdapter courseAdapter;
    private EditText et_search;
    private FloatingActionButton fab_sort;
    private boolean sortAscending = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        rv_courses = view.findViewById(R.id.rv_courses);
        et_search = view.findViewById(R.id.et_search);
        fab_sort = view.findViewById(R.id.fab_sort);

        mCourses = new ArrayList<>();

        rv_courses.setHasFixedSize(true);
        rv_courses.setLayoutManager(new LinearLayoutManager(getContext()));
        courseAdapter = new CourseAdapter(getContext(), mCourses, true);
        rv_courses.setAdapter(courseAdapter);

        readCourses();

        et_search.addTextChangedListener(new TextWatcher() {
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

        fab_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sortAscending) {
                    List<Course> sortedCourses = new ArrayList<>();
                    sortedCourses = mCourses.stream().sorted().collect(Collectors.toList());
                    mCourses.clear();
                    mCourses.addAll(sortedCourses);
                    sortAscending = false;
                    fab_sort.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
                    courseAdapter.notifyDataSetChanged();
                } else {
                    List<Course> sortedCourses = new ArrayList<>();
                    sortedCourses = mCourses.stream().sorted().collect(Collectors.toList());
                    sortedCourses.sort(Comparator.reverseOrder());
                    mCourses.clear();
                    mCourses.addAll(sortedCourses);
                    sortAscending = true;
                    fab_sort.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                    courseAdapter.notifyDataSetChanged();
                }
            }
        });

        return view;
    }

    private void readCourses() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Courses");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (TextUtils.isEmpty(et_search.getText().toString())) {

                    mCourses.clear();
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        Course course = snap.getValue(Course.class);
                        mCourses.add(course);
                    }
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
                    List<Course> sortedCourses = new ArrayList<>();
                    sortedCourses = mCourses.stream().sorted().collect(Collectors.toList());
                    mCourses.clear();
                    mCourses.addAll(sortedCourses);
                    courseAdapter.notifyDataSetChanged();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}