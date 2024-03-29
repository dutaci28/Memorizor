package com.example.memorizor.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.memorizor.Adapter.CourseAdapter;
import com.example.memorizor.Model.Course;
import com.example.memorizor.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BookmarksFragment extends Fragment {

    private TextView tv_empty;
    private RecyclerView rv_courses_bookmarked;
    private List<Course> mCourses;
    private CourseAdapter courseAdapter;
    private ImageButton btn_bookmarks_tab;
    private ImageButton btn_purchases_tab;

    private List<String> bookmarkedCourses = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bookmarks, container, false);

        rv_courses_bookmarked = view.findViewById(R.id.rv_courses_bookmarked);
        rv_courses_bookmarked.setHasFixedSize(true);
        rv_courses_bookmarked.setLayoutManager(new LinearLayoutManager(getContext()));
        tv_empty = view.findViewById(R.id.tv_empty);
        btn_bookmarks_tab = view.findViewById(R.id.btn_bookmarks_tab);
        btn_purchases_tab = view.findViewById(R.id.btn_purchases_tab);

        tv_empty.setTranslationX(-800);
        tv_empty.setAlpha(0);
        tv_empty.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(50).start();

        mCourses = new ArrayList<>();
        courseAdapter = new CourseAdapter(getContext(), mCourses, true);
        rv_courses_bookmarked.setAdapter(courseAdapter);

        populateBookmarkedCourses();

        btn_bookmarks_tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateBookmarkedCourses();
            }
        });

        btn_purchases_tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populatePurchasedCourses();
            }
        });
        return view;
    }

    private void populateBookmarkedCourses(){
        FirebaseDatabase.getInstance().getReference().child("Bookmarks").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Bookmarked").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookmarkedCourses.clear();
                for(DataSnapshot snap : snapshot.getChildren()){
                    bookmarkedCourses.add(snap.getKey());
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
                    for(String s : bookmarkedCourses){
                        Course course = snap.getValue(Course.class);
                        if(course.getCourseId().equals(s)){
                            mCourses.add(course);
                        }
                    }
                }
                if(mCourses.size()!=0){
                    tv_empty.setText("");
                } else {
                    tv_empty.setText("You have not yet bookmarked any courses!");
                }
                courseAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void populatePurchasedCourses(){
        FirebaseDatabase.getInstance().getReference().child("Purchases").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Purchased").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookmarkedCourses.clear();
                for(DataSnapshot snap : snapshot.getChildren()){
                    bookmarkedCourses.add(snap.getKey());
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
                    for(String s : bookmarkedCourses){
                        Course course = snap.getValue(Course.class);
                        if(course.getCourseId().equals(s)){
                            mCourses.add(course);
                        }
                    }
                }
                if(mCourses.size()!=0){
                    tv_empty.setText("");
                } else {
                    tv_empty.setText("You have not yet purchased any courses!");
                }
                courseAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}