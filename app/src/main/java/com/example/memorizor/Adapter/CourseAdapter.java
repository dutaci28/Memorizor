package com.example.memorizor.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memorizor.CourseActivity;
import com.example.memorizor.Fragments.SearchFragment;
import com.example.memorizor.Model.Course;
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
                    holder.username.setText(hostUser[0].getUsername());
                    Picasso.get().load(hostUser[0].getProfileImageUrl()).into(holder.profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Course course = mCourses.get(position);

        holder.title.setText(course.getTitle());
        holder.description.setText(course.getDescription());
        holder.price.setText("$" + course.getPrice());
        Picasso.get().load(course.getImageUrl()).placeholder(R.mipmap.ic_launcher).into(holder.image);

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

        holder.btn_open_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CourseActivity.class);
                intent.putExtra("courseId", course.getCourseId());
                mContext.startActivity(intent);
            }
        });

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
        public TextView username;
        public TextView title;
        public TextView description;
        public TextView price;
        public ImageView image;
        public Button btn_open_course;
        public ImageButton btn_bookmark;
        public CircleImageView profileImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            fullname = itemView.findViewById(R.id.fullname);
            username = itemView.findViewById(R.id.username);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            price = itemView.findViewById(R.id.price);
            btn_open_course = itemView.findViewById(R.id.btn_open_course);
            btn_bookmark = itemView.findViewById(R.id.btn_bookmark);
            profileImage = itemView.findViewById(R.id.image_profile);
        }
    }

}
