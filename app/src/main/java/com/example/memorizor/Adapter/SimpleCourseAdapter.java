package com.example.memorizor.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memorizor.CourseActivity;
import com.example.memorizor.Model.Course;
import com.example.memorizor.Model.Rating;
import com.example.memorizor.Model.User;
import com.example.memorizor.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SimpleCourseAdapter extends RecyclerView.Adapter<SimpleCourseAdapter.ViewHolder>{

    private Context mContext;
    private List<Course> mCourses;
    private boolean isFragment;

    public SimpleCourseAdapter(Context mContext, List<Course> mCourses, boolean isFragment) {
        this.mContext = mContext;
        this.mCourses = mCourses;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public SimpleCourseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.simple_course_item, parent, false);
        return new SimpleCourseAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleCourseAdapter.ViewHolder holder, int position) {
        String uid = mCourses.get(position).getPublisher();
        final User[] hostUser = new User[1];

        Query query1 = FirebaseDatabase.getInstance().getReference().child("Users")
                .orderByChild("id").startAt(uid).endAt(uid + "\uf8ff");

        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    hostUser[0] = snap.getValue(User.class);
                    holder.fullname_simple_course.setText(hostUser[0].getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        Course course = mCourses.get(position);
        holder.title_simple_course.setText(course.getTitle());
        holder.price_simple_course.setText("$" + course.getPrice());

        holder.cardView_simple_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CourseActivity.class);
                intent.putExtra("courseId", course.getCourseId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });

        holder.cardView_simple_course.setTranslationY(-800);
        holder.cardView_simple_course.setAlpha(0);
        holder.cardView_simple_course.animate().translationY(0).alpha(1).setDuration(300).setStartDelay(position*100).start();

    }

    @Override
    public int getItemCount() {
        return mCourses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView title_simple_course;
        public TextView fullname_simple_course;
        public TextView price_simple_course;

        public CardView cardView_simple_course;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title_simple_course = itemView.findViewById(R.id.title_simple_course);
            fullname_simple_course = itemView.findViewById(R.id.fullname_simple_course);
            price_simple_course = itemView.findViewById(R.id.price_simple_course);
            cardView_simple_course = itemView.findViewById(R.id.cardView_simple_course);

        }
    }
}
