package ContactDoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthcare.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {


    DatabaseReference rootReference;
    FirebaseUser firebaseUser;
    FirebaseAuth mFirebaseAuth;
    StorageReference storageReference;

    TextView docName;
    CircleImageView docProfileImage;
    String docID;

    RecyclerView recyclerView;
    EditText sendMsg;
    FloatingActionButton send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.toolbarMessage);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        docProfileImage= findViewById(R.id.messengerImage);
        docName =findViewById(R.id.txtMessengerName);

        docID = getIntent().getStringExtra("docID");

        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        rootReference = FirebaseDatabase.getInstance().getReference("Doctors");
        storageReference = FirebaseStorage.getInstance().getReference("Doctors").child("ProfileImage");

        LoadData();

        recyclerView = findViewById(R.id.rv_chat);
        sendMsg = findViewById(R.id.et_message_chat);
        send = findViewById(R.id.fab_chat);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg  = sendMsg.getText().toString();

                if (!msg.equals("")){
                    sendMessage(firebaseUser.getUid(),docID,msg);
                }
                else {
                    Toast.makeText(MessageActivity.this, "Please Enter Message", Toast.LENGTH_SHORT);
                }
                sendMsg.setText("");
            }
        });




    }

    private void sendMessage(String patient, String doctor, String message) {
        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        rootReference = FirebaseDatabase.getInstance().getReference();

        HashMap<String,Object>hashMap = new HashMap<>();
        hashMap.put("Patient",patient);
        hashMap.put("Doctor",doctor);
        hashMap.put("Message",message);

        rootReference.child("Chats").push().setValue(hashMap);
    }

    private void LoadData() {

        storageReference.child(docID +".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(getApplicationContext()).load(uri.toString()).resize(400,600).centerInside().into(docProfileImage);
            }
        });

        rootReference.child(docID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name  = snapshot.child("displayName").getValue().toString();
                docName.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
