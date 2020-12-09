package ForDoctor.Appointments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.applandeo.materialcalendarview.CalendarUtils;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.example.healthcare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import Appointments.Booking.Model.BookingInformation;

public class CheckAppointmentsActivity extends AppCompatActivity {

    CalendarView calendarView;
    RecyclerView recycler_time_slot;
    LinearLayout layout_confirm;
    LottieAnimationView lottieAnimationView;

    FirebaseUser firebaseUser;
    FirebaseAuth mFirebaseAuth;
    DatabaseReference rootReference;

    SimpleDateFormat simpleDateFormat;

    List<BookingInformation> pending_appointments, confirmed_appointments;
    List<Calendar> calendars;
    List<EventDay> events;

    Typeface typeface;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_appointments);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");


        initialize();
        loadAppointmentsToCalender();
    }

    private void loadAppointmentsToCalender() {
        calendars = new ArrayList<>();
        events = new ArrayList<>();
        pending_appointments = new ArrayList<>();
        confirmed_appointments = new ArrayList<>();

        rootReference = FirebaseDatabase.getInstance().getReference().child("Appointments");
        rootReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        BookingInformation bookingInformation = dataSnapshot.getValue(BookingInformation.class);
                        assert bookingInformation != null;
                        if (bookingInformation.getDoctorID().equals(firebaseUser.getUid())) {
                            try {
                                if (!simpleDateFormat.parse(bookingInformation.getDate()).before(new Date())) {
                                    if (bookingInformation.getStatus().equalsIgnoreCase("pending")) {

                                        String[] dateArray = bookingInformation.getDate().split("-");
                                        Calendar calendar = Calendar.getInstance();
                                        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateArray[ 0 ]));
                                        calendar.set(Calendar.MONTH, Integer.parseInt(dateArray[ 1 ]) - 1);
                                        calendar.set(Calendar.YEAR, Integer.parseInt(dateArray[ 2 ]));

                                        pending_appointments.add(bookingInformation);
                                        events.add(new EventDay(calendar, R.drawable.for_pending));
                                        calendars.add(calendar);

                                    }
                                    if (bookingInformation.getStatus().equalsIgnoreCase("confirmed")) {
                                        String[] dateArray = bookingInformation.getDate().split("-");
                                        Calendar calendar = Calendar.getInstance();
                                        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateArray[ 0 ]));
                                        calendar.set(Calendar.MONTH, Integer.parseInt(dateArray[ 1 ]) - 1);
                                        calendar.set(Calendar.YEAR, Integer.parseInt(dateArray[ 2 ]));

                                        confirmed_appointments.add(bookingInformation);
                                        events.add(new EventDay(calendar, R.drawable.for_confirm));
                                        calendars.add(calendar);
                                    }


                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }


                        }

                    }
                    if (!pending_appointments.isEmpty())
                        layout_confirm.setVisibility(View.VISIBLE);

                    calendarView.setEvents(events);
                    calendarView.setHighlightedDays(calendars);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initialize() {
        calendarView = findViewById(R.id.datePicker);
        recycler_time_slot = findViewById(R.id.recycler_time_slot);
        layout_confirm = findViewById(R.id.layout_confirm);
        lottieAnimationView = findViewById(R.id.lottie_animation_view);
    }
}