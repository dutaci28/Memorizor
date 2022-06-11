package com.example.memorizor.Adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class ParentAdapter extends RecyclerView.Adapter<ParentAdapter.ViewHolder> {
    private Context mContext;
    private Map<String, List<Course>> hashedCoursesMap;

    public ParentAdapter(Context mContext, Map<String, List<Course>> hashedCoursesMap) {
        this.mContext = mContext;
        this.hashedCoursesMap = hashedCoursesMap;
    }

    @NonNull
    @Override
    public ParentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.parent_item, parent, false);
        return new ParentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int poz = position;
        ChildAdapter childAdapter;

        String key = hashedCoursesMap.keySet().toArray()[poz].toString();

        holder.tv_hashtag.setText("#" + key);
        holder.rv_child.setHasFixedSize(true);
        holder.rv_child.setLayoutManager(new LinearLayoutManager(mContext));
        childAdapter = new ChildAdapter(mContext, hashedCoursesMap.get(key));
        holder.rv_child.setAdapter(childAdapter);
        childAdapter.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return hashedCoursesMap.keySet().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_hashtag;
        public RecyclerView rv_child;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_hashtag = itemView.findViewById(R.id.tv_hashtag);
            rv_child = itemView.findViewById(R.id.rv_child);
        }
    }
}
