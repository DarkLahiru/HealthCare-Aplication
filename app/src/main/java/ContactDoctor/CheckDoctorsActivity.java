package ContactDoctor;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthcare.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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

import ForDoctor.MyProfile.DoctorData;
import ForDoctor.MyProfile.MyProfileDoctorActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import medicine.ListData;


public class CheckDoctorsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseRecyclerOptions<DoctorData> options;
    FirebaseRecyclerAdapter<DoctorData, DoctorViewHolder> adapter;

    DatabaseReference rootReference;
    FirebaseUser firebaseUser;
    FirebaseAuth mFirebaseAuth;
    StorageReference storageReference;

    TextView docName,docAbout,docProfile,docMessage,docFavourite;
    CircleImageView docProfileImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_doctors);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView)findViewById(R.id.rVResult_List);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        rootReference = FirebaseDatabase.getInstance().getReference("Doctors");
        storageReference = FirebaseStorage.getInstance().getReference("Doctors").child("ProfileImage");


        docProfile = findViewById(R.id.txtDocProfile);
        docMessage = findViewById(R.id.txtSendMessage);
        docFavourite = findViewById(R.id.txtFavourite);

        LoadData();
    }


    private void LoadData() {


        options = new FirebaseRecyclerOptions.Builder<DoctorData>().setQuery(rootReference,DoctorData.class).build();
        adapter = new FirebaseRecyclerAdapter<DoctorData, DoctorViewHolder>(options) {

            @NonNull
            @Override
            public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_doctor,parent,false);
                return new DoctorViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final DoctorViewHolder holder, final int position, @NonNull DoctorData model) {
                holder.docName.setText(model.getDisplayName());
                holder.docDescription.setText(model.getSpecializations());
                final String key = getRef(position).getKey();
                storageReference.child(key +".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(getApplicationContext()).load(uri.toString()).resize(400,600).centerInside().into(holder.docFace);
                    }
                });

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BottomSheetDialog bottomSheetDialog =  new BottomSheetDialog(CheckDoctorsActivity.this,R.style.BottomSheetDialogTheme);
                        final View bottomSheetView  = LayoutInflater.from(getApplicationContext())
                                .inflate(
                                        R.layout.layout_bottomsheet_doctor,
                                        (LinearLayout)findViewById(R.id.bottomSheetContainer)
                                );
                        bottomSheetDialog.setContentView(bottomSheetView);
                        bottomSheetDialog.show();

                        bottomSheetView.findViewById(R.id.txtDocProfile).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent profile = new Intent(CheckDoctorsActivity.this, MyProfileDoctorActivity.class);
                                profile.putExtra("docID",key);
                                startActivity(profile);
                            }
                        });
                        bottomSheetView.findViewById(R.id.txtSendMessage).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                               Intent message = new Intent(CheckDoctorsActivity.this,MessageActivity.class);
                               message.putExtra("docID",key);
                               startActivity(message);
                            }
                        });
                        bottomSheetView.findViewById(R.id.txtFavourite).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(CheckDoctorsActivity.this, "Login Error, You are not a Doctor !!!", Toast.LENGTH_SHORT).show();
                            }
                        });

                        docProfileImage = bottomSheetView.findViewById(R.id.imgDocProfile);
                        docName = bottomSheetView.findViewById(R.id.txtDocName);
                        docAbout = bottomSheetView.findViewById(R.id.txtDocStatus);
                        storageReference.child(key +".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.with(getApplicationContext()).load(uri.toString()).resize(400,600).centerInside().into(docProfileImage);
                            }
                        });

                        assert key != null;
                        rootReference.child(key).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                //Toast.makeText(CheckDoctorsActivity.this, "test", Toast.LENGTH_SHORT).show();
                                String name = snapshot.child("MyProfile").child("displayName").getValue().toString();
                                String about = snapshot.child("MyProfile").child("specializations").getValue().toString();

                                docName.setText(name);
                                docAbout.setText(about);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }
                });
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    public static class DoctorViewHolder extends RecyclerView.ViewHolder{

        TextView docName , docDescription;
        CircleImageView docFace;

        View mView;
        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            docName = (TextView)mView.findViewById(R.id.name_text);
            docDescription = (TextView)mView.findViewById(R.id.status_text);
            docFace = (CircleImageView)mView.findViewById(R.id.profile_imageFace);
        }
    }
}
