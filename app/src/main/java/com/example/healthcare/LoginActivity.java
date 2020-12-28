package com.example.healthcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import Profile.FirstMyProfileActivity;

public class LoginActivity extends AppCompatActivity {
    TextInputLayout emailId, password;
    Button signIn;
    TextView signUp, forgotPw;
    FirebaseAuth mFirebaseAuth;
    DatabaseReference rootReference;

    FirebaseUser firebaseUser;
    public String loginType;
    public String firstTimeLogin;
    private FirebaseAuth.AuthStateListener mAuthStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.eTUserName);
        password = findViewById(R.id.eTPassword);
        signIn = findViewById(R.id.btnSignIn);
        signUp = findViewById(R.id.btnRegisterHere);
        forgotPw = findViewById(R.id.txtForgotPw);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if (mFirebaseUser != null) {
                    Intent i = new Intent(LoginActivity.this, NavigationActivity.class);
                    startActivity(i);

                    /*rootReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                           String firstLogin = Objects.requireNonNull(dataSnapshot.child("First Time Login").getValue()).toString();
                           if (firstLogin.equals("false")) {
                                Intent first = new Intent(LoginActivity.this, FirstMyProfileActivity.class);
                                startActivity(first);
                               rootReference.child("First Time Login").setValue("true");
                           }
                           else {

                            }
                       }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });*/

                } else {
                    Toast.makeText(LoginActivity.this, "Please Login", Toast.LENGTH_SHORT).show();
                }
            }
        };

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailId.getEditText().getText().toString();
                String pwd = password.getEditText().getText().toString();
                if (email.isEmpty()) {
                    emailId.setError("Please enter your email");
                    emailId.requestFocus();
                } else if (pwd.isEmpty()) {
                    password.setError("Please enter your password");
                    password.requestFocus();
                } else {
                    mFirebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Login Error, Please Login Again", Toast.LENGTH_SHORT).show();
                            } else {
                                mFirebaseAuth = FirebaseAuth.getInstance();
                                firebaseUser = mFirebaseAuth.getCurrentUser();
                                rootReference = FirebaseDatabase.getInstance().getReference();

                                if (firebaseUser.isEmailVerified()) {
                                    rootReference.child("Users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            loginType = Objects.requireNonNull(snapshot.child("LoginType").getValue()).toString();
                                            firstTimeLogin = Objects.requireNonNull(snapshot.child("First Time Login").getValue()).toString();

                                            if ("Patient".equalsIgnoreCase(loginType)) {
                                               // Toast.makeText(LoginActivity.this, "Welcome Back", Toast.LENGTH_SHORT).show();
                                                if ("False".equalsIgnoreCase(firstTimeLogin)) {
                                                    Intent first = new Intent(LoginActivity.this, FirstMyProfileActivity.class);
                                                    startActivity(first);
                                                    finish();
                                                } else {

                                                    Intent dash = new Intent(LoginActivity.this, NavigationActivity.class);
                                                    startActivity(dash);
                                                }
                                            } else {

                                                Toast.makeText(LoginActivity.this, "Login Error, You are not a Patient !!!", Toast.LENGTH_SHORT).show();
                                            }
                                        }


                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                } else {
                                    firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(LoginActivity.this, "Please verify your Email ", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            }
                        }
                    });
                }

            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intSignUp = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intSignUp);
            }
        });

        forgotPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText resetMail = new EditText(v.getContext());


                AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
                alertDialog
                        .setTitle("Reset Password ?")
                        .setMessage("Enter Your Email To Received Reset Link")
                        .setView(resetMail);

                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String mail = resetMail.getText().toString();
                        mFirebaseAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(LoginActivity.this,"Reset Link Sent To Your Email",Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this,"Error !! Reset Link is not Sent"+e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                alertDialog.create().show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
}
