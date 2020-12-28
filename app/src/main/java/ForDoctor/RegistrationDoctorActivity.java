package ForDoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.healthcare.R;
import com.example.healthcare.RegistrationActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import Profile.User;

public class RegistrationDoctorActivity extends AppCompatActivity {

    Button register;
    EditText emailId, Password, rePassword;
    FirebaseAuth mFirebaseAuth;
    DatabaseReference rootReference;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_doctor);

        emailId = findViewById(R.id.usereTDoctorEmail);
        Password = findViewById(R.id.passeTDoctor);
        rePassword = findViewById(R.id.rePasseTDoctor);
        register = findViewById(R.id.btnSignUpDoc);

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
                        mFirebaseAuth.createUserWithEmailAndPassword(email, pwd)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                firebaseUser = mFirebaseAuth.getCurrentUser();

                                assert firebaseUser != null;
                                firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Toast.makeText(RegistrationDoctorActivity.this, "Verification Email has been sent to your Email ", Toast.LENGTH_LONG).show();

                                        Toast.makeText(RegistrationDoctorActivity.this, "You Created Account Successfully", Toast.LENGTH_SHORT).show();

                                        rootReference.child("Users").child(firebaseUser.getUid()).child("First Time Login").setValue("false");
                                        rootReference.child("Users").child(firebaseUser.getUid()).child("LoginType").setValue("Doctor");
                                        Intent myIntent = new Intent(getApplicationContext(), LoginDoctorActivity.class);
                                        startActivity(myIntent);
                                        finish();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RegistrationDoctorActivity.this, "SignUp Unsuccessful, Please Try Again" + e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                }
            }
        });
    }
}
