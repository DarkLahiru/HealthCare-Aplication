package ForDoctor.Messages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.healthcare.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.List;
import java.util.Objects;

import Chat.model.ChatList;
import de.hdodenhof.circleimageview.CircleImageView;
import Profile.User;

public class ChatListDoctorActivity extends AppCompatActivity {
    DatabaseReference rootReference;
    DatabaseReference rf;
    FirebaseUser firebaseUser;
    FirebaseAuth mFirebaseAuth;
    StorageReference storageReference;

    FirebaseRecyclerOptions<ChatList> options;
    FirebaseRecyclerAdapter<ChatList, ViewHolder> adapter;

    private List<ChatList> userList;
    RecyclerView recyclerView;
    private List<User> user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list_doctor);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Messages");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recycleViewMessageList);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();

        userList = new ArrayList<>();

        rootReference = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid());
        rootReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        ChatList chatList = dataSnapshot.getValue(ChatList.class);
                        userList.add(chatList);
                    }
                    chatList();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void chatList() {
        user = new ArrayList<>();
        rootReference = FirebaseDatabase.getInstance().getReference("Patients");
        rootReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User users = dataSnapshot.child("MyProfile").getValue(User.class);
                        for (ChatList chatList : userList) {
                            if (!userList.isEmpty()) {
                                if (users.getId().equalsIgnoreCase(chatList.getId())) {
                                    user.add(users);
                                }
                            }

                        }
                    }
                }
                rootReference = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid());
                options = new FirebaseRecyclerOptions.Builder<ChatList>().setQuery(rootReference, ChatList.class).build();
                adapter = new FirebaseRecyclerAdapter<ChatList, ViewHolder>(options) {
                    @NonNull
                    @Override
                    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_doctor, parent, false);
                        return new ViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull ChatList model) {
                        rf = FirebaseDatabase.getInstance().getReference("Patients");
                        rf.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                holder.patientName.setText(snapshot.child(model.getId()).child("MyProfile").child("displayName").getValue().toString());
                                holder.patientPhone.setText(snapshot.child(model.getId()).child("MyProfile").child("phoneNum").getValue().toString());

                                storageReference = FirebaseStorage.getInstance().getReference("Patients").child("ProfileImage");
                                storageReference.child(model.getId() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Picasso.with(getApplicationContext()).load(uri.toString()).resize(400, 600).centerInside().into(holder.patientFace);
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getApplicationContext(), DocMessagesActivity.class);
                                intent.putExtra("PatientID", model.getId());
                                startActivity(intent);
                            }
                        });
                    }
                };
                adapter.startListening();
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView patientName, patientPhone;
        CircleImageView patientFace;
        View mView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            patientName = (TextView) mView.findViewById(R.id.name_text);
            patientPhone = (TextView) mView.findViewById(R.id.status_text);
            patientFace = (CircleImageView) mView.findViewById(R.id.profile_imageFace);
        }
    }
}
