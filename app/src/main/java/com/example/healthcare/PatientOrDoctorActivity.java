package com.example.healthcare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import ForDoctor.LoginDoctorActivity;

public class PatientOrDoctorActivity extends AppCompatActivity {

    ImageView patientLogin ,doctorLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_or_doctor);


        patientLogin  = (ImageView)findViewById(R.id.patientLogin);
        doctorLogin  = (ImageView)findViewById(R.id.doctorLogin);

        patientLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent patientLogin = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(patientLogin);
            }
        });

        doctorLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent doctorLogin = new Intent(getApplicationContext(), LoginDoctorActivity.class);
                startActivity(doctorLogin);
            }
        });
    }
}
