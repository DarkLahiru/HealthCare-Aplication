package Chat;

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
import ContactDoctor.MessageActivity;
import ForDoctor.MyProfile.DoctorData;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListActivity extends AppCompatActivity {

    DatabaseReference rootReference;
    DatabaseReference rf;
    FirebaseUser firebaseUser;
    FirebaseAuth mFirebaseAuth;
    StorageReference storageReference;

    FirebaseRecyclerOptions<ChatList> options;
    FirebaseRecyclerAdapter<ChatList, UserViewHolder> adapter;

    private List<ChatList> userList;
    RecyclerView recyclerView;
    private List<DoctorData> user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Messages");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recycleViewChatList);
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
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ChatList chatList = dataSnapshot.getValue(ChatList.class);
                    userList.add(chatList);
                }
                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void chatList() {
        user = new ArrayList<>();
        rootReference = FirebaseDatabase.getInstance().getReference("Doctors");
        rootReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        DoctorData doctorData = dataSnapshot.child("MyProfile").getValue(DoctorData.class);
                        for (ChatList chatList : userList) {
                            if (doctorData.getId().equalsIgnoreCase(chatList.getId())) {
                                user.add(doctorData);
                            }
                        }
                    }
                }
                rootReference = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid());
                options = new FirebaseRecyclerOptions.Builder<ChatList>().setQuery(rootReference, ChatList.class).build();
                adapter = new FirebaseRecyclerAdapter<ChatList, UserViewHolder>(options) {
                    @NonNull
                    @Override
                    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_doctor,parent,false);
                        return new UserViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull ChatList model) {
                        rf = FirebaseDatabase.getInstance().getReference("Doctors");
                        rf.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                holder.docName.setText(snapshot.child(model.getId()).child("displayName").getValue().toString());
                                holder.docDescription.setText(snapshot.child(model.getId()).child("specializations").getValue().toString());
                                storageReference = FirebaseStorage.getInstance().getReference("Doctors").child("ProfileImage");
                                storageReference.child(model.getId() +".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Picasso.with(getApplicationContext()).load(uri.toString()).resize(400,600).centerInside().into(holder.docFace);
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
                                Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                                intent.putExtra("docID",model.getId());
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


    private class UserViewHolder extends RecyclerView.ViewHolder {

        TextView docName , docDescription;
        CircleImageView docFace;
        View mView;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            docName = (TextView)mView.findViewById(R.id.name_text);
            docDescription = (TextView)mView.findViewById(R.id.status_text);
            docFace = (CircleImageView)mView.findViewById(R.id.profile_imageFace);
        }
    }
}
