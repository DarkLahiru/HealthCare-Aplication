package Appointments.Pending;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;

import com.example.healthcare.R;
import com.google.android.material.datepicker.MaterialCalendar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import Appointments.Booking.Model.BookingInformation;
import Appointments.Booking.Model.TimeSlot;

public class PendingAppointmentActivity extends AppCompatActivity {
    DatabaseReference rootReference;
    CalendarView calendarView;
    FirebaseUser firebaseUser;
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_appointment);

        Toolbar toolbar = findViewById(R.id.toolbar);
        calendarView = (CalendarView) findViewById(R.id.datePicker);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        loadAvailableTimeSlotOfDoctor();

    }

    private void loadAvailableTimeSlotOfDoctor() {

        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        rootReference = FirebaseDatabase.getInstance().getReference().child("Appointments");
        rootReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ArrayList<Calendar> calendars = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        BookingInformation bookingInformation = dataSnapshot.getValue(BookingInformation.class);
                        if (bookingInformation.getPatientID().equals(firebaseUser.getUid())) {

                            String dateArray[] = bookingInformation.getDate().split("-");
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateArray[0]));
                            calendar.set(Calendar.MONTH, Integer.parseInt(dateArray[1]));
                            calendar.set(Calendar.YEAR, Integer.parseInt(dateArray[2]));
                            calendars.add(calendar);
                        }
                    }
                    //calendarView.setHighlightedDays(calendars);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}