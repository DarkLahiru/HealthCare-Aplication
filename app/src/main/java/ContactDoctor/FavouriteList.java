package ContactDoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import Chat.ChatListActivity;
import Chat.model.ChatList;
import ForDoctor.MyProfile.DoctorData;
import ForDoctor.MyProfile.MyProfileDoctorActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class FavouriteList extends AppCompatActivity implements RatingDialogListener {

    DatabaseReference rootReference;
    DatabaseReference rf;
    FirebaseUser firebaseUser;
    FirebaseAuth mFirebaseAuth;
    StorageReference storageReference;

    FirebaseRecyclerOptions<ChatList> options;
    FirebaseRecyclerAdapter<ChatList, UserViewHolder> adapter;

    RecyclerView recyclerView;
    AppRatingDialog.Builder appRateDocBuilder;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_list);


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Favourites");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recycleViewFavList);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        LoadData();

    }

    private void LoadData() {

        rootReference = FirebaseDatabase.getInstance().getReference("PatientFavourites").child(firebaseUser.getUid());
        options = new FirebaseRecyclerOptions.Builder<ChatList>().setQuery(rootReference, ChatList.class).build();
        adapter = new FirebaseRecyclerAdapter<ChatList, UserViewHolder>(options) {
            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_doctor, parent, false);
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
                        storageReference.child(model.getId() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.with(getApplicationContext()).load(uri.toString()).resize(400, 600).centerInside().into(holder.docFace);
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
                        BottomSheetDialog bottomSheetDialog =  new BottomSheetDialog(FavouriteList.this,R.style.BottomSheetDialogTheme);
                        final View bottomSheetView  = LayoutInflater.from(getApplicationContext())
                                .inflate(
                                        R.layout.layout_bottomsheet_doctor,
                                        (LinearLayout)findViewById(R.id.bottomSheetContainer)
                                );
                        TextView fav = bottomSheetView.findViewById(R.id.txtFavourite);
                        ImageView unFav = bottomSheetView.findViewById(R.id.imgFav);
                        unFav.setImageResource(R.drawable.ic_unfavorite);
                        fav.setText(R.string.remove_favourite);
                        bottomSheetDialog.setContentView(bottomSheetView);
                        bottomSheetDialog.show();

                        bottomSheetView.findViewById(R.id.txtDocProfile).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent profile = new Intent(FavouriteList.this, MyProfileDoctorActivity.class);
                                profile.putExtra("docID",model.getId());
                                startActivity(profile);
                            }
                        });
                        bottomSheetView.findViewById(R.id.txtSendMessage).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent message = new Intent(FavouriteList.this,MessageActivity.class);
                                message.putExtra("docID",model.getId());
                                startActivity(message);
                            }
                        });
                        fav.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                rootReference.child(model.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(FavouriteList.this, "Removed from favourites", Toast.LENGTH_SHORT).show();
                                        finish();
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
                                        .create(FavouriteList.this)
                                        .show();

                                key = model.getId();
                            }
                        });
                        bottomSheetView.findViewById(R.id.txtFeedback).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent message = new Intent(FavouriteList.this, DoctorRateActivity.class);
                                message.putExtra("docID", model.getId());
                                startActivity(message);
                            }
                        });

                        ImageView docProfileImage = bottomSheetView.findViewById(R.id.imgDocProfile);
                        TextView docName = bottomSheetView.findViewById(R.id.txtDocName);
                        TextView docAbout = bottomSheetView.findViewById(R.id.txtDocStatus);
                        storageReference.child(model.getId() +".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.with(getApplicationContext()).load(uri.toString()).resize(400,600).centerInside().into(docProfileImage);
                            }
                        });

                        rf.child(model.getId()).addValueEventListener(new ValueEventListener() {
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

    private static class UserViewHolder extends RecyclerView.ViewHolder {

        TextView docName, docDescription;
        CircleImageView docFace;
        View mView;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            docName = (TextView) mView.findViewById(R.id.name_text);
            docDescription = (TextView) mView.findViewById(R.id.status_text);
            docFace = (CircleImageView) mView.findViewById(R.id.profile_imageFace);
        }
    }


}