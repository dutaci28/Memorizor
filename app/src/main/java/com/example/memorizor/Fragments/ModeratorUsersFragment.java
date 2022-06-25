package com.example.memorizor.Fragments;

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
import android.widget.EditText;
import android.widget.TextView;

import com.example.memorizor.Adapter.SimpleUserAdapter;
import com.example.memorizor.Model.User;
import com.example.memorizor.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ModeratorUsersFragment extends Fragment {

    private RecyclerView rv_users;
    private List<User> mUsers;
    private SimpleUserAdapter userAdapter;
    private EditText et_search_users;
    private TextView tv_total_users;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_moderator_users, container, false);

        rv_users = view.findViewById(R.id.rv_users);
        et_search_users = view.findViewById(R.id.et_search_users);
        tv_total_users = view.findViewById(R.id.tv_total_users);

        mUsers = new ArrayList<>();

        rv_users.setHasFixedSize(true);
        rv_users.setLayoutManager(new LinearLayoutManager(getContext()));
        userAdapter = new SimpleUserAdapter(getContext(), mUsers, true);
        rv_users.setAdapter(userAdapter);

        readUsers();

        et_search_users.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUser(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return view;
    }

    private void readUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    User user = snap.getValue(User.class);
                    mUsers.add(user);
                }
                String total = "Total users: " + mUsers.size();
                tv_total_users.setText(total);
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchUser(String s) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    User user = snap.getValue(User.class);
                    if (user.getName().toLowerCase().contains(s.toLowerCase()) || user.getEmail().toLowerCase().contains(s.toLowerCase())) {
                        mUsers.add(user);
                    }
                    String total = "Searched users: " + mUsers.size();
                    tv_total_users.setText(total);
                    userAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}