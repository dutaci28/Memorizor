package com.example.memorizor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.memorizor.Adapter.CourseAdapter;
import com.example.memorizor.Adapter.VideoAdapter;
import com.example.memorizor.Adapter.VideoUploadAdapter;
import com.example.memorizor.Model.Course;
import com.example.memorizor.Model.Rating;
import com.example.memorizor.Model.Video;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CourseActivity extends AppCompatActivity {

    private ImageView iv_photo;
    private TextView tv_title;
    private TextView tv_description;
//    private ImageButton btn_bookmark;
    private Button btn_bookmark;
    private Button btn_buy;
    private RecyclerView rv_videos;
    private RatingBar rating_bar;

    private String courseId;
    private Course course;
    private Object lock = new Object();

    private VideoAdapter videoAdapter;

    private List<Video> mVideos = new ArrayList<>();
    private List<Uri> mVideoUris = new ArrayList<>();

    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        getSupportActionBar().hide();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        iv_photo = findViewById(R.id.image);
        tv_title = findViewById(R.id.title);
        tv_description = findViewById(R.id.description);
        btn_bookmark = findViewById(R.id.btn_bookmark);
        btn_buy = findViewById(R.id.btn_buy);
        rv_videos = findViewById(R.id.rv_videos);
        rating_bar = findViewById(R.id.rating_bar);

        courseId = getIntent().getStringExtra("courseId");

        rv_videos.setHasFixedSize(true);
        rv_videos.setLayoutManager(new LinearLayoutManager(this));
        videoAdapter = new VideoAdapter(this, mVideos);
        rv_videos.setAdapter(videoAdapter);

        readCourse();
        isPurchased(courseId, btn_buy);
        isBookmarked(courseId, btn_bookmark);

        btn_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_buy.getText().toString().equals("Buy Now")) {
                    FirebaseDatabase.getInstance().getReference().child("Purchases").child(firebaseUser.getUid()).child("Purchased").child(course.getCourseId()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Purchases").child(firebaseUser.getUid()).child("Purchased").child(course.getCourseId()).removeValue();

                }
            }
        });

        btn_bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((Boolean) btn_bookmark.getTag()) {
                    FirebaseDatabase.getInstance().getReference().child("Bookmarks").child(firebaseUser.getUid()).child("Bookmarked").child(course.getCourseId()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Bookmarks").child(firebaseUser.getUid()).child("Bookmarked").child(course.getCourseId()).removeValue();
                }
            }
        });

        rating_bar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                int ratingValue = (int) rating_bar.getRating();

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ratings");
                String ratingId = ref.push().getKey();
                HashMap<String, Object> map = new HashMap<>();
                map.put("courseId", courseId);
                map.put("ratingId", ratingId);
                map.put("value", ratingValue);

                ref.child(ratingId).setValue(map);

                //problema cu rating, de fiecare data cand trimiti un rating nou se trimite si ratingul deja afisat ( media celor existente )
                //fix posibil creare o bara de rating doar pt acordat ratinguri si una doar pt afisat
            }
        });
    }


    private void readCourse() {

        Query query1 = FirebaseDatabase.getInstance().getReference().child("Courses")
                .orderByChild("courseId").startAt(courseId).endAt(courseId + "\uf8ff");

        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    course = snap.getValue(Course.class);
                    tv_title.setText(course.getTitle());
                    tv_description.setText(course.getDescription());
                    Picasso.get().load(course.getImageUrl()).into(iv_photo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Query query2 = FirebaseDatabase.getInstance().getReference().child("Ratings")
                .orderByChild("courseId").startAt(courseId).endAt(courseId + "\uf8ff");

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
                rating_bar.setRating((int)finalRating);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Query query3 = FirebaseDatabase.getInstance().getReference().child("Videos")
                .orderByChild("hostCourseId").startAt(courseId).endAt(courseId + "\uf8ff");

        query3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Video video = snap.getValue(Video.class);
                    mVideos.add(video);
                    mVideoUris.add(Uri.parse(video.getVideoUrl()));
                }
                videoAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void isPurchased(final String id, final Button btn_buy) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Purchases").child(firebaseUser.getUid()).child("Purchased");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(id).exists()) {
                    btn_buy.setText("Refund Course");
                } else {
                    btn_buy.setText("Buy Now");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void isBookmarked(final String id, final Button btn_bookmark) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Bookmarks").child(firebaseUser.getUid()).child("Bookmarked");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(id).exists()) {
                    btn_bookmark.setTag(false);
                    btn_bookmark.setText("Bookmarked");
                } else {
                    btn_bookmark.setTag(true);
                    btn_bookmark.setText("Bookmark");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}