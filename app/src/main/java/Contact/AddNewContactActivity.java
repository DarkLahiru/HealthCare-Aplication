package Contact;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddNewContactActivity extends AppCompatActivity {

    TextInputLayout name, phoneNumber, location;
    ImageView uploadContactImage;
    CircleImageView contactImg;
    Uri imgUri;
    Button btnContactSave;
    DatabaseReference rootReference;
    FirebaseUser firebaseUser;
    FirebaseAuth mFirebaseAuth;
    StorageReference storageReference;

    boolean isImageAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_contact);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorBlack));
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        name = findViewById(R.id.txtContactFullName);
        phoneNumber = findViewById(R.id.txtContactPhoneNum);
        location = findViewById(R.id.txtContactLocation);
        btnContactSave = findViewById(R.id.btnAddContact);
        uploadContactImage = findViewById(R.id.clickContactUpload);
        contactImg = findViewById(R.id.imgContactImage);
        isImageAdd = false;

        final ContactDetails contactDetails = new ContactDetails();

        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        rootReference = FirebaseDatabase.getInstance().getReference().child("Contact Details").child(firebaseUser.getUid());
        storageReference = FirebaseStorage.getInstance().getReference("ContactImages");

        uploadContactImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 12);
            }
        });

        btnContactSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String conName = name.getEditText().getText().toString();
                String conPhone = phoneNumber.getEditText().getText().toString();
                String conLocation = location.getEditText().getText().toString();

                String key = rootReference.push().getKey();

                if (conName.isEmpty() || conPhone.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fill all the fields!!", Toast.LENGTH_SHORT).show();
                } else {

                    contactDetails.setName(conName);
                    contactDetails.setPhoneNumber(conPhone);
                    contactDetails.setAddress(conLocation);

                    if (isImageAdd) {
                        StorageReference myRef = storageReference.child(key + ".jpg");
                        myRef.putFile(imgUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Could not upload image. Try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    assert key != null;
                    rootReference.child(key).setValue(contactDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 12 && resultCode == RESULT_OK && data != null) {
            imgUri = data.getData();
            contactImg.setImageURI(imgUri);
            isImageAdd = true;

        }
    }
}

