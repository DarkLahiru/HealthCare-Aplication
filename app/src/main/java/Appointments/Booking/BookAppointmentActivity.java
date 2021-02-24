package Appointments.Booking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import com.example.healthcare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shuhart.stepview.StepView;

import java.util.ArrayList;
import java.util.List;

import Common.Common;
import Common.NonSwipeViewPager;
import ForDoctor.Appointment;

import ForDoctor.LoginDoctorActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.dialog.SpotsDialog;

import static android.widget.Toast.LENGTH_LONG;

public class BookAppointmentActivity extends AppCompatActivity {

    LocalBroadcastManager localBroadcastManager;
    DatabaseReference rootReference;
    FirebaseUser firebaseUser;
    FirebaseAuth mFirebaseAuth;
    AlertDialog dialog;

    @BindView(R.id.btn_prev_step)
    Button btn_prev_step;
    @BindView(R.id.btn_next_step)
    Button btn_next_step;
    @BindView(R.id.step_view)
    StepView step_view;
    @BindView(R.id.viewPaper)
    NonSwipeViewPager viewPaper;

    @OnClick(R.id.btn_prev_step)
    void previousStep() {
        if (Common.step == 3 || Common.step > 0) {
            Common.step--;
            viewPaper.setCurrentItem(Common.step);
            if (Common.step <3){
                btn_next_step.setEnabled(true);
                setColorButton();
            }
        }
    }

    @OnClick(R.id.btn_next_step)
    void nextClick() {

        //Toast.makeText(getApplicationContext(), ""+Common.CURRENT_DOCTOR, Toast.LENGTH_SHORT).show();
        if (Common.step < 3 || Common.step == 0) {
            Common.step++;
            if (Common.step == 1) {

                if (Common.currentDoctor != null){
                    //Toast.makeText(getApplicationContext(), ""+Common.currentDoctor, Toast.LENGTH_SHORT).show();
                    loadLocationByDoctor(Common.currentDoctor);
                }
            }
            else if (Common.step  == 2 ){
                if (Common.currentLocation != null){
                    //Toast.makeText(getApplicationContext(), ""+Common.currentLocation, Toast.LENGTH_SHORT).show();
                    loadTimeByDoctor(Common.currentLocation);
                }
            }
            else if (Common.step  == 3 ){
                if (Common.currentTimeSlot != -1){
                    //Toast.makeText(getApplicationContext(), ""+Common.currentTimeSlot, Toast.LENGTH_SHORT).show();
                    confirmBooking();
                }
            }
            viewPaper.setCurrentItem(Common.step);
        }
    }

    private void confirmBooking() {
        Intent intent = new Intent(Common.KEY_CONFIRM_BOOKING);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void loadTimeByDoctor(String locationID) {
        Intent intent = new Intent(Common.KEY_DISPLAY_TIME_SLOT);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void loadLocationByDoctor(String keyDoctorStore) {
        dialog.show();
        if (!TextUtils.isEmpty(keyDoctorStore)) {

            rootReference = FirebaseDatabase.getInstance().getReference().child("AppointmentLocation").child(keyDoctorStore);
            rootReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<Appointment> appointments = new ArrayList<>();
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                        Appointment appointment = dataSnapshot.getValue(Appointment.class);
                        assert appointment != null;
                        appointment.setLocationID(dataSnapshot.getKey());
                        appointments.add(appointment);
                    }
                    Intent intent = new Intent(Common.KEY_LOCATION_LOAD_DONE);
                    intent.putParcelableArrayListExtra(Common.KEY_LOCATION_LOAD_DONE,appointments);
                    localBroadcastManager.sendBroadcast(intent);
                    dialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
    }

    private BroadcastReceiver buttonNextReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, @NonNull Intent intent) {
            int step = intent.getIntExtra(Common.KEY_STEP,0);
            if (step == 1 ){Common.currentDoctor = intent.getStringExtra(Common.KEY_DOCTOR_STORE);}
            else if (step == 2 ){Common.currentLocation = intent.getStringExtra(Common.KEY_LOCATION_SELECTED);}
            else if (step == 3 ){Common.currentTimeSlot = intent.getIntExtra(Common.KEY_TIME_SLOT,-1);}

            btn_next_step.setEnabled(true);
            setColorButton();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);
        ButterKnife.bind(BookAppointmentActivity.this);

        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(buttonNextReceiver, new IntentFilter(Common.KEY_ENABLE_BUTTON_NEXT));

        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        assert firebaseUser != null;
        Common.currentPatient = firebaseUser.getUid();

        setupStepView();
        setColorButton();

        //View
        viewPaper.setAdapter(new MyViewPageAdapter(getSupportFragmentManager()));
        viewPaper.setOffscreenPageLimit(4);
        viewPaper.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                step_view.go(position, true);
                if (position == 0)
                    btn_prev_step.setEnabled(false);
                else
                    btn_prev_step.setEnabled(true);
                btn_next_step.setEnabled(false);
                setColorButton();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(buttonNextReceiver);
    }

    private void setColorButton() {
        if (btn_next_step.isEnabled()) {
            btn_next_step.setBackgroundResource(R.color.colorPrimaryDark);
        } else {
            btn_next_step.setBackgroundResource(android.R.color.darker_gray);
        }

        if (btn_prev_step.isEnabled()) {
            btn_prev_step.setBackgroundResource(R.color.colorPrimaryDark);
        } else {
            btn_prev_step.setBackgroundResource(android.R.color.darker_gray);
        }
    }

    private void setupStepView() {
        List<String> stepList = new ArrayList<>();
        stepList.add("Doctor");
        stepList.add("Location");
        stepList.add("Time");
        stepList.add("Confirm");
        step_view.setSteps(stepList);

    }
}