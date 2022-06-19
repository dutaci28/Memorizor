package com.example.memorizor.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memorizor.CourseActivity;
import com.example.memorizor.Model.Course;
import com.example.memorizor.R;
import com.squareup.picasso.Picasso;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ViewHolder>{
    private Context mContext;
    private List<Course> hashedCourses;

    public ChildAdapter(Context mContext, List<Course> hashedCourses) {
        this.mContext = mContext;
        this.hashedCourses = hashedCourses;
    }

    @NonNull
    @Override
    public ChildAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.child_item, parent, false);
        return new ChildAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int poz = position;
        try {
            Picasso.get().load(hashedCourses.get(poz).getImageUrl()).placeholder(R.drawable.backgroudn).centerInside().fit().into(holder.image_child);
//            InputStream is = (InputStream) new URL(hashedCourses.get(poz).getImageUrl()).getContent();
//            Drawable d = Drawable.createFromStream(is, "preview");
//            holder.image_child.setImageDrawable(d);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.image_child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CourseActivity.class);
                intent.putExtra("courseId", hashedCourses.get(poz).getCourseId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return hashedCourses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image_child;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image_child = itemView.findViewById(R.id.image_child);
        }
    }
}
