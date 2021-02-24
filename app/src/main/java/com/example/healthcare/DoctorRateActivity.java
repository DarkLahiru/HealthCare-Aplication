package com.example.healthcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Appointments.Booking.Model.BookingInformation;
import ContactDoctor.DoctorRate;

public class DoctorRateActivity extends AppCompatActivity {

    String docID;
    String userUID;

    DatabaseReference rootReference;
    FirebaseUser firebaseUser;
    FirebaseAuth mFirebaseAuth;
    RecyclerView rVFeedback;
    List<DoctorRate> doctorRates;
    ExtendedFloatingActionButton extendedFloatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_rate);

        initialize();
        getDocID();
        loadDoctorFeedback();


        extendedFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void getDocID() {

        docID = getIntent().getStringExtra("docID");

        if (TextUtils.isEmpty(docID)){
            userUID = firebaseUser.getUid();
        }
        else {
            userUID = docID;
        }
    }

    private void initialize() {


        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        extendedFloatingActionButton = findViewById(R.id.extended_fab);
        rVFeedback = findViewById(R.id.rVFeedback);
        rVFeedback.setHasFixedSize(true);
        rVFeedback.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadDoctorFeedback() {

        doctorRates = new ArrayList<>();
        rootReference = FirebaseDatabase.getInstance().getReference().child("DoctorRate").child(userUID);
        rootReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        DoctorRate doctorRate = dataSnapshot.getValue(DoctorRate.class);
                        assert doctorRate != null;
                        if (doctorRate.getDoctorID().equals(userUID)){
                            doctorRates.add(doctorRate);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DoctorRateAdapter adapter  = new DoctorRateAdapter(DoctorRateActivity.this,doctorRates);
        rVFeedback.setAdapter(adapter);

    }
}