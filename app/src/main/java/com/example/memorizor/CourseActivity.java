package com.example.memorizor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.memorizor.Adapter.VideoAdapter;
import com.example.memorizor.Model.Course;
import com.example.memorizor.Model.Rating;
import com.example.memorizor.Model.User;
import com.example.memorizor.Model.Video;
import com.gkemon.XMLtoPDF.PdfGenerator;
import com.gkemon.XMLtoPDF.PdfGeneratorListener;
import com.gkemon.XMLtoPDF.model.FailureResponse;
import com.gkemon.XMLtoPDF.model.SuccessResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CourseActivity extends AppCompatActivity {

    private ImageView iv_photo;
    private TextView tv_title;
    private TextView tv_description;
    private TextView tv_price;
    private Button btn_bookmark;
    private Button btn_buy;
    private RecyclerView rv_videos;
    private RatingBar rating_bar;
    private TextView tv_preview;
    private ImageView iv_preview;
    private ConstraintLayout bottom_constraint_layout;
    private TextView tv_ratings_number_course;
    private ImageButton btn_delete;
    private Button btn_generate_diploma;

    //diploma
    TextView tv_diploma_name, tv_diploma_course_title, tv_diploma_date;

    private String courseId;
    private Course course;
    private Object lock = new Object();
    boolean rated = false;
    int noRatings = 0;
    private VideoAdapter videoAdapter;
    private List<Video> mVideos = new ArrayList<>();
    private List<Uri> mVideoUris = new ArrayList<>();

    private FirebaseUser firebaseUser;
    private User currentUser;

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
        tv_price = findViewById(R.id.tv_price);
        btn_buy = findViewById(R.id.btn_buy);
        rv_videos = findViewById(R.id.rv_videos);
        rating_bar = findViewById(R.id.rating_bar);
        tv_preview = findViewById(R.id.tv_preview);
        iv_preview = findViewById(R.id.iv_preview);
        bottom_constraint_layout = findViewById(R.id.bottom_constraint_layout);
        tv_ratings_number_course = findViewById(R.id.tv_ratings_number_course);
        btn_delete = findViewById(R.id.btn_delete);
        btn_generate_diploma = findViewById(R.id.btn_generate_diploma);

        courseId = getIntent().getStringExtra("courseId");

        rv_videos.setHasFixedSize(true);
        rv_videos.setLayoutManager(new LinearLayoutManager(this));
        videoAdapter = new VideoAdapter(this, mVideos);
        rv_videos.setAdapter(videoAdapter);

        readCourse();
        isBookmarked(courseId, btn_bookmark);

        btn_generate_diploma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View content = getLayoutInflater().inflate(R.layout.diploma_print_layout, null);
                TextView tv_diploma_name, tv_diploma_course_title;
                tv_diploma_name = content.findViewById(R.id.tv_diploma_name);
                tv_diploma_course_title = content.findViewById(R.id.tv_diploma_course_title);
                tv_diploma_date = content.findViewById(R.id.tv_diploma_date);
                tv_diploma_name.setText(currentUser.getName());
                tv_diploma_course_title.setText(course.getTitle());
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDateTime now = LocalDateTime.now();
                tv_diploma_date.setText(dtf.format(now));

                PdfGenerator.getBuilder()
                        .setContext(CourseActivity.this)
                        .fromViewSource()
                        .fromView(content)
                        .setFileName("Diploma")
                        .setFolderNameOrPath("diploma_folder")
                        .actionAfterPDFGeneration(PdfGenerator.ActionAfterPDFGeneration.SHARE)
                        .build(new PdfGeneratorListener() {
                            @Override
                            public void onFailure(FailureResponse failureResponse) {
                                super.onFailure(failureResponse);
                                /* If pdf is not generated by an error then you will findout the reason behind it
                                 * from this FailureResponse. */
                            }
                            @Override
                            public void onStartPDFGeneration() {
                                /*When PDF generation begins to start*/
                            }

                            @Override
                            public void onFinishPDFGeneration() {
                                /*When PDF generation is finished*/
                            }

                            @Override
                            public void showLog(String log) {
                                super.showLog(log);
                                /*It shows logs of events inside the pdf generation process*/
                            }

                            @Override
                            public void onSuccess(SuccessResponse response) {
                                super.onSuccess(response);
                                System.out.println(response.getPath());

                                /* If PDF is generated successfully then you will find SuccessResponse
                                 * which holds the PdfDocument,File and path (where generated pdf is stored)*/

                            }
                        });
            }
        });

        iv_preview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener1 = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:

                                DatabaseReference dbref= FirebaseDatabase.getInstance().getReference().child("Courses");
                                Query query=dbref.child(courseId);

                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        dataSnapshot.getRef().removeValue();
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });

                                DatabaseReference dbref1 = FirebaseDatabase.getInstance().getReference().child("Videos");
                                Query query1=dbref1.orderByChild("hostCourseId").startAt(courseId).endAt(courseId + "\uf8ff");

                                query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for(DataSnapshot snap : dataSnapshot.getChildren()){
                                            snap.getRef().removeValue();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });

                                DatabaseReference dbref2 = FirebaseDatabase.getInstance().getReference().child("Ratings");
                                Query query2=dbref2.orderByChild("courseId").startAt(courseId).endAt(courseId + "\uf8ff");

                                query2.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for(DataSnapshot snap : dataSnapshot.getChildren()){
                                            snap.getRef().removeValue();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });

                                DatabaseReference dbref3 = FirebaseDatabase.getInstance().getReference().child("Bookmarks");
                                Query query3=dbref3;

                                query3.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for(DataSnapshot snap : dataSnapshot.getChildren()){
                                            if (snap.child("Bookmarked").child(courseId).exists()) {
                                                snap.child("Bookmarked").child(courseId).getRef().removeValue();
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });

                                DatabaseReference dbref4 = FirebaseDatabase.getInstance().getReference().child("Purchases");
                                Query query4=dbref4;

                                query4.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for(DataSnapshot snap : dataSnapshot.getChildren()){
                                            if (snap.child("Purchased").child(courseId).exists()) {
                                                snap.child("Purchased").child(courseId).getRef().removeValue();
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });

                                DatabaseReference dbref5 = FirebaseDatabase.getInstance().getReference().child("Hashtags");
                                Query query5=dbref5;

                                query5.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for(DataSnapshot snap : dataSnapshot.getChildren()){
                                            if (snap.child(courseId).exists()) {
                                                snap.child(courseId).getRef().removeValue();
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });

                                finish();
                                if(currentUser.getPermissions().equals("user")){
                                    startActivity(new Intent(CourseActivity.this, MainActivity.class));
                                    finish();
                                } else if(currentUser.getPermissions().equals("moderator")){
                                    startActivity(new Intent(CourseActivity.this, MainActivityModerator.class));
                                    finish();
                                }


                                break;

                            case DialogInterface.BUTTON_NEGATIVE:

                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(CourseActivity.this);
                builder.setMessage("Do you want to delete the course?").setPositiveButton("Yes", dialogClickListener1)
                        .setNegativeButton("No", dialogClickListener1).show();
            }
        });

        btn_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_buy.getText().toString().equals("Buy Now")) {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDateTime now = LocalDateTime.now();
                    FirebaseDatabase.getInstance().getReference().child("Purchases").child(firebaseUser.getUid()).child("Purchased").child(course.getCourseId()).setValue(dtf.format(now));
                    tv_preview.setVisibility(View.GONE);
                    iv_preview.setVisibility(View.GONE);

                } else {
                    FirebaseDatabase.getInstance().getReference().child("Purchases").child(firebaseUser.getUid()).child("Purchased").child(course.getCourseId()).removeValue();
                    tv_preview.setVisibility(View.VISIBLE);
                    iv_preview.setVisibility(View.VISIBLE);
                }
            }
        });

        btn_bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((Boolean) btn_bookmark.getTag()) {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDateTime now = LocalDateTime.now();
                    FirebaseDatabase.getInstance().getReference().child("Bookmarks").child(firebaseUser.getUid()).child("Bookmarked").child(course.getCourseId()).setValue(dtf.format(now));
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Bookmarks").child(firebaseUser.getUid()).child("Bookmarked").child(course.getCourseId()).removeValue();
                }
            }
        });

        LayerDrawable stars = (LayerDrawable) rating_bar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);

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
                    tv_price.setText("$" + course.getPrice());
                    tv_description.setText(course.getDescription());
                    Picasso.get().load(course.getImageUrl()).into(iv_photo);
                    if (course.getPublisher().equals(firebaseUser.getUid())) {
                        btn_buy.setVisibility(View.GONE);
                        tv_price.setVisibility(View.GONE);
                        tv_preview.setVisibility(View.GONE);
                        iv_preview.setVisibility(View.GONE);
                        btn_bookmark.setVisibility(View.GONE);
                        btn_delete.setVisibility(View.VISIBLE);
                    } else {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Purchases").child(firebaseUser.getUid()).child("Purchased");
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.child(course.getCourseId()).exists()) {
                                    btn_buy.setText("Refund Course");
                                    tv_preview.setVisibility(View.GONE);
                                    iv_preview.setVisibility(View.GONE);

                                } else {
                                    btn_buy.setText("Buy Now");
                                    tv_preview.setVisibility(View.VISIBLE);
                                    iv_preview.setVisibility(View.VISIBLE);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
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
                ratings.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Rating rating = snap.getValue(Rating.class);
                    ratings.add(rating);
                }

                int finalRating = 0;
                for (Rating r : ratings) {
                    if (r.getUserId().equals(firebaseUser.getUid())) {
                        finalRating += r.getValue();
                        rating_bar.setIsIndicator(true);
                        rated = true;
                    }
                }

                noRatings = ratings.size();
                tv_ratings_number_course.setText("(" + noRatings + " ratings)");

                if (rated) {
                    rating_bar.setRating(finalRating);
                } else {
                    rating_bar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                        @Override
                        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                            if(fromUser){
                                int ratingValue = (int) rating_bar.getRating();
                                Toast.makeText(CourseActivity.this, "You rated " + ratingValue + " stars out of 5.", Toast.LENGTH_SHORT).show();

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ratings");
                                String ratingId = ref.push().getKey();
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("courseId", courseId);
                                map.put("ratingId", ratingId);
                                map.put("userId", firebaseUser.getUid());
                                map.put("value", ratingValue);

                                ref.child(ratingId).setValue(map);
                                rating_bar.setIsIndicator(true);
                            }

                        }
                    });
                }
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

        Query query4 = FirebaseDatabase.getInstance().getReference().child("Users")
                .orderByChild("id").startAt(firebaseUser.getUid()).endAt(firebaseUser.getUid() + "\uf8ff");

        query4.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    currentUser = snap.getValue(User.class);
                }

                if(currentUser.getPermissions().equals("moderator") || course.getPublisher().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    btn_delete.setVisibility(View.VISIBLE);
                } else {
                    btn_delete.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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