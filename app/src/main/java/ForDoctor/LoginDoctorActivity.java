package ForDoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthcare.LoginActivity;
import com.example.healthcare.R;
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

import ForDoctor.MyProfile.FirstDoctorProfileActivity;

public class LoginDoctorActivity extends AppCompatActivity {
    TextInputLayout emailId, password;
    Button signIn;
    TextView signUp,forgotPw;
    FirebaseAuth mFirebaseAuth;
    DatabaseReference rootReference;
    FirebaseUser firebaseUser;
    public String loginType;
    public String firstTimeLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_doctor);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.eTUserNameDoctor);
        password = findViewById(R.id.eTPasswordDoctor);
        signIn = findViewById(R.id.btnSignInDoc);
        signUp = findViewById(R.id.btnRegisterHereDoc);
        forgotPw = findViewById(R.id.txtForgotPw);

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
                    mFirebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(LoginDoctorActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(LoginDoctorActivity.this, "Login Error, Please Login Again", Toast.LENGTH_SHORT).show();
                            } else {
                                mFirebaseAuth = FirebaseAuth.getInstance();
                                firebaseUser = mFirebaseAuth.getCurrentUser();
                                rootReference = FirebaseDatabase.getInstance().getReference();

                                if (firebaseUser.isEmailVerified()) {

                                    rootReference.child("Users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            loginType = Objects.requireNonNull(snapshot.child("LoginType").getValue()).toString();
                                            firstTimeLogin = Objects.requireNonNull(snapshot.child("First Time Login").getValue()).toString();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });


                                    rootReference.child("Doctors").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                            if ("Doctor".equalsIgnoreCase(loginType)) {
                                                //Toast.makeText(LoginDoctorActivity.this, "You are a doctor", Toast.LENGTH_SHORT).show();
                                                if (firstTimeLogin.equals("false")) {
                                                    //rootReference.child("Users").child(firebaseUser.getUid()).child("First Time Login").setValue("true");
                                                    Intent first = new Intent(LoginDoctorActivity.this, FirstDoctorProfileActivity.class);
                                                    startActivity(first);
                                                    finish();

                                                } else {
                                                    Intent dash = new Intent(LoginDoctorActivity.this, NavigationDoctor.class);
                                                    startActivity(dash);
                                                }
                                            } else {

                                                Toast.makeText(LoginDoctorActivity.this, "Login Error, You are not a Doctor !!!", Toast.LENGTH_SHORT).show();
                                            }
                                        }


                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                } else {
                                    Toast.makeText(LoginDoctorActivity.this, "Please verify your Email ", Toast.LENGTH_SHORT).show();
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
                Intent intSignUp = new Intent(getApplicationContext(), RegistrationDoctorActivity.class);
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
                                Toast.makeText(LoginDoctorActivity.this,"Reset Link Sent To Your Email",Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginDoctorActivity.this,"Error !! Reset Link is not Sent"+e.getMessage(),Toast.LENGTH_SHORT).show();
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
}
