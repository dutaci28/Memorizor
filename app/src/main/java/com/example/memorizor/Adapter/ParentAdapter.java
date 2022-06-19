package com.example.memorizor.Adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
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
        holder.tv_hashtag.setTranslationX(-800);
        holder.tv_hashtag.setAlpha(0);
        holder.tv_hashtag.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(position*100).start();

        holder.rv_parent.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false));
        childAdapter = new ChildAdapter(mContext, hashedCoursesMap.get(key));
        holder.rv_parent.setAdapter(childAdapter);
        childAdapter.notifyDataSetChanged();

        holder.rv_parent.setTranslationX(800);
        holder.rv_parent.setAlpha(0);
        holder.rv_parent.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300 + position*100).start();
    }

    @Override
    public int getItemCount() {
        return hashedCoursesMap.keySet().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_hashtag;
        public RecyclerView rv_parent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_hashtag = itemView.findViewById(R.id.tv_hashtag);
            rv_parent = itemView.findViewById(R.id.rv_parent);
        }
    }
}
