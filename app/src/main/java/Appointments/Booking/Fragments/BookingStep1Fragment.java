package Appointments.Booking.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.healthcare.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import Appointments.Booking.BookAppointmentActivity;
import Appointments.Booking.Interface.IRecyclerItemSelectedListener;
import Common.Common;

import ForDoctor.MyProfile.DoctorData;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

public class BookingStep1Fragment extends Fragment {

    FirebaseRecyclerOptions<DoctorData> options;
    FirebaseRecyclerAdapter<DoctorData, DoctorViewHolder> adapter;
    DatabaseReference rootReference;
    FirebaseUser firebaseUser;
    FirebaseAuth mFirebaseAuth;
    StorageReference storageReference;

    List<CardView> cardViewList;
    List<DoctorData> doctorDataList;

    static BookingStep1Fragment instance;
    Context context;

    public static BookingStep1Fragment getInstance() {
        if (instance == null)
            instance = new BookingStep1Fragment();
        return instance;
    }

    @BindView(R.id.rVResult_List_Step_One)
    RecyclerView recyclerView;
    @BindView(R.id.search_field)
    EditText searchField;
    @BindView(R.id.search_btn)
    ImageView searchBtn;
    Unbinder unbinder;

    LocalBroadcastManager localBroadcastManager;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        rootReference = FirebaseDatabase.getInstance().getReference("Doctors");
        storageReference = FirebaseStorage.getInstance().getReference("Doctors").child("ProfileImage");

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View itemView = inflater.inflate(R.layout.fragment_booking_step_one, container, false);
        unbinder = ButterKnife.bind(this, itemView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = searchField.getText().toString();
                firebaseSearch(searchText);
            }
        });
        loadDoctor();

        return itemView;
    }

    private void firebaseSearch(String searchText) {
        cardViewList = new ArrayList<>();
        doctorDataList = new ArrayList<>();
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        Toast.makeText(getContext(), "Searching", Toast.LENGTH_LONG).show();
        Query firebaseSearchQuery = rootReference.orderByChild("displayName").startAt(searchText).endAt(searchText + "\uf8ff");
        FirebaseRecyclerOptions<DoctorData> FirebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<DoctorData>().setQuery(firebaseSearchQuery, DoctorData.class).build();
        FirebaseRecyclerAdapter<DoctorData, DoctorViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<DoctorData, DoctorViewHolder>(FirebaseRecyclerOptions) {

            @NonNull
            @Override
            public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_doctor, parent, false);
                return new DoctorViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull DoctorViewHolder holder, int position, @NonNull DoctorData model) {
                holder.docName.setText(model.getDisplayName());
                holder.docDescription.setText(model.getSpecializations());
                final String key = getRef(position).getKey();

                if (!cardViewList.contains(holder.cardDoctor))
                    cardViewList.add(holder.cardDoctor);

                holder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
                    @Override
                    public void onItemSelectedListener(View view, int pos) {
                        //for not selected card background
                        for (CardView cardView : cardViewList)
                            cardView.setCardBackgroundColor(getContext().getResources().getColor(android.R.color.white));

                        //set selected card background
                        holder.cardDoctor.setCardBackgroundColor(getContext().getResources().getColor(android.R.color.holo_orange_dark));


                        Intent intentTwo = new Intent(Common.KEY_ENABLE_BUTTON_NEXT);
                        localBroadcastManager.sendBroadcast(intentTwo);


                    }
                });

                storageReference.child(key + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(getContext()).load(uri.toString()).resize(400, 600).centerInside().into(holder.docFace);
                    }
                });
            }
        };

        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private void loadDoctor() {
        cardViewList = new ArrayList<>();
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        options = new FirebaseRecyclerOptions.Builder<DoctorData>().setQuery(rootReference, DoctorData.class).build();
        adapter = new FirebaseRecyclerAdapter<DoctorData, DoctorViewHolder>(options) {

            @NonNull
            @Override
            public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_doctor, parent, false);
                return new DoctorViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull DoctorViewHolder holder, int position, @NonNull DoctorData model) {
                holder.docName.setText(model.getDisplayName());
                holder.docDescription.setText(model.getSpecializations());
                final String key = getRef(position).getKey();
                if (!cardViewList.contains(holder.cardDoctor))
                    cardViewList.add(holder.cardDoctor);

                holder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
                    @Override
                    public void onItemSelectedListener(View view, int pos) {
                        //for not selected card background
                        for (CardView cardView : cardViewList)
                            cardView.setCardBackgroundColor(getContext().getResources().getColor(android.R.color.white));

                        //set selected card background
                        holder.cardDoctor.setCardBackgroundColor(getContext().getResources().getColor(android.R.color.holo_orange_dark));

                        Intent intent = new Intent(Common.KEY_ENABLE_BUTTON_NEXT);
                        intent.putExtra(Common.KEY_DOCTOR_STORE, key);
                        intent.putExtra(Common.KEY_STEP, 1);
                        localBroadcastManager.sendBroadcast(intent);

                    }
                });


                storageReference.child(key + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(getContext()).load(uri.toString()).resize(400, 600).centerInside().into(holder.docFace);
                    }
                });
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    static class DoctorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView cardDoctor;
        TextView docName, docDescription;
        CircleImageView docFace;
        View mView;

        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;


        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            cardDoctor = (CardView) mView.findViewById(R.id.card_doctor);
            docName = (TextView) mView.findViewById(R.id.name_text);
            docDescription = (TextView) mView.findViewById(R.id.status_text);
            docFace = (CircleImageView) mView.findViewById(R.id.profile_imageFace);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            iRecyclerItemSelectedListener.onItemSelectedListener(v, getAdapterPosition());
        }
    }
}
