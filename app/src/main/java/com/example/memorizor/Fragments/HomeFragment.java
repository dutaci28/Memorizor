package com.example.memorizor.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.memorizor.Adapter.ParentAdapter;
import com.example.memorizor.Model.Course;
import com.example.memorizor.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private RecyclerView rv_parent_items;
    private List<String> hashTags = new ArrayList<>();
    private ParentAdapter parentAdapter;

    private Map<String, List<Course>> hashedCoursesMap = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rv_parent_items = view.findViewById(R.id.rv_parent_items);
        rv_parent_items.setHasFixedSize(true);
        rv_parent_items.setLayoutManager(new LinearLayoutManager(getContext()));
        parentAdapter = new ParentAdapter(getContext(), hashedCoursesMap);
        rv_parent_items.setAdapter(parentAdapter);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Hashtags");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                hashTags.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    hashTags.add(snap.getKey());
                }
                System.out.println(hashTags);
                parentAdapter.notifyDataSetChanged();

                List<Course> allCourses = new ArrayList<>();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Courses");
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Course course = snap.getValue(Course.class);
                            allCourses.add(course);
                        }
                        for (int poz = 0; poz < hashTags.size(); poz++) {
                            List<Course> hashedCourses = new ArrayList<>();
                            for (Course c : allCourses) {
                                if (c.getDescription().contains("#" + hashTags.get(poz))) {
                                    hashedCourses.add(c);
                                }
                            }
                            hashedCoursesMap.put(hashTags.get(poz), hashedCourses);
                        }
                        System.out.println(hashedCoursesMap);
                        parentAdapter.notifyDataSetChanged();
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