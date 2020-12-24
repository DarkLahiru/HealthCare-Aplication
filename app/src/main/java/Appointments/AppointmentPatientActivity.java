package Appointments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.healthcare.R;

import java.util.Objects;

import Appointments.Booking.BookAppointmentActivity;
import Appointments.Confirmed.ConfirmedAppointmentActivity;
import Appointments.Pending.PendingAppointmentActivity;

public class AppointmentPatientActivity extends AppCompatActivity {

    ImageView add,pending,confirm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_patient);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        add = findViewById(R.id.createApp);
        pending = findViewById(R.id.pendingApp);
        confirm = findViewById(R.id.confirmApp);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent((getApplicationContext()), BookAppointmentActivity.class));
            }
        });

        pending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent((getApplicationContext()), PendingAppointmentActivity.class));
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent((getApplicationContext()), ConfirmedAppointmentActivity.class));
            }
        });
    }

}