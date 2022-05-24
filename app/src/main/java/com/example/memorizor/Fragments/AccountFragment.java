package com.example.memorizor.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.memorizor.MainActivity;
import com.example.memorizor.Model.User;
import com.example.memorizor.R;
import com.example.memorizor.RegisterActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class AccountFragment extends Fragment {

    private CircleImageView profileImage;
    private TextView username;
    private TextView fullname;

    private User currentUser;

    public static final int GET_IMAGE_FROM_GALLERY = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        profileImage = view.findViewById(R.id.image_profile);
        username = view.findViewById(R.id.tv_username);
        fullname = view.findViewById(R.id.tv_fullname);

        readUser();

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_IMAGE_FROM_GALLERY);
            }
        });

        return view;
    }

    private void readUser() {
        Query query1 = FirebaseDatabase.getInstance().getReference().child("Users")
                .orderByChild("id").startAt(FirebaseAuth.getInstance().getCurrentUser().getUid()).endAt(FirebaseAuth.getInstance().getCurrentUser().getUid() + "\uf8ff");

        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    currentUser = snap.getValue(User.class);
                    username.setText(currentUser.getUsername());
                    fullname.setText(currentUser.getName());
                    if(currentUser.getProfileImageUrl().equals("")){
                        Toast.makeText(getContext(), "You can upload a profile image by clicking on the circle view.", Toast.LENGTH_SHORT).show();
                    } else {
                        Picasso.get().load(currentUser.getProfileImageUrl()).into(profileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String getFileExtension(Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContext().getContentResolver().getType(uri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_IMAGE_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri imageUri = data.getData();
            profileImage.setImageURI(imageUri);

            StorageReference profileImagePath = FirebaseStorage.getInstance().getReference("ProfileImages").child(currentUser.getUsername() + System.currentTimeMillis() + "." + getFileExtension(imageUri));
            profileImagePath.putFile(imageUri).continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return profileImagePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    String imageUrl = task.getResult().toString();
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                    HashMap<String , Object> map = new HashMap<>();
                    map.put("name" , currentUser.getName());
                    map.put("email", currentUser.getEmail());
                    map.put("username" , currentUser.getUsername());
                    map.put("id" , userId);
                    map.put("profileImageUrl", imageUrl);

                    ref.setValue(map);
                }
            });
        }
    }
}