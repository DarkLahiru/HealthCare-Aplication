package Contact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewContactDetails extends AppCompatActivity {
    TextInputLayout name, phoneNumber, location;
    CircleImageView contactImg;
    DatabaseReference rootReference;
    FirebaseUser firebaseUser;
    FirebaseAuth mFirebaseAuth;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contact_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        name = findViewById(R.id.txtViewFullName);
        phoneNumber = findViewById(R.id.txtViewPhoneNum);
        location = findViewById(R.id.txtViewHomeAddress);
        contactImg = findViewById(R.id.imgViewContactImage);

        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        rootReference = FirebaseDatabase.getInstance().getReference().child("Contact Details").child(firebaseUser.getUid());


        String ContactUID = getIntent().getStringExtra("ContactItem");

        assert ContactUID != null;
        rootReference.child(ContactUID).addValueEventListener(new ValueEventListener() {
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

        storageReference = FirebaseStorage.getInstance().getReference("ContactImages").child(ContactUID + ".jpg");
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(getApplicationContext()).load(uri.toString()).resize(400,600).centerInside().into(contactImg);
            }
        });
    }
}
