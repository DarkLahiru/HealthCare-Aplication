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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthcare.DoctorRateActivity;
import com.example.healthcare.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.util.Arrays;
import java.util.Objects;

import Chat.ChatListActivity;
import ForDoctor.MyProfile.DoctorData;
import ForDoctor.MyProfile.MyProfileDoctorActivity;
import de.hdodenhof.circleimageview.CircleImageView;


public class CheckDoctorsActivity extends AppCompatActivity implements RatingDialogListener {

    RecyclerView recyclerView;
    FirebaseRecyclerOptions<DoctorData> options;
    FirebaseRecyclerAdapter<DoctorData, DoctorViewHolder> adapter;

    DatabaseReference rootReference;
    FirebaseUser firebaseUser;
    FirebaseAuth mFirebaseAuth;
    StorageReference storageReference;

    TextView docName, docAbout, docProfile, docMessage, docFavourite;
    CircleImageView docProfileImage;
    ImageView searchBtn;
    EditText mSearchText;

    Button message, favourite;
    AppRatingDialog.Builder appRateDocBuilder;
    String key,patientName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_doctors);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.rVResult_List);
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

        message = findViewById(R.id.btnMessageList);
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChatListActivity.class);
                startActivity(intent);
            }
        });
        favourite = findViewById(R.id.btnFavourite);
        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FavouriteList.class);
                startActivity(intent);
            }
        });
        mSearchText = findViewById(R.id.search_field);
        searchBtn = findViewById(R.id.search_btn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = mSearchText.getText().toString();
                firebaseSearch(searchText);
            }
        });

    }

    private void firebaseSearch(String searchText) {

        Toast.makeText(CheckDoctorsActivity.this, "Searching", Toast.LENGTH_LONG).show();
        Query firebaseSearchQuery = rootReference.orderByChild("displayName").startAt(searchText).endAt(searchText + "\uf8ff");
        FirebaseRecyclerOptions<DoctorData> FirebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<DoctorData>().setQuery(firebaseSearchQuery, DoctorData.class).build();
        FirebaseRecyclerAdapter<DoctorData, DoctorViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<DoctorData, DoctorViewHolder>(FirebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull DoctorViewHolder holder, int position, @NonNull DoctorData model) {
                holder.docName.setText(model.getDisplayName());
                holder.docDescription.setText(model.getSpecializations());
                key = getRef(position).getKey();
                storageReference.child(key + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(getApplicationContext()).load(uri.toString()).resize(400, 600).centerInside().into(holder.docFace);
                    }
                });

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(CheckDoctorsActivity.this, R.style.BottomSheetDialogTheme);
                        final View bottomSheetView = LayoutInflater.from(getApplicationContext())
                                .inflate(
                                        R.layout.layout_bottomsheet_doctor,
                                        (LinearLayout) findViewById(R.id.bottomSheetContainer)
                                );
                        bottomSheetDialog.setContentView(bottomSheetView);
                        bottomSheetDialog.show();

                        bottomSheetView.findViewById(R.id.txtDocProfile).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent profile = new Intent(CheckDoctorsActivity.this, MyProfileDoctorActivity.class);
                                profile.putExtra("docID", key);
                                startActivity(profile);
                            }
                        });
                        bottomSheetView.findViewById(R.id.txtSendMessage).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent message = new Intent(CheckDoctorsActivity.this, MessageActivity.class);
                                message.putExtra("docID", key);
                                startActivity(message);
                            }
                        });
                        bottomSheetView.findViewById(R.id.txtFavourite).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                DatabaseReference rReference = FirebaseDatabase.getInstance().getReference("PatientFavourites").child(firebaseUser.getUid());
                                rReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            boolean flag = false;
                                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                if (dataSnapshot.getKey().equalsIgnoreCase(key)) {
                                                    flag = true;
                                                }
                                            }
                                            if (flag) {
                                                Toast.makeText(CheckDoctorsActivity.this, "Already Added", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(CheckDoctorsActivity.this, "Added to favourite", Toast.LENGTH_SHORT).show();
                                                rReference.child(key).child("id").setValue(key);
                                            }
                                        } else {
                                            Toast.makeText(CheckDoctorsActivity.this, "Added to favourite", Toast.LENGTH_SHORT).show();
                                            rReference.child(key).child("id").setValue(key);

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });
                        bottomSheetView.findViewById(R.id.txtRateDoc).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                appRateDocBuilder = new AppRatingDialog.Builder();
                                appRateDocBuilder.setPositiveButtonText("Submit")
                                        .setNegativeButtonText("Cancel")
                                        .setNoteDescriptions(Arrays.asList("Very Bad", "Not good", "Quite ok", "Very Good", "Excellent !!!"))
                                        .setDefaultRating(2)
                                        .setTitle("Rate Your Doctor")
                                        .setDescription("Please select some stars and give your feedback")
                                        .setCommentInputEnabled(true)
                                        .setStarColor(R.color.starColor)
                                        .setNoteDescriptionTextColor(R.color.noteDescriptionTextColor)
                                        .setTitleTextColor(R.color.colorBlack)
                                        .setDescriptionTextColor(R.color.contentTextColor)
                                        .setHint("Please write your comment here ...")
                                        .setHintTextColor(R.color.hintTextColor)
                                        .setCommentTextColor(R.color.commentTextColor)
                                        .setCommentBackgroundColor(R.color.commentBackgroundColor)
                                        .setWindowAnimation(R.style.MyDialogFadeAnimation)
                                        .setCancelable(false)
                                        .setCanceledOnTouchOutside(false)
                                        .create(CheckDoctorsActivity.this)
                                        .show();

                            }
                        });
                        bottomSheetView.findViewById(R.id.txtFeedback).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent message = new Intent(CheckDoctorsActivity.this, DoctorRateActivity.class);
                                message.putExtra("docID", key);
                                startActivity(message);
                            }
                        });


                        docProfileImage = bottomSheetView.findViewById(R.id.imgDocProfile);
                        docName = bottomSheetView.findViewById(R.id.txtDocName);
                        docAbout = bottomSheetView.findViewById(R.id.txtDocStatus);
                        storageReference.child(key + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.with(getApplicationContext()).load(uri.toString()).resize(400, 600).centerInside().into(docProfileImage);
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

            @NonNull
            @Override
            public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_doctor, parent, false);
                return new DoctorViewHolder(view);
            }
        };

        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private void LoadData() {
        options = new FirebaseRecyclerOptions.Builder<DoctorData>().setQuery(rootReference, DoctorData.class).build();
        adapter = new FirebaseRecyclerAdapter<DoctorData, DoctorViewHolder>(options) {
            @NonNull
            @Override
            public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_doctor, parent, false);
                return new DoctorViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull DoctorViewHolder holder,int position, @NonNull DoctorData model) {
                holder.docName.setText(model.getDisplayName());
                holder.docDescription.setText(model.getSpecializations());
                key = getRef(position).getKey();
                storageReference.child(key + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(getApplicationContext()).load(uri.toString()).resize(400, 600).centerInside().into(holder.docFace);
                    }
                });

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(CheckDoctorsActivity.this, R.style.BottomSheetDialogTheme);
                        final View bottomSheetView = LayoutInflater.from(getApplicationContext())
                                .inflate(
                                        R.layout.layout_bottomsheet_doctor,
                                        (LinearLayout) findViewById(R.id.bottomSheetContainer)
                                );
                        bottomSheetDialog.setContentView(bottomSheetView);
                        bottomSheetDialog.show();

                        bottomSheetView.findViewById(R.id.txtDocProfile).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent profile = new Intent(CheckDoctorsActivity.this, MyProfileDoctorActivity.class);
                                profile.putExtra("docID", key);
                                startActivity(profile);
                            }
                        });
                        bottomSheetView.findViewById(R.id.txtSendMessage).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent message = new Intent(CheckDoctorsActivity.this, MessageActivity.class);
                                message.putExtra("docID", key);
                                startActivity(message);
                            }
                        });
                        bottomSheetView.findViewById(R.id.txtFavourite).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                DatabaseReference rReference = FirebaseDatabase.getInstance().getReference("PatientFavourites").child(firebaseUser.getUid());
                                rReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            boolean flag = false;
                                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                if (dataSnapshot.getKey().equalsIgnoreCase(key)) {
                                                    flag = true;
                                                }
                                            }
                                            if (flag) {
                                                Toast.makeText(CheckDoctorsActivity.this, "Already Added", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(CheckDoctorsActivity.this, "Added to favourite", Toast.LENGTH_SHORT).show();
                                                rReference.child(key).child("id").setValue(key);
                                            }
                                        } else {
                                            Toast.makeText(CheckDoctorsActivity.this, "Added to favourite", Toast.LENGTH_SHORT).show();
                                            rReference.child(key).child("id").setValue(key);

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });
                        bottomSheetView.findViewById(R.id.txtRateDoc).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                appRateDocBuilder = new AppRatingDialog.Builder();
                                appRateDocBuilder.setPositiveButtonText("Submit")
                                        .setNegativeButtonText("Cancel")
                                        .setNoteDescriptions(Arrays.asList("Very Bad", "Not good", "Quite ok", "Very Good", "Excellent !!!"))
                                        .setDefaultRating(2)
                                        .setTitle("Rate Your Doctor")
                                        .setDescription("Please select some stars and give your feedback")
                                        .setCommentInputEnabled(true)
                                        .setStarColor(R.color.starColor)
                                        .setNoteDescriptionTextColor(R.color.noteDescriptionTextColor)
                                        .setTitleTextColor(R.color.colorBlack)
                                        .setDescriptionTextColor(R.color.contentTextColor)
                                        .setHint("Please write your comment here ...")
                                        .setHintTextColor(R.color.hintTextColor)
                                        .setCommentTextColor(R.color.commentTextColor)
                                        .setCommentBackgroundColor(R.color.commentBackgroundColor)
                                        .setWindowAnimation(R.style.MyDialogFadeAnimation)
                                        .setCancelable(false)
                                        .setCanceledOnTouchOutside(false)
                                        .create(CheckDoctorsActivity.this)
                                        .show();

                            }
                        });
                        bottomSheetView.findViewById(R.id.txtFeedback).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent message = new Intent(CheckDoctorsActivity.this, DoctorRateActivity.class);
                                message.putExtra("docID", key);
                                startActivity(message);
                            }
                        });


                        docProfileImage = bottomSheetView.findViewById(R.id.imgDocProfile);
                        docName = bottomSheetView.findViewById(R.id.txtDocName);
                        docAbout = bottomSheetView.findViewById(R.id.txtDocStatus);
                        storageReference.child(key + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.with(getApplicationContext()).load(uri.toString()).resize(400, 600).centerInside().into(docProfileImage);
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

    @Override
    public void onNegativeButtonClicked() {
    }

    @Override
    public void onNeutralButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(int i, String s) {

        DoctorRate rate = new DoctorRate();
        rate.setStars(String.valueOf(i));
        rate.setFeedback(s);
        rate.setPatientID(firebaseUser.getUid());
        rate.setDoctorID(key);

        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("DoctorRate").child(key).child(firebaseUser.getUid());
        database.setValue(rate).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(),"Thank you for your feedback",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"There is a problem "+e.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });

    }


    public static class DoctorViewHolder extends RecyclerView.ViewHolder {

        TextView docName, docDescription;
        CircleImageView docFace;

        View mView;

        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            docName = (TextView) mView.findViewById(R.id.name_text);
            docDescription = (TextView) mView.findViewById(R.id.status_text);
            docFace = (CircleImageView) mView.findViewById(R.id.profile_imageFace);
        }
    }
}
