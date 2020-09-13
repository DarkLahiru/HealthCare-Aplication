package ForDoctor.MyProfile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import ForDoctor.MyProfile.DoctorData;
import ForDoctor.NavigationDoctor;
import de.hdodenhof.circleimageview.CircleImageView;

public class FirstDoctorProfileActivity extends AppCompatActivity {

    TextInputLayout displayName, fullName, phoneNum, specializations, homeAddress;
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
        setContentView(R.layout.activity_first_doctor_profile_activitiy);

        btnSave = findViewById(R.id.btnFirstSaveDoctor);
        clickUpload = findViewById(R.id.clickFUploadDoctor);
        uploadedImage = findViewById(R.id.uploadedFImageDoctor);

        displayName = findViewById(R.id.txtFDisplayNameDoctor);
        fullName = findViewById(R.id.txtFFullNameDoctor);
        phoneNum = findViewById(R.id.txtFPhoneNumDoctor);
        specializations = findViewById(R.id.txtSpecializations);
        homeAddress = findViewById(R.id.txtFHomeAddressDoctor);
        final DoctorData doctorData = new DoctorData();

        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        rootReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference("Doctors").child("ProfileImage");
        progressDialog = new ProgressDialog(this);


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
                String docName= displayName.getEditText().getText().toString();
                String docFullName = fullName.getEditText().getText().toString();
                String docSP = specializations.getEditText().getText().toString();
                String phone    = phoneNum.getEditText().getText().toString();
                String address = homeAddress.getEditText().getText().toString();

                if (docName.isEmpty() || docFullName.isEmpty() || docSP.isEmpty() || phone.isEmpty() || address.isEmpty() ) {
                    Toast.makeText(getApplicationContext(), "Please fill all the fields!!", Toast.LENGTH_SHORT).show();
                }
                else {

                    doctorData.setDisplayName(docName);
                    doctorData.setFullName(docFullName);
                    doctorData.setSpecializations(docSP);
                    doctorData.setPhoneNum(phone);
                    doctorData.setHomeAddress(address);
                    doctorData.setId(firebaseUser.getUid());

                    rootReference.child("Doctors").child(firebaseUser.getUid()).child("displayName").setValue(docName);
                    rootReference.child("Doctors").child(firebaseUser.getUid()).child("specializations").setValue(docSP);
                    //rootReference.child("Doctors").child(firebaseUser.getUid()).child("id").setValue(firebaseUser.getUid());
                    rootReference.child("Doctors").child(firebaseUser.getUid()).child("profileImage").setValue(firebaseUser.getUid()+".jpg");

                    rootReference.child("Doctors").child(firebaseUser.getUid()).child("MyProfile").setValue(doctorData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isComplete()) {

                                Toast.makeText(getApplicationContext(), "Update Data Successfully", Toast.LENGTH_SHORT).show();
                                finish();
                                Intent myIntent = new Intent(getApplicationContext(), NavigationDoctor.class);
                                startActivity(myIntent);

                            }
                        }
                    });
                }

            }
        });
    }


    @Override
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
