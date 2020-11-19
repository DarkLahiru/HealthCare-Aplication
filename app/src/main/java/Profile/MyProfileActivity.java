package Profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfileActivity extends AppCompatActivity {
    TextInputLayout fullName, birthDay, phoneNum, height, weight, homeAddress,bmi;
    CircleImageView profileImage;
    TextView profileName,emailId;

    Button btnUpdate;

    DatabaseReference rootReference;
    FirebaseUser firebaseUser;
    FirebaseAuth mFirebaseAuth;
    StorageReference storageReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        btnUpdate = findViewById(R.id.btnUpdate);
        profileName = findViewById(R.id.profile_name);
        fullName = findViewById(R.id.txtFullNameUpdated);
        birthDay = findViewById(R.id.txtBirthDayUpdated);
        phoneNum = findViewById(R.id.txtPhoneNumUpdated);
        height = findViewById(R.id.txtHeightUpdated);
        weight = findViewById(R.id.txtWeightUpdated);
        homeAddress = findViewById(R.id.txtHomeAddressUpdated);
        bmi = findViewById(R.id.txtBmiUpdated);
        profileImage = findViewById(R.id.profile_image);
        emailId = findViewById(R.id.emailAddress);

        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();


        rootReference = FirebaseDatabase.getInstance().getReference("Patients").child(firebaseUser.getUid());
        rootReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String dName = Objects.requireNonNull(dataSnapshot.child("MyProfile").child("displayName").getValue()).toString();
                String fName = Objects.requireNonNull(dataSnapshot.child("MyProfile").child("fullName").getValue()).toString();
                String bod = Objects.requireNonNull(dataSnapshot.child("MyProfile").child("birthDay").getValue()).toString();
                String phone = Objects.requireNonNull(dataSnapshot.child("MyProfile").child("phoneNum").getValue()).toString();
                String h = Objects.requireNonNull(dataSnapshot.child("MyProfile").child("height").getValue()).toString();
                String w = Objects.requireNonNull(dataSnapshot.child("MyProfile").child("weight").getValue()).toString();
                String address = Objects.requireNonNull(dataSnapshot.child("MyProfile").child("homeAddress").getValue()).toString();
                String email = Objects.requireNonNull(dataSnapshot.child("LoginDetails").child("username").getValue()).toString();


                profileName.setText(dName);
                emailId.setText(email);
                Objects.requireNonNull(fullName.getEditText()).setText(fName);
                Objects.requireNonNull(birthDay.getEditText()).setText(bod);
                Objects.requireNonNull(phoneNum.getEditText()).setText(phone);
                Objects.requireNonNull(height.getEditText()).setText(h);
                Objects.requireNonNull(weight.getEditText()).setText(w);
                Objects.requireNonNull(homeAddress.getEditText()).setText(address);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        storageReference = FirebaseStorage.getInstance().getReference("Patients").child("ProfileImage").child(firebaseUser.getUid() + ".jpg");
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(getApplicationContext()).load(uri.toString()).resize(400,600).centerInside().into(profileImage);
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goEdit = new Intent(MyProfileActivity.this,EditMyProfileActivity.class);
                startActivity(goEdit);
            }
        });

    }


}
