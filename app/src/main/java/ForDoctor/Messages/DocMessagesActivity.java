package ForDoctor.Messages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Chat.Adapter.MessageAdapter;
import Chat.model.Chats;
import de.hdodenhof.circleimageview.CircleImageView;

public class DocMessagesActivity extends AppCompatActivity {

    DatabaseReference rootReference;
    FirebaseUser firebaseUser;
    FirebaseAuth mFirebaseAuth;
    StorageReference storageReference;

    TextView patientName;
    CircleImageView patientProfileImage;
    String patientID;

    RecyclerView recyclerView;
    EditText sendMsg;
    FloatingActionButton send;

    MessageAdapter messageAdapter;
    List<Chats> mChats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.toolbarMessage);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        patientProfileImage = findViewById(R.id.messengerImage);
        patientName = findViewById(R.id.txtMessengerName);
        patientID = getIntent().getStringExtra("patientID");

        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        rootReference = FirebaseDatabase.getInstance().getReference("Patients");
        storageReference = FirebaseStorage.getInstance().getReference("Patients").child("ProfileImage");

        LoadData();

        recyclerView = findViewById(R.id.rv_chat);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        sendMsg = findViewById(R.id.et_message_chat);
        send = findViewById(R.id.fab_chat);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg  = sendMsg.getText().toString();

                if (!msg.equals("")){
                    sendMessage(firebaseUser.getUid(),patientID,msg);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please Enter Message", Toast.LENGTH_SHORT).show();
                }
                sendMsg.setText("");
            }
        });
    }

    private void sendMessage(String sender, String receiver, String message) {
        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        rootReference = FirebaseDatabase.getInstance().getReference();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
        rootReference.child("Chats").push().setValue(hashMap);
    }


    private void readMessage(String sender, String receiver) {
        mChats = new ArrayList<>();
        rootReference = FirebaseDatabase.getInstance().getReference("Chats");
        rootReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChats.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Chats chats = dataSnapshot.getValue(Chats.class);
                    assert chats != null;
                    if (chats.getSender().equals(sender) && chats.getReceiver().equals(receiver) || chats.getSender().equals(receiver) && chats.getReceiver().equals(sender)) {
                        mChats.add(chats);
                    }
                    messageAdapter = new MessageAdapter(DocMessagesActivity.this, mChats, receiver,"Doctors");
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void LoadData() {

        storageReference.child(patientID +".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(getApplicationContext()).load(uri.toString()).resize(400,600).centerInside().into(patientProfileImage);
            }
        });

        rootReference.child(patientID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name  = snapshot.child("MyProfile").child("displayName").getValue().toString();
                patientName.setText(name);
                readMessage(firebaseUser.getUid(),patientID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
