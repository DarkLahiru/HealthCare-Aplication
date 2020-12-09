package Appointments.Pending;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarUtils;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.example.healthcare.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.MaterialCalendar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shreyaspatil.MaterialDialog.MaterialDialog;
import com.shreyaspatil.MaterialDialog.interfaces.DialogInterface;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import Appointments.Booking.Model.BookingInformation;
import Appointments.Booking.Model.TimeSlot;
import Common.SpacesItemDecoration;

public class PendingAppointmentActivity extends AppCompatActivity {
    DatabaseReference rootReference;
    com.applandeo.materialcalendarview.CalendarView calendarView;
    FirebaseUser firebaseUser;
    FirebaseAuth mFirebaseAuth;
    RecyclerView rVPending;
    List<BookingInformation> pendingAppointments, clickedDateTime;
    SimpleDateFormat simpleDateFormat;
    List<Calendar> calendars;
    BottomSheetDialog bottomSheetDialog;
    MaterialDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_appointment);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        initialize();
        initView();
        loadPendingAppointments();


    }


    @SuppressLint("SimpleDateFormat")
    private void loadPendingAppointments() {
        bottomSheetDialog = new BottomSheetDialog(PendingAppointmentActivity.this, R.style.BottomSheetDialogTheme);
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        calendars = new ArrayList<>();
        pendingAppointments = new ArrayList<>();
        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        rootReference = FirebaseDatabase.getInstance().getReference().child("Appointments");
        rootReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        BookingInformation bookingInformation = dataSnapshot.getValue(BookingInformation.class);
                        if (bookingInformation.getPatientID().equals(firebaseUser.getUid()) && bookingInformation.getStatus().equals("pending")) {

                            String[] dateArray = bookingInformation.getDate().split("-");
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateArray[ 0 ]));
                            calendar.set(Calendar.MONTH, Integer.parseInt(dateArray[ 1 ]) - 1);
                            calendar.set(Calendar.YEAR, Integer.parseInt(dateArray[ 2 ]));
                            calendars.add(calendar);
                            pendingAppointments.add(bookingInformation);
                        }
                    }
                    calendarView.setHighlightedDays(calendars);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {

                clickedDateTime = new ArrayList<>();
                Calendar clickedDayCalendar = eventDay.getCalendar();
                String date = simpleDateFormat.format(clickedDayCalendar.getTime());
                //Toast.makeText(getApplicationContext(),date,Toast.LENGTH_SHORT).show();
                if (pendingAppointments.size() != 0) {
                    for (int i = 0; i < pendingAppointments.size(); i++) {
                        if (date.equalsIgnoreCase(pendingAppointments.get(i).getDate())) {
                            clickedDateTime.add(pendingAppointments.get(i));
                        }
                    }
                }
                Collections.reverse(clickedDateTime);
                mDialog = new MaterialDialog.Builder(PendingAppointmentActivity.this)
                        .setTitle("Cancel?")
                        .setMessage("Are you sure want to cancel this appointment?")
                        .setCancelable(false)
                        .setPositiveButton("Cancel", R.drawable.ic_delete, new MaterialDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                // Delete Operation
                                rootReference.child(clickedDateTime.get(which-1).getNodeKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(), "Remove Location Successfully", Toast.LENGTH_SHORT).show();
                                        dialogInterface.dismiss();
                                        finish();
                                        overridePendingTransition(0, 0);
                                        startActivity(getIntent());
                                        overridePendingTransition(0, 0);

                                    }
                                });

                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                                        .child("AppointmentTimeSlot")
                                        .child(clickedDateTime.get(which-1).getDoctorID())
                                        .child(clickedDateTime.get(which-1).getDate())
                                        .child(clickedDateTime.get(which-1).getNodeKey());
                                databaseReference.removeValue().addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "TimeSlot Remove process is Unsuccessful ", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Don't", R.drawable.ic_close, new MaterialDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                dialogInterface.dismiss();
                            }
                        })
                        .build();
                PendingTimeSlotAdapter adapter = new PendingTimeSlotAdapter(PendingAppointmentActivity.this, clickedDateTime, bottomSheetDialog, mDialog);
                rVPending.setAdapter(adapter);

            }
        });



    }

    private void initView() {
        rVPending.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        rVPending.setLayoutManager(gridLayoutManager);
        rVPending.addItemDecoration(new SpacesItemDecoration(8));


    }

    private void initialize() {
        calendarView = findViewById(R.id.datePicker);
        rVPending = findViewById(R.id.recycler_time_slot_pending);
    }
}