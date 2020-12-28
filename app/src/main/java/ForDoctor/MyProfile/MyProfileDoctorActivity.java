package ForDoctor.MyProfile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.healthcare.DoctorRateActivity;
import com.example.healthcare.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import ForDoctor.AppointmentLocationActivity;
import Profile.MyProfileActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfileDoctorActivity extends AppCompatActivity {
    TextInputLayout fullName, phoneNum,specializations, homeAddress,doctorID;
    CircleImageView profileImage;
    TextView profileName,emailId,patientMeet,feedback;

    Button btnUpdate;

    DatabaseReference rootReference;
    FirebaseUser firebaseUser;
    FirebaseAuth mFirebaseAuth;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile_doctor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        doctorID = findViewById(R.id.txtDocID);
        btnUpdate = findViewById(R.id.btnUpdateDoctor);
        profileName = findViewById(R.id.profile_nameDoctor);
        fullName = findViewById(R.id.txtFullNameDoctor);
        specializations =findViewById(R.id.txtSpecialization);
        phoneNum = findViewById(R.id.txtPhoneNumDoctor);
        homeAddress = findViewById(R.id.txtHomeAddressDoctor);
        profileImage = findViewById(R.id.profile_imageDoc);
        emailId = findViewById(R.id.emailAddressDoctor);
        patientMeet = findViewById(R.id.txtMeetLocationChange);
        feedback = findViewById(R.id.txtFeedback);

        patientMeet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goChange = new Intent(getApplicationContext(), AppointmentLocationActivity.class);
                startActivity(goChange);
            }
        });

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goChange = new Intent(getApplicationContext(), DoctorRateActivity.class);
                startActivity(goChange);
            }
        });

        String docID;
        docID = getIntent().getStringExtra("docID");
        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        if (TextUtils.isEmpty(docID)) {
            rootReference = FirebaseDatabase.getInstance().getReference().child("Doctors").child(firebaseUser.getUid());
            storageReference = FirebaseStorage.getInstance().getReference().child("Doctors").child("ProfileImage").child(firebaseUser.getUid()+".jpg");
        }
        else
        {
            btnUpdate.setVisibility(View.GONE);
            rootReference = FirebaseDatabase.getInstance().getReference().child("Doctors").child(docID);
            storageReference = FirebaseStorage.getInstance().getReference().child("Doctors").child("ProfileImage").child(docID+".jpg");
        }

        rootReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String medID  = Objects.requireNonNull(snapshot.child("MyProfile").child("regID").getValue()).toString();
                String dName = Objects.requireNonNull(snapshot.child("MyProfile").child("displayName").getValue()).toString();
                String fName = Objects.requireNonNull(snapshot.child("MyProfile").child("fullName").getValue()).toString();
                String spec = Objects.requireNonNull(snapshot.child("MyProfile").child("specializations").getValue()).toString();
                String phone = Objects.requireNonNull(snapshot.child("MyProfile").child("phoneNum").getValue()).toString();
                String address = Objects.requireNonNull(snapshot.child("MyProfile").child("homeAddress").getValue()).toString();
                String email = firebaseUser.getEmail();

                Objects.requireNonNull(doctorID.getEditText()).setText(medID);
                profileName.setText(dName);
                emailId.setText(email);
                Objects.requireNonNull(fullName.getEditText()).setText(fName);
                Objects.requireNonNull(specializations.getEditText()).setText(spec);
                Objects.requireNonNull(phoneNum.getEditText()).setText(phone);
                Objects.requireNonNull(homeAddress.getEditText()).setText(address);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(getApplicationContext()).load(uri.toString()).resize(400,600).centerInside().into(profileImage);
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goEdit = new Intent(getApplicationContext(),EditMyProfileDoctorActivity.class);
                startActivity(goEdit);
            }
        });
    }
}
