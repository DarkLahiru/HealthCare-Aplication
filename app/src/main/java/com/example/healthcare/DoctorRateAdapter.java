package com.example.healthcare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import ContactDoctor.DoctorRate;

public class DoctorRateAdapter extends RecyclerView.Adapter<DoctorRateAdapter.MyViewHolder> {
    Context context;
    List<DoctorRate> doctorRates;
    DatabaseReference databaseReference;
    public DoctorRateAdapter(Context context, List<DoctorRate> doctorRates) {
        this.context = context;
        this.doctorRates = doctorRates;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(context).inflate(R.layout.row_feedback, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //holder.patientName.setText(doctorRates.get(position).getPatientName());
        holder.feedback.setText(doctorRates.get(position).getFeedback());
        holder.stars.setRating(Float.parseFloat(doctorRates.get(position).getStars()));
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Patients").child(doctorRates.get(position).getPatientID()).child("MyProfile");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.patientName.setText(snapshot.child("displayName").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return doctorRates.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView patientName,feedback;
        RatingBar stars;
        View mView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            patientName = itemView.findViewById(R.id.txtPatientName);
            feedback = itemView.findViewById(R.id.txtFeedback);
            stars = itemView.findViewById(R.id.ratingBar);

        }
    }
}
