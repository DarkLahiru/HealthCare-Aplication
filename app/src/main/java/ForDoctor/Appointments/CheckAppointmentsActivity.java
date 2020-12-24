package ForDoctor.Appointments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.anubhav.android.customdialog.CustomDialog;
import com.applandeo.materialcalendarview.CalendarUtils;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.example.healthcare.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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
import Appointments.Confirmed.ConfirmedAppointmentActivity;
import Common.SpacesItemDecoration;

public class CheckAppointmentsActivity extends AppCompatActivity {

    CalendarView calendarView;
    RecyclerView recycler_time_slot, pending_time_slot;
    LinearLayout layout_confirm;
    LottieAnimationView lottieAnimationView;
    BottomSheetDialog bottomSheetDialog;

    FirebaseUser firebaseUser;
    FirebaseAuth mFirebaseAuth;
    DatabaseReference rootReference;

    SimpleDateFormat simpleDateFormat;

    List<BookingInformation> pending_appointments, confirmed_appointments, same_day, clickedDateTime;
    List<Calendar> calendars;
    List<EventDay> events;


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
        recycler_time_slot.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recycler_time_slot.setLayoutManager(gridLayoutManager);
        recycler_time_slot.addItemDecoration(new SpacesItemDecoration(8));
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                eventOnSelectedDay(eventDay);
            }
        });
        layout_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkPendingAppointments();
            }
        });


    }

    private void eventOnSelectedDay(@NonNull EventDay eventDay) {
        bottomSheetDialog = new BottomSheetDialog(CheckAppointmentsActivity.this, R.style.BottomSheetDialogTheme);
        layout_confirm.setVisibility(View.GONE);
        clickedDateTime = new ArrayList<>();


        Calendar clickedDayCalendar = eventDay.getCalendar();
        String date = simpleDateFormat.format(clickedDayCalendar.getTime());
        if (!pending_appointments.isEmpty()) {
            for (BookingInformation bookingInformation : pending_appointments) {
                if (date.equals(bookingInformation.getDate())) {
                    clickedDateTime.add(bookingInformation);
                }
            }
        }

        if (!confirmed_appointments.isEmpty()) {
            for (BookingInformation bookingInformation : confirmed_appointments) {
                if (date.equals(bookingInformation.getDate())) {
                    clickedDateTime.add(bookingInformation);
                }
            }
        }


        EventOnSelectedDayAdapter adapter = new EventOnSelectedDayAdapter(CheckAppointmentsActivity.this, clickedDateTime,bottomSheetDialog);
        recycler_time_slot.setAdapter(adapter);


    }

    private void checkPendingAppointments() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recycler_time_slot.setLayoutManager(layoutManager);
        layout_confirm.setVisibility(View.GONE);
        new CustomDialog.Builder(this)
                .setTitle("Pending Appointments")
                .setCustomView(recycler_time_slot)
                .setBtnConfirmText("Finish")
                .setBtnConfirmTextSizeDp(16)
                .setBtnConfirmTextColor("#1fd1ab")
                .setBtnCancelText("Cancel", false)
                .setBtnCancelTextColor("#555555")
                .onCancel(new CustomDialog.BtnCallback() {
                    @Override
                    public void onClick(@NonNull CustomDialog customDialog, @NonNull CustomDialog.BtnAction btnAction) {
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
                    }
                })
                .onConfirm(new CustomDialog.BtnCallback() {
                    @Override
                    public void onClick(@NonNull CustomDialog customDialog, @NonNull CustomDialog.BtnAction btnAction) {
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
                    }
                })
                .show();

        CheckAppointmentAdapter adapter = new CheckAppointmentAdapter(CheckAppointmentsActivity.this, pending_appointments);
        recycler_time_slot.setAdapter(adapter);


    }

    private void loadAppointmentsToCalender() {
        calendars = new ArrayList<>();
        events = new ArrayList<>();
        pending_appointments = new ArrayList<>();
        confirmed_appointments = new ArrayList<>();
        same_day = new ArrayList<>();
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
                                        calendars.add(calendar);

                                    } else if (bookingInformation.getStatus().equalsIgnoreCase("confirmed")) {
                                        String[] dateArray = bookingInformation.getDate().split("-");
                                        Calendar calendar = Calendar.getInstance();
                                        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateArray[ 0 ]));
                                        calendar.set(Calendar.MONTH, Integer.parseInt(dateArray[ 1 ]) - 1);
                                        calendar.set(Calendar.YEAR, Integer.parseInt(dateArray[ 2 ]));

                                        confirmed_appointments.add(bookingInformation);
                                        calendars.add(calendar);
                                    }


                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }


                        }

                    }

                    if (!pending_appointments.isEmpty()) {
                        layout_confirm.setVisibility(View.VISIBLE);
                        if (!confirmed_appointments.isEmpty()) {
                            for (BookingInformation pending : pending_appointments) {
                                for (BookingInformation confirm : confirmed_appointments) {
                                    if (confirm.getDate().equals(pending.getDate()))
                                        same_day.add(pending);
                                }
                            }
                            if (!same_day.isEmpty()) {
                                for (BookingInformation sameDay : same_day) {
                                    String[] dateArray = sameDay.getDate().split("-");
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateArray[ 0 ]));
                                    calendar.set(Calendar.MONTH, Integer.parseInt(dateArray[ 1 ]) - 1);
                                    calendar.set(Calendar.YEAR, Integer.parseInt(dateArray[ 2 ]));
                                    events.add(new EventDay(calendar, R.drawable.pending_confirmed));
                                }
                                for (BookingInformation bookingInformation : pending_appointments) {
                                    String[] dateArray = bookingInformation.getDate().split("-");
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateArray[ 0 ]));
                                    calendar.set(Calendar.MONTH, Integer.parseInt(dateArray[ 1 ]) - 1);
                                    calendar.set(Calendar.YEAR, Integer.parseInt(dateArray[ 2 ]));
                                    events.add(new EventDay(calendar, R.drawable.for_pending));
                                }
                                for (BookingInformation bookingInformation : confirmed_appointments) {
                                    String[] dateArray = bookingInformation.getDate().split("-");
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateArray[ 0 ]));
                                    calendar.set(Calendar.MONTH, Integer.parseInt(dateArray[ 1 ]) - 1);
                                    calendar.set(Calendar.YEAR, Integer.parseInt(dateArray[ 2 ]));

                                    events.add(new EventDay(calendar, R.drawable.for_confirm));
                                }

                            } else {
                                for (BookingInformation bookingInformation : pending_appointments) {
                                    String[] dateArray = bookingInformation.getDate().split("-");
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateArray[ 0 ]));
                                    calendar.set(Calendar.MONTH, Integer.parseInt(dateArray[ 1 ]) - 1);
                                    calendar.set(Calendar.YEAR, Integer.parseInt(dateArray[ 2 ]));
                                    events.add(new EventDay(calendar, R.drawable.for_pending));
                                }
                                for (BookingInformation bookingInformation : confirmed_appointments) {
                                    String[] dateArray = bookingInformation.getDate().split("-");
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateArray[ 0 ]));
                                    calendar.set(Calendar.MONTH, Integer.parseInt(dateArray[ 1 ]) - 1);
                                    calendar.set(Calendar.YEAR, Integer.parseInt(dateArray[ 2 ]));

                                    events.add(new EventDay(calendar, R.drawable.for_confirm));
                                }
                            }

                        } else {
                            for (BookingInformation bookingInformation : pending_appointments) {
                                String[] dateArray = bookingInformation.getDate().split("-");
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateArray[ 0 ]));
                                calendar.set(Calendar.MONTH, Integer.parseInt(dateArray[ 1 ]) - 1);
                                calendar.set(Calendar.YEAR, Integer.parseInt(dateArray[ 2 ]));
                                events.add(new EventDay(calendar, R.drawable.for_pending));
                            }
                        }

                    } else if (!confirmed_appointments.isEmpty()) {
                        for (BookingInformation bookingInformation : confirmed_appointments) {
                            String[] dateArray = bookingInformation.getDate().split("-");
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateArray[ 0 ]));
                            calendar.set(Calendar.MONTH, Integer.parseInt(dateArray[ 1 ]) - 1);
                            calendar.set(Calendar.YEAR, Integer.parseInt(dateArray[ 2 ]));

                            events.add(new EventDay(calendar, R.drawable.for_confirm));
                        }
                    }


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
        //pending_time_slot = findViewById(R.id.recycler_view_pending_appointments);
        calendarView = findViewById(R.id.datePicker);
        recycler_time_slot = findViewById(R.id.recycler_time_slot);
        layout_confirm = findViewById(R.id.layout_confirm);
        lottieAnimationView = findViewById(R.id.lottie_animation_view);
    }
}