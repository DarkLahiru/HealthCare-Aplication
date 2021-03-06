package ForDoctor.MyProfile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.healthcare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditMyProfileDoctorActivity extends AppCompatActivity {
    TextInputLayout docRegID,displayName, fullName, phoneNum, specializations, homeAddress;
    Button btnSave;
    ImageView clickUpload;
    Uri imageUri;
    CircleImageView uploadedImage;
    ProgressDialog progressDialog;

    DatabaseReference rootReference;

    FirebaseUser firebaseUser;
    FirebaseAuth mFirebaseAuth;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_my_profile_doctor);

        btnSave = findViewById(R.id.btnSaveDoctorEdit);
        clickUpload = findViewById(R.id.clickUploadEdit);
        uploadedImage = findViewById(R.id.uploadedImageEdit);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        docRegID = findViewById(R.id.txtDocRegID);
        displayName = findViewById(R.id.txtDisplayNameDoctorEdit);
        fullName = findViewById(R.id.txtFullNameDoctorEdit);
        phoneNum = findViewById(R.id.txtPhoneNumDoctorEdit);
        specializations = findViewById(R.id.txtSpecializationEdit);
        homeAddress = findViewById(R.id.txtHomeAddressDoctorEdit);
        final DoctorData doctorData = new DoctorData();

        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        rootReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference("Doctors").child("ProfileImage");
        progressDialog = new ProgressDialog(this);


        LoadData();



        clickUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 12);
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String docID = docRegID.getEditText().getText().toString();
                String docName= displayName.getEditText().getText().toString();
                String docFullName = fullName.getEditText().getText().toString();
                String docSP = specializations.getEditText().getText().toString();
                String phone    = phoneNum.getEditText().getText().toString();
                String address = homeAddress.getEditText().getText().toString();

                if (docName.isEmpty() || docFullName.isEmpty() || docSP.isEmpty() || phone.isEmpty() || address.isEmpty() ) {
                    Toast.makeText(getApplicationContext(), "Please fill all the fields!!", Toast.LENGTH_SHORT).show();
                }
                else {
                    doctorData.setRegID(docID);
                    doctorData.setDisplayName(docName);
                    doctorData.setFullName(docFullName);
                    doctorData.setSpecializations(docSP);
                    doctorData.setPhoneNum(phone);
                    doctorData.setHomeAddress(address);
                    doctorData.setId(firebaseUser.getUid());

                    rootReference.child("Doctors").child(firebaseUser.getUid()).child("MyProfile").setValue(doctorData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isComplete()) {
                                Toast.makeText(getApplicationContext(), "Update Data Successfully", Toast.LENGTH_SHORT).show();
                                finish();


                            }
                        }
                    });
                }

            }
        });
    }

    private void LoadData() {
        DatabaseReference rootReference;
        StorageReference storageReference;

        rootReference = FirebaseDatabase.getInstance().getReference().child("Doctors").child(firebaseUser.getUid());
        storageReference = FirebaseStorage.getInstance().getReference().child("Doctors").child("ProfileImage").child(firebaseUser.getUid()+".jpg");

        rootReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String medID  = Objects.requireNonNull(snapshot.child("MyProfile").child("regID").getValue()).toString();
                String dName = Objects.requireNonNull(snapshot.child("MyProfile").child("displayName").getValue()).toString();
                String fName = Objects.requireNonNull(snapshot.child("MyProfile").child("fullName").getValue()).toString();
                String spec = Objects.requireNonNull(snapshot.child("MyProfile").child("specializations").getValue()).toString();
                String phone = Objects.requireNonNull(snapshot.child("MyProfile").child("phoneNum").getValue()).toString();
                String address = Objects.requireNonNull(snapshot.child("MyProfile").child("homeAddress").getValue()).toString();
                //String email = Objects.requireNonNull(snapshot.child("LoginDetails").child("username").getValue()).toString();

                Objects.requireNonNull(docRegID.getEditText()).setText(medID);
                Objects.requireNonNull(displayName.getEditText()).setText(dName);
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
                Picasso.with(getApplicationContext()).load(uri.toString()).resize(400,600).centerInside().into(uploadedImage);
            }
        });
    }


    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 12 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            uploadedImage.setImageURI(imageUri);

            //upload the image to Firebase
            StorageReference myRef = storageReference.child(firebaseUser.getUid() + ".jpg");
            progressDialog.setMessage("Uploading Image");
            progressDialog.show();
            myRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Could not upload image. Try again", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
