package com.example.memorizor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.memorizor.Adapter.CourseAdapter;
import com.example.memorizor.Adapter.VideoUploadAdapter;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddActivity extends AppCompatActivity {

    private ImageView image;
    private EditText title;
    private EditText description;
    private EditText price;
    private RecyclerView rv_videos_upload;
    private Button upload;
    private Button btn_pick_video;

    private Uri imageUri;
    private Uri videoUri;

    private List<Uri> mVideoUploadUris = new ArrayList<>();
    private VideoUploadAdapter videoUploadAdapter;

    private String courseId;

    public static final int GET_IMAGE_FROM_GALLERY = 2;
    public static final int GET_VIDEO_FROM_GALLERY = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        getSupportActionBar().hide();

        image = findViewById(R.id.image);
        title = findViewById(R.id.etTitle);
        description = findViewById(R.id.etDescription);
        price = findViewById(R.id.etPrice);
        rv_videos_upload = findViewById(R.id.rv_videos_upload);
        upload = findViewById(R.id.btnUpload);
        btn_pick_video = findViewById(R.id.btn_pick_video);

        rv_videos_upload.setHasFixedSize(true);
        rv_videos_upload.setLayoutManager(new LinearLayoutManager(this));
        videoUploadAdapter = new VideoUploadAdapter(this, mVideoUploadUris);
        rv_videos_upload.setAdapter(videoUploadAdapter);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_IMAGE_FROM_GALLERY);
            }
        });

        btn_pick_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.INTERNAL_CONTENT_URI), GET_VIDEO_FROM_GALLERY);
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadCourse();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_IMAGE_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            imageUri = data.getData();
            image.setImageURI(imageUri);
        }
        if (requestCode == GET_VIDEO_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            videoUri = data.getData();
            mVideoUploadUris.add(videoUri);
            videoUploadAdapter.notifyDataSetChanged();
        }
    }

    private void uploadCourse() {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();
        if (imageUri != null) {
            StorageReference imagePath = FirebaseStorage.getInstance().getReference("CourseImages").child(title.getText().toString() + System.currentTimeMillis() + "." + getFileExtension(imageUri));
            imagePath.putFile(imageUri).continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return imagePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    String imageUrl = task.getResult().toString();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Courses");
                    courseId = ref.push().getKey();
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("courseId", courseId);
                    map.put("imageUrl", imageUrl);
                    map.put("title", title.getText().toString());
                    map.put("description", description.getText().toString());
                    map.put("price", price.getText().toString());
                    map.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());

                    ref.child(courseId).setValue(map);
                }
            });

            for (int i = 0; i < mVideoUploadUris.size(); i++) {
                int index = i;
                StorageReference videoPath = FirebaseStorage.getInstance().getReference("CourseVideos").child(title.getText().toString() + "_" + index + "_" + System.currentTimeMillis() + "." + getFileExtension(videoUri));
                videoPath.putFile(videoUri).continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return videoPath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        String videoUrl = task.getResult().toString();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Videos");

                        String videoId = ref.push().getKey();
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("videoId", videoId);
                        map.put("videoUrl", videoUrl);
                        map.put("videoIndex", index);
                        map.put("hostCourseId", courseId);
                        //map.put("title", title.getText().toString());

                        ref.child(videoId).setValue(map);

                        pd.dismiss();
                        startActivity(new Intent(AddActivity.this, MainActivity.class));
                        finish();
                    }
                });
            }
        } else {
            Toast.makeText(this, "Upload data incomplete! (No image was selected)", Toast.LENGTH_SHORT).show();
        }


    }

    private String getFileExtension(Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(uri));
    }
}