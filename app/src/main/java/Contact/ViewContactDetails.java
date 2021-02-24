package Contact;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.healthcare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
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
import com.shreyaspatil.MaterialDialog.MaterialDialog;
import com.shreyaspatil.MaterialDialog.interfaces.DialogInterface;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import Appointments.Pending.PendingAppointmentActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class ViewContactDetails extends AppCompatActivity {
    TextInputLayout name, phoneNumber, location;
    CircleImageView contactImg;
    DatabaseReference rootReference;
    FirebaseUser firebaseUser;
    FirebaseAuth mFirebaseAuth;
    StorageReference storageReference;
    ExtendedFloatingActionButton fabEdit,fabDone,fabDelete;
    ImageView uploadContactImage;
    CardView cardView;
    MaterialDialog mDialog;
    Uri imgUri;
    boolean isImageAdd;
    String contactUID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contact_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorBlack));
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        contactUID = getIntent().getStringExtra("ContactItem");

        name = findViewById(R.id.txtViewFullName);
        phoneNumber = findViewById(R.id.txtViewPhoneNum);
        location = findViewById(R.id.txtViewHomeAddress);
        contactImg = findViewById(R.id.imgViewContactImage);
        fabEdit = findViewById(R.id.extended_fab);
        fabDone= findViewById(R.id.extended_fab_done);
        fabDelete = findViewById(R.id.extended_fab_delete);
        uploadContactImage = findViewById(R.id.clickContactUpload);

        cardView  = findViewById(R.id.crdViewUpload);

        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        rootReference = FirebaseDatabase.getInstance().getReference().child("Contact Details").child(firebaseUser.getUid());
        storageReference = FirebaseStorage.getInstance().getReference("ContactImages").child(contactUID + ".jpg");


        assert contactUID != null;
        rootReference.child(contactUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String viewName = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                    String viewPhone = Objects.requireNonNull(snapshot.child("phoneNumber").getValue()).toString();
                    String viewLocation = Objects.requireNonNull(snapshot.child("address").getValue()).toString();

                    name.getEditText().setText(viewName);
                    phoneNumber.getEditText().setText(viewPhone);
                    location.getEditText().setText(viewLocation);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(getApplicationContext()).load(uri.toString()).resize(400,600).centerInside().into(contactImg);
            }
        });

        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editContact();
            }
        });

        fabDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadData();
                name.getEditText().setFocusableInTouchMode(false);
                phoneNumber.getEditText().setFocusableInTouchMode(false);
                location.getEditText().setFocusableInTouchMode(false);
                cardView.setVisibility(View.GONE);
                fabEdit.setVisibility(View.VISIBLE);
                fabDone.setVisibility(View.GONE);
                fabDelete.setVisibility(View.VISIBLE);
            }
        });

        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFile();

            }
        });


    }



    private void uploadData() {
        rootReference = FirebaseDatabase.getInstance().getReference().child("Contact Details").child(firebaseUser.getUid()).child(contactUID);
        storageReference = FirebaseStorage.getInstance().getReference("ContactImages").child(contactUID + ".jpg");

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 12);
            }
        });

        ContactDetails contactDetails  = new ContactDetails();
        String conName = name.getEditText().getText().toString();
        String conPhone = phoneNumber.getEditText().getText().toString();
        String conLocation = location.getEditText().getText().toString();

        if (conName.isEmpty() || conPhone.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please fill all the fields!!", Toast.LENGTH_SHORT).show();
        }
        else {
            contactDetails.setName(conName);
            contactDetails.setPhoneNumber(conPhone);
            contactDetails.setAddress(conLocation);

            if (isImageAdd) {
                storageReference.putFile(imgUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Could not upload image. Try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            rootReference.setValue(contactDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(), "Update Data Successfully", Toast.LENGTH_SHORT).show();
                   /* finish();
                    overridePendingTransition(0, 0);
                    startActivity(getIntent());
                    overridePendingTransition(0, 0);*/
                }
            });
        }

    }

    @SuppressLint("SetTextI18n")
    private void editContact() {
        name.getEditText().setFocusableInTouchMode(true);
        phoneNumber.getEditText().setFocusableInTouchMode(true);
        location.getEditText().setFocusableInTouchMode(true);
        cardView.setVisibility(View.VISIBLE);
        fabEdit.setVisibility(View.GONE);
        fabDone.setVisibility(View.VISIBLE);
        fabDelete.setVisibility(View.INVISIBLE);

        uploadContactImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 12);
            }
        });


    }
    private void deleteFile() {
        mDialog = new MaterialDialog.Builder(ViewContactDetails.this)
                .setTitle("Delete ?")
                .setMessage("Are you sure want to delete this contact")
                .setCancelable(false)
                .setPositiveButton("Yes", R.drawable.ic_delete, new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        // Delete Operation

                        rootReference.child(contactUID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getApplicationContext(), "Delete contact Successfully", Toast.LENGTH_SHORT).show();
                                dialogInterface.dismiss();
                                startActivity(new Intent(ViewContactDetails.this, ContactActivity.class));


                            }
                        });
                    }
                })
                .setNegativeButton("No", R.drawable.ic_close, new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                })
                .build();
        mDialog.show();

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
