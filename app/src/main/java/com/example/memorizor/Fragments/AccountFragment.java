package com.example.memorizor.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.example.memorizor.AccountSettingsActivity;
import com.example.memorizor.Adapter.CourseAdapter;
import com.example.memorizor.Model.Course;
import com.example.memorizor.Model.User;
import com.example.memorizor.R;
import com.example.memorizor.StartActivity;
import com.example.memorizor.UserStatsActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class AccountFragment extends Fragment {

    private CircleImageView profileImage;
    private TextView email;
    private TextView fullname;
    private DrawerLayout drawer_layout;
    private FloatingActionButton fab_settings;
    private NavigationView account_navigation_view;

    private RecyclerView rv_courses_account;
    private List<String> accountCourses = new ArrayList<>();
    private List<Course> mCourses = new ArrayList<>();
    private CourseAdapter courseAdapter;

    private User currentUser;

    public static final int GET_IMAGE_FROM_GALLERY = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        profileImage = view.findViewById(R.id.image_profile);
        email = view.findViewById(R.id.tv_email);
        fullname = view.findViewById(R.id.tv_fullname);
        drawer_layout = view.findViewById(R.id.drawer_layout);
        fab_settings = view.findViewById(R.id.fab_settings);
        account_navigation_view = view.findViewById(R.id.account_navigation_view);

        readUser();

        rv_courses_account = view.findViewById(R.id.rv_courses_account);
        rv_courses_account.setHasFixedSize(true);
        rv_courses_account.setLayoutManager(new LinearLayoutManager(getContext()));

        mCourses = new ArrayList<>();
        courseAdapter = new CourseAdapter(getContext(), mCourses, true);
        rv_courses_account.setAdapter(courseAdapter);

        populateAccountCourses();

        fab_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!drawer_layout.isDrawerOpen(GravityCompat.END))
                    drawer_layout.openDrawer(GravityCompat.END);
                else drawer_layout.closeDrawer(GravityCompat.START);
            }
        });

        account_navigation_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_account_details:
                        Intent intent = new Intent(getContext(), AccountSettingsActivity.class);
                        intent.putExtra("userId", currentUser.getId());
                        getContext().startActivity(intent);
                        break;

                    case R.id.nav_statistics:

                        Intent intent1 = new Intent(getContext(), UserStatsActivity.class);
                        intent1.putExtra("userId", currentUser.getId());
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getContext().startActivity(intent1);

                        break;

                    case R.id.nav_signout:

                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        FirebaseAuth.getInstance().signOut();
                                        startActivity(new Intent(getContext(), StartActivity.class));
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:

                                        break;
                                }
                            }
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();

                        break;
                }
                return false;
            }
        });

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
                    email.setText(currentUser.getEmail());
                    fullname.setText(currentUser.getName());
                    if (currentUser.getProfileImageUrl().equals("")) {
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

            StorageReference profileImagePath = FirebaseStorage.getInstance().getReference("ProfileImages").child(currentUser.getName() + System.currentTimeMillis() + "." + getFileExtension(imageUri));
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

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("name", currentUser.getName());
                    map.put("email", currentUser.getEmail());
                    map.put("id", userId);
                    map.put("permissions", "user");
                    map.put("profileImageUrl", imageUrl);

                    ref.setValue(map);
                }
            });
        }
    }

    private void populateAccountCourses() {
        FirebaseDatabase.getInstance().getReference().child("Courses").orderByChild("publisher").startAt(FirebaseAuth.getInstance().getCurrentUser().getUid()).endAt(FirebaseAuth.getInstance().getCurrentUser().getUid() + "\uf8ff").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                accountCourses.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    accountCourses.add(snap.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Courses");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mCourses.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    for (String s : accountCourses) {
                        Course course = snap.getValue(Course.class);
                        if (course.getCourseId().equals(s)) {
                            mCourses.add(course);
                        }
                    }
                }
                courseAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }
}