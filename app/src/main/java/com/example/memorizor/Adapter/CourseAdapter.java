package com.example.memorizor.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memorizor.Model.Course;
import com.example.memorizor.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {

    private Context mContext;
    private List<Course> mCourses;
    private boolean isFragment;

    private FirebaseUser firebaseUser;

    public CourseAdapter(Context mContext, List<Course> mCourses, boolean isFragment) {
        this.mContext = mContext;
        this.mCourses = mCourses;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.course_item, parent, false);
        return new CourseAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Course course = mCourses.get(position);

        holder.title.setText(course.getTitle());
        holder.description.setText(course.getDescription());
        holder.price.setText("$" + course.getPrice());

        Picasso.get().load(course.getImageUrl()).placeholder(R.mipmap.ic_launcher).into(holder.image);

        //isBought(course.getId(),holder.butonCuPret?)
        //face la search fragment

    }

    @Override
    public int getItemCount() {
        return mCourses.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView title;
        public TextView description;
        public TextView price;
        public ImageView image;
        public Button details;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            price = itemView.findViewById(R.id.price);
        }
    }

}
