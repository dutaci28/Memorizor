package com.example.memorizor.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memorizor.CourseActivity;
import com.example.memorizor.Fragments.SearchFragment;
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
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

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
        String uid = mCourses.get(position).getPublisher();
        final User[] hostUser = new User[1];

        Query query1 = FirebaseDatabase.getInstance().getReference().child("Users")
                .orderByChild("id").startAt(uid).endAt(uid + "\uf8ff");

        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    hostUser[0] = snap.getValue(User.class);
                    holder.fullname.setText(hostUser[0].getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Course course = mCourses.get(position);

        LayerDrawable stars = (LayerDrawable) holder.ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);

        Query query2 = FirebaseDatabase.getInstance().getReference().child("Ratings")
                .orderByChild("courseId").startAt(course.getCourseId()).endAt(course.getCourseId() + "\uf8ff");

        List<Rating> ratings = new ArrayList<>();
        query2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Rating rating = snap.getValue(Rating.class);
                    ratings.add(rating);
                }

                int finalRating = 0;
                int noRatings = 0;
                for (Rating r : ratings) {
                    noRatings++;
                    finalRating += r.getValue();

                }
                if (noRatings > 0) {
                    finalRating /= noRatings;
                }
                holder.ratingBar.setRating((int)finalRating);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.title.setText(course.getTitle());
        holder.price.setText("$" + course.getPrice());
        Picasso.get().load(course.getImageUrl()).placeholder(R.drawable.backgroudn).into(holder.image);

        isBookmarked(course.getCourseId(), holder.btn_bookmark);

        holder.btn_bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((Boolean)holder.btn_bookmark.getTag()){
                    FirebaseDatabase.getInstance().getReference().child("Bookmarks").child(firebaseUser.getUid()).child("Bookmarked").child(course.getCourseId()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Bookmarks").child(firebaseUser.getUid()).child("Bookmarked").child(course.getCourseId()).removeValue();
                }
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CourseActivity.class);
                intent.putExtra("courseId", course.getCourseId());
                mContext.startActivity(intent);
            }
        });

        holder.cardView.setTranslationY(-800);
        holder.cardView.setAlpha(0);
        holder.cardView.animate().translationY(0).alpha(1).setDuration(300).setStartDelay(position*100).start();

    }

    @Override
    public int getItemCount() {
        return mCourses.size();
    }

    private void isBookmarked(final String id, final ImageButton btn_bookmark) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Bookmarks").child(firebaseUser.getUid()).child("Bookmarked");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(id).exists()) {
                    btn_bookmark.setTag(false);
                    btn_bookmark.setImageResource(R.drawable.ic_baseline_bookmark_remove_24);
                }
                else {
                    btn_bookmark.setTag(true);
                    btn_bookmark.setImageResource(R.drawable.ic_baseline_bookmark_border_24);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView fullname;
        public TextView title;
        public TextView price;
        public ImageView image;
        public ImageButton btn_bookmark;
        public RatingBar ratingBar;

        public CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            fullname = itemView.findViewById(R.id.fullname);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            price = itemView.findViewById(R.id.price);
            cardView = itemView.findViewById(R.id.cardView);
            btn_bookmark = itemView.findViewById(R.id.btn_bookmark);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }

}
