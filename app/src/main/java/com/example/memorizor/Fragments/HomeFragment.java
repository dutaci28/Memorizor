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
import com.example.memorizor.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView rv_parent_items;
    private List<String> hashTags;
    private ParentAdapter parentAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        hashTags = new ArrayList<>();

        rv_parent_items = view.findViewById(R.id.rv_parent_items);
        rv_parent_items.setHasFixedSize(true);
        rv_parent_items.setLayoutManager(new LinearLayoutManager(getContext()));
        parentAdapter = new ParentAdapter(getContext(), hashTags);
        rv_parent_items.setAdapter(parentAdapter);

        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Hashtags");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                hashTags.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    hashTags.add(snap.getKey());
                }
                parentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}