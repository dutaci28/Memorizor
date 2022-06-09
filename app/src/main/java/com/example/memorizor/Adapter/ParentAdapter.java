package com.example.memorizor.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memorizor.R;


import java.util.List;

public class ParentAdapter extends RecyclerView.Adapter<ParentAdapter.ViewHolder>{
    private Context mContext;
    private List<String> hashTags;

    public ParentAdapter(Context mContext, List<String> hashTags) {
        this.mContext = mContext;
        this.hashTags = hashTags;
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
        holder.tv_hashtag.setText(hashTags.get(poz));
    }

    @Override
    public int getItemCount() {
        return hashTags.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_hashtag;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_hashtag = itemView.findViewById(R.id.tv_hashtag);
        }
    }
}
