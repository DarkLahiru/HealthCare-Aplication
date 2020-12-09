package Appointments.Confirmed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Toast;

import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.example.healthcare.R;
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
import com.shreyaspatil.MaterialDialog.MaterialDialog;
import com.shreyaspatil.MaterialDialog.interfaces.DialogInterface;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import Appointments.Booking.Model.BookingInformation;
import Appointments.Pending.PendingAppointmentActivity;
import Appointments.Pending.PendingTimeSlotAdapter;
import Common.SpacesItemDecoration;

public class ConfirmedAppointmentActivity extends AppCompatActivity {
    com.applandeo.materialcalendarview.CalendarView calendarView;
    SimpleDateFormat simpleDateFormat;
    BottomSheetDialog bottomSheetDialog;
    RecyclerView rVConfirmed;
    List<Calendar> calendars;
    List<BookingInformation> confirmedAppointments, clickedDateTime;

    DatabaseReference rootReference;
    FirebaseUser firebaseUser;
    FirebaseAuth mFirebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmed_appointment);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        initialize();
        initView();
        loadPendingAppointments();
    }

    @SuppressLint("SimpleDateFormat")
    private void loadPendingAppointments() {
        bottomSheetDialog = new BottomSheetDialog(ConfirmedAppointmentActivity.this, R.style.BottomSheetDialogTheme);
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        calendars = new ArrayList<>();
        confirmedAppointments = new ArrayList<>();
        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        rootReference = FirebaseDatabase.getInstance().getReference().child("Appointments");
        rootReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        BookingInformation bookingInformation = dataSnapshot.getValue(BookingInformation.class);
                        if (bookingInformation.getPatientID().equals(firebaseUser.getUid()) && bookingInformation.getStatus().equals("Confirmed")) {

                            String[] dateArray = bookingInformation.getDate().split("-");
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateArray[ 0 ]));
                            calendar.set(Calendar.MONTH, Integer.parseInt(dateArray[ 1 ]) - 1);
                            calendar.set(Calendar.YEAR, Integer.parseInt(dateArray[ 2 ]));
                            calendars.add(calendar);
                            confirmedAppointments.add(bookingInformation);
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
                if (confirmedAppointments.size() != 0) {
                    for (int i = 0; i < confirmedAppointments.size(); i++) {
                        if (date.equalsIgnoreCase(confirmedAppointments.get(i).getDate())) {
                            clickedDateTime.add(confirmedAppointments.get(i));
                        }
                    }
                }
                Collections.reverse(clickedDateTime);

                ConfirmedTimeSlotAdapter adapter = new ConfirmedTimeSlotAdapter(ConfirmedAppointmentActivity.this, clickedDateTime, bottomSheetDialog);
                rVConfirmed.setAdapter(adapter);

            }
        });

    }

    private void initView() {
        rVConfirmed.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        rVConfirmed.setLayoutManager(gridLayoutManager);
        rVConfirmed.addItemDecoration(new SpacesItemDecoration(8));
    }

    private void initialize() {
        calendarView = findViewById(R.id.datePicker);
        rVConfirmed = findViewById(R.id.recycler_time_slot_confirmed);
    }
}