package com.example.memorizor.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.memorizor.MainActivity;
import com.example.memorizor.Model.User;
import com.example.memorizor.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginTabFragment extends Fragment {

    private EditText email;
    private EditText password;
    private Button login;

    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_login_tab, container, false);

        email=root.findViewById(R.id.et_login_email);
        password = root.findViewById(R.id.et_login_password);
        login = root.findViewById(R.id.btn_login_login);

        email.setTranslationX(800);
        password.setTranslationX(800);
        login.setTranslationX(800);

        email.setAlpha(0);
        password.setAlpha(0);
        login.setAlpha(0);

        email.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(100).start();
        password.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(200).start();
        login.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();

        mAuth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();

                if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){
                    Toast.makeText(getContext(), "Empty credentials", Toast.LENGTH_SHORT).show();
                } else {

                    Query query1 = FirebaseDatabase.getInstance().getReference().child("Users")
                            .orderByChild("email").startAt(email.getText().toString()).endAt(email.getText().toString() + "\uf8ff");

                    query1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean exists = false;

                            for (DataSnapshot snap : snapshot.getChildren()) {
                                User user = snap.getValue(User.class);
                                if(user.getEmail().equals(email.getText().toString())){
                                    exists = true;
                                }
                            }

                            if(exists == false){
                                Toast.makeText(getContext(), "User does not exist", Toast.LENGTH_SHORT).show();
                            } else {
                                loginUser(txt_email , txt_password);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        return root;
    }

    private void loginUser(String email, String password) {

        ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Logging in");
        pd.show();

        mAuth.signInWithEmailAndPassword(email , password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Intent intent = new Intent(getContext() , MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    pd.dismiss();
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    Toast.makeText(getContext(), "Password is incorrect", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


}
