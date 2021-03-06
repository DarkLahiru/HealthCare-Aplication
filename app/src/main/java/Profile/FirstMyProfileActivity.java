package Profile;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.healthcare.NavigationActivity;
import com.example.healthcare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import ForDoctor.MyProfile.FirstDoctorProfileActivity;
import Reminder.AddReminderActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class FirstMyProfileActivity extends AppCompatActivity {
    TextInputLayout displayName, fullName, birthDay, phoneNum, height, weight, homeAddress;
    Button btnSave;
    ImageView clickUpload;
    Uri imageUri;
    CircleImageView uploadedImage;
    ProgressDialog progressDialog;
    TextInputEditText eTBirthDay;


    DatabaseReference rootReference;
    FirebaseUser firebaseUser;
    FirebaseAuth mFirebaseAuth;
    StorageReference storageReference;

    boolean imgLoad = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_my_profile);
        btnSave = findViewById(R.id.btnFirstSave);
        clickUpload = findViewById(R.id.clickFUpload);
        uploadedImage = findViewById(R.id.uploadedFImage);
        eTBirthDay = findViewById(R.id.eTBirthDay);
        displayName = findViewById(R.id.txtFDisplayName);
        fullName = findViewById(R.id.txtFFullName);
        birthDay = findViewById(R.id.txtFBirthDay);
        phoneNum = findViewById(R.id.txtFPhoneNum);
        height = findViewById(R.id.txtFHeight);
        weight = findViewById(R.id.txtFWeight);
        homeAddress = findViewById(R.id.txtFHomeAddress);


        mFirebaseAuth = FirebaseAuth.getInstance();
        rootReference = FirebaseDatabase.getInstance().getReference();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("Patients").child("ProfileImage");

        progressDialog = new ProgressDialog(this);


        clickUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 12);
            }
        });
        eTBirthDay.setEnabled(true);
        eTBirthDay.setTextIsSelectable(true);
        eTBirthDay.setFocusable(false);
        eTBirthDay.setFocusableInTouchMode(false);

        eTBirthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mCurrentDate = Calendar.getInstance();
                int year = mCurrentDate.get(Calendar.YEAR);
                int month = mCurrentDate.get(Calendar.MONTH);
                int day = mCurrentDate.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker;
                datePicker = new DatePickerDialog(FirstMyProfileActivity.this, new DatePickerDialog.OnDateSetListener() {

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String strMonth = "", strDay = "";
                        month++;
                        if (month < 10) {
                            strMonth = "0" + month;
                        } else {
                            strMonth = "" + month;
                        }
                        if (dayOfMonth < 10) {
                            strDay = "0" + dayOfMonth;
                        } else {
                            strDay = "" + dayOfMonth;
                        }
                        Objects.requireNonNull(birthDay.getEditText()).setText(strMonth + "/" + strDay + "/" + year);

                    }
                }, year, month, day);
                datePicker.getDatePicker().setMaxDate(new Date().getTime());
                datePicker.show();
            }
        });


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (imgLoad) {
                    String name = Objects.requireNonNull(displayName.getEditText()).getText().toString();
                    String fName = Objects.requireNonNull(fullName.getEditText()).getText().toString();
                    String bod = Objects.requireNonNull(birthDay.getEditText()).getText().toString();
                    String phone = Objects.requireNonNull(phoneNum.getEditText()).getText().toString();
                    String heightValue = Objects.requireNonNull(height.getEditText()).getText().toString();
                    String weightValue = Objects.requireNonNull(weight.getEditText()).getText().toString();
                    String address = Objects.requireNonNull(homeAddress.getEditText()).getText().toString();

                    if (name.isEmpty() || fName.isEmpty() || bod.isEmpty() || phone.isEmpty() || heightValue.isEmpty() || weightValue.isEmpty() || address.isEmpty()) {
                        Toast.makeText(FirstMyProfileActivity.this, "Please fill all the fields!!", Toast.LENGTH_SHORT).show();
                    } else {


                        User myDetails = new User(name, fName, bod, phone, heightValue, weightValue, address);
                        rootReference.child("Patients").child(firebaseUser.getUid()).child("MyProfile").setValue(myDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isComplete()) {
                                    rootReference.child("Users").child(firebaseUser.getUid()).child("First Time Login").setValue("true");
                                    Toast.makeText(FirstMyProfileActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                                    finish();
                                    Intent myIntent = new Intent(getApplicationContext(), NavigationActivity.class);
                                    startActivity(myIntent);

                                }
                            }
                        });
                    }
                }
                else  Toast.makeText(FirstMyProfileActivity.this, "Please add a image of you's", Toast.LENGTH_SHORT).show();

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
                        imgLoad = true;
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
