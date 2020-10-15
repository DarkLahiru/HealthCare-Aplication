package com.example.healthcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import profile.User;


public class RegistrationActivity extends AppCompatActivity {

    Button register;
    EditText emailId, Password, rePassword;
    FirebaseAuth mFirebaseAuth;
    DatabaseReference rootReference;
    FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);
        emailId = findViewById(R.id.usereT);
        Password = findViewById(R.id.passeT);
        rePassword = findViewById(R.id.rePasseT);
        register = findViewById(R.id.btnSignUp);
        mFirebaseAuth = FirebaseAuth.getInstance();
        rootReference = FirebaseDatabase.getInstance().getReference();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailId.getText().toString();
                String pwd = Password.getText().toString();
                String rePwd = rePassword.getText().toString();
                if (email.isEmpty()) {
                    emailId.setError("Please enter email");
                    emailId.requestFocus();
                } else if (pwd.isEmpty()) {
                    Password.setError("Please enter password");
                    Password.requestFocus();
                } else if (rePwd.isEmpty()) {
                    rePassword.setError("Please enter password again");
                    rePassword.requestFocus();
                } else if (!rePwd.equals(pwd)) {
                    rePassword.setError("Please enter password correctly");
                    rePassword.requestFocus();
                } else {
                    pwd = pwd.trim();
                    int len = rePassword.length();
                    if (len < 6) {
                        Password.setError("Password must have at least 6 characters");
                        Password.requestFocus();
                    } else {
                        final String finalPwd = pwd;
                        mFirebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (!task.isSuccessful()) {
                                            Toast.makeText(RegistrationActivity.this, "SignUp Unsuccessful, Please Try Again", Toast.LENGTH_SHORT).show();
                                        } else {

                                            firebaseUser = mFirebaseAuth.getCurrentUser();
                                            User myUserInsertObj = new User(email, finalPwd);

                                            rootReference.child("Patients").child(firebaseUser.getUid()).child("LoginDetails").setValue(myUserInsertObj)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isComplete()) {
                                                                Toast.makeText(RegistrationActivity.this, "You Created Account Successfully", Toast.LENGTH_SHORT).show();
                                                                finish();
                                                                rootReference.child("Users").child(firebaseUser.getUid()).child("First Time Login").setValue("false");
                                                                rootReference.child("Users").child(firebaseUser.getUid()).child("LoginType").setValue("Patient");
                                                                rootReference.child("Patients").child(firebaseUser.getUid()).child("MyProfile").child("id").setValue(firebaseUser.getUid());
                                                                Intent myIntent = new Intent(getApplicationContext(), LoginActivity.class);
                                                                startActivity(myIntent);

                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                }
                        );
                    }
                }
            }
        });


    }


}
