package com.example.memorizor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.memorizor.Adapter.VideoAdapter;
import com.example.memorizor.Adapter.VideoUploadAdapter;
import com.example.memorizor.Model.Course;
import com.example.memorizor.Model.Video;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CourseActivity extends AppCompatActivity {

    private ImageView iv_photo;
    private TextView tv_title;
    private TextView tv_description;
    private ImageButton btn_bookmark;
    private Button btn_buy;
    private RecyclerView rv_videos;

    private String courseId;

    private VideoAdapter videoAdapter;

    private List<Video> mVideos = new ArrayList<>();
    private List<Uri> mVideoUris = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        getSupportActionBar().hide();

        iv_photo = findViewById(R.id.image);
        tv_title = findViewById(R.id.title);
        tv_description = findViewById(R.id.description);
        btn_bookmark = findViewById(R.id.btn_bookmark);
        btn_buy = findViewById(R.id.btn_buy);
        rv_videos = findViewById(R.id.rv_videos);

        courseId = getIntent().getStringExtra("courseId");

        rv_videos.setHasFixedSize(true);
        rv_videos.setLayoutManager(new LinearLayoutManager(this));
        videoAdapter = new VideoAdapter(this, mVideos);
        rv_videos.setAdapter(videoAdapter);

        readCourse();
    }

    private void readCourse() {
        Query query1 = FirebaseDatabase.getInstance().getReference().child("Courses")
                .orderByChild("courseId").startAt(courseId).endAt(courseId + "\uf8ff");

        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Course course = snap.getValue(Course.class);
                    tv_title.setText(course.getTitle());
                    tv_description.setText(course.getDescription());
                    Picasso.get().load(course.getImageUrl()).into(iv_photo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Query query2 = FirebaseDatabase.getInstance().getReference().child("Videos")
                .orderByChild("hostCourseId").startAt(courseId).endAt(courseId + "\uf8ff");

        query2.addValueEventListener(new ValueEventListener() {
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
}