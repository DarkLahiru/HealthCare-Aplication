package Appointments.Booking.Fragments;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.healthcare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

import Appointments.Booking.Model.BookingInformation;
import Appointments.Booking.Model.TimeSlot;
import Common.Common;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class BookingStep4Fragment extends Fragment {

    SimpleDateFormat simpleDateFormat;
    LocalBroadcastManager localBroadcastManager;

    DatabaseReference rootReference;
    FirebaseUser firebaseUser;
    FirebaseAuth mFirebaseAuth;

    Unbinder unbinder;

    @BindView(R.id.txt_booking_doctor)
    TextView txt_booking_doctor;
    @BindView(R.id.txt_booking_address)
    TextView txt_booking_address;
    @BindView(R.id.txt_booking_address_name)
    TextView txt_booking_address_name;
    @BindView(R.id.txt_booking_time_slot)
    TextView txt_booking_time_slot;
    @BindView(R.id.txt_booking_phone_number)
    TextView txt_booking_phone_number;

    @OnClick(R.id.btn_confirm)
    void confirmBooking() {


        BookingInformation bookingInformation = new BookingInformation();

        bookingInformation.setStatus("pending");
        bookingInformation.setPatientID(Common.currentPatient);
        bookingInformation.setDoctorID(Common.currentDoctor);
        bookingInformation.setLocationId(Common.currentLocation);
        bookingInformation.setDoctorName(txt_booking_doctor.getText().toString());
        bookingInformation.setDocPhone(txt_booking_phone_number.getText().toString());
        bookingInformation.setLocationName(txt_booking_address_name.getText().toString());
        bookingInformation.setLocationAddress(txt_booking_address.getText().toString());
        bookingInformation.setTime(new StringBuilder(Common.convertTimeSlotToString(Common.currentTimeSlot))
                .append(" at ")
                .append(simpleDateFormat.format(Common.currentDate.getTime())).toString());
        bookingInformation.setSlot(Long.valueOf(Common.currentTimeSlot));
        bookingInformation.setDate(simpleDateFormat.format(Common.currentDate.getTime()));





        rootReference = FirebaseDatabase.getInstance().getReference().child("Patients").child(Common.currentPatient).child("MyProfile");
        rootReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookingInformation.setPatientName(Objects.requireNonNull(snapshot.child("fullName").getValue()).toString());
                bookingInformation.setPatientPhone(Objects.requireNonNull(snapshot.child("phoneNum").getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        rootReference = FirebaseDatabase.getInstance().getReference().child("Appointments");
        DatabaseReference pushedPostRef = rootReference.push();
        String postId = pushedPostRef.getKey();
        bookingInformation.setNodeKey(postId);
        assert postId != null;
        rootReference.child(postId).setValue(bookingInformation).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()) {
                    resetStaticData();
                    getActivity().finish();
                    Toast.makeText(getContext(), "Appointment Successfully Created", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setSlot(Long.valueOf(Common.currentTimeSlot));
        rootReference  = FirebaseDatabase.getInstance().getReference().child("AppointmentTimeSlot").child(Common.currentDoctor).child(simpleDateFormat.format(Common.currentDate.getTime()));
        rootReference.child(postId).setValue(timeSlot).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()) {
                    getActivity().finish();
                }
            }
        });

    }


    BroadcastReceiver confirmBookingReceiver =  new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setData();
        }
    };

    private void setData() {
        rootReference = FirebaseDatabase.getInstance().getReference().child("Doctors").child(Common.currentDoctor).child("MyProfile");
        rootReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                txt_booking_doctor.setText(Objects.requireNonNull(snapshot.child("fullName").getValue()).toString());
                txt_booking_phone_number.setText(Objects.requireNonNull(snapshot.child("phoneNum").getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        rootReference = FirebaseDatabase.getInstance().getReference().child("AppointmentLocation").child(Common.currentDoctor).child(Common.currentLocation);
        rootReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               txt_booking_address.setText(Objects.requireNonNull(snapshot.child("locationAddress").getValue()).toString());
               txt_booking_address_name.setText(Objects.requireNonNull(snapshot.child("locationName").getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        txt_booking_time_slot.setText(new StringBuilder(Common.convertTimeSlotToString(Common.currentTimeSlot))
        .append(" at ")
        .append(simpleDateFormat.format(Common.currentDate.getTime())));

    }

    private void resetStaticData() {
        Common.step = 0;
        Common.currentDoctor = null;
        Common.currentLocation = null;
        Common.currentTimeSlot = -1;
        Common.currentDate.add(Calendar.DATE,0);
    }

    static BookingStep4Fragment instance;

    public static BookingStep4Fragment getInstance() {
        if (instance == null)
            instance = new BookingStep4Fragment();
        return instance;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(confirmBookingReceiver, new IntentFilter(Common.KEY_CONFIRM_BOOKING));

    }

    @Override
    public void onDestroy() {
        localBroadcastManager.unregisterReceiver(confirmBookingReceiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View itemView = inflater.inflate(R.layout.fragment_booking_step_four, container, false);
        unbinder = ButterKnife.bind(this, itemView);
        return itemView;
    }
}
