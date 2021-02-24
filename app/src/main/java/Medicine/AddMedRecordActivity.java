package Medicine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.healthcare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import Profile.EditMyProfileActivity;

public class AddMedRecordActivity extends AppCompatActivity {

    TextInputLayout date, hospital, doc, reason, pills;
    Button btnSave;
    DatabaseReference rootReference;
    FirebaseUser firebaseUser;
    FirebaseAuth mFirebaseAuth;
    TextInputEditText txtMedDate;

    /*long maxNum = 0;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_med_record);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        date = findViewById(R.id.medDate);
        hospital = findViewById(R.id.medPlace);
        doc = findViewById(R.id.medDoc);
        reason = findViewById(R.id.medReason);
        pills = findViewById(R.id.medPills);
        btnSave = findViewById(R.id.btnSaveMed);
        txtMedDate = findViewById(R.id.txtMedDate);

        txtMedDate.setEnabled(true);
        txtMedDate.setTextIsSelectable(true);
        txtMedDate.setFocusable(false);
        txtMedDate.setFocusableInTouchMode(false);

        final ListData listData = new ListData();



        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        rootReference = FirebaseDatabase.getInstance().getReference().child("Medical Details").child(firebaseUser.getUid());
        /*rootReference.addValueEventListener(new ValueEventListener() {
            @Override+
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    maxNum = (dataSnapshot.getChildrenCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        txtMedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mCurrentDate = Calendar.getInstance();
                int year = mCurrentDate.get(Calendar.YEAR);
                int month = mCurrentDate.get(Calendar.MONTH);
                int day = mCurrentDate.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker;
                datePicker = new DatePickerDialog(AddMedRecordActivity.this, new DatePickerDialog.OnDateSetListener() {

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String strMonth = "", strDay = "";
                        month++;
                        if (month < 10) {
                            strMonth = "0" + month;
                        } else {
                            strMonth = "" + month;
                        }
                        if (dayOfMonth < 10) {
                            strDay = "0" + dayOfMonth;
                        } else {
                            strDay = "" + dayOfMonth;
                        }
                        Objects.requireNonNull(date.getEditText()).setText(strMonth + "/" + strDay + "/" + year);

                    }
                }, year, month, day);
                datePicker.getDatePicker().setMaxDate(new Date().getTime());
                datePicker.show();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dateMed= date.getEditText().getText().toString();
                String hospitalMed = hospital.getEditText().getText().toString();
                String docMed = doc.getEditText().getText().toString();
                String reasonMed = reason.getEditText().getText().toString();
                String pillsMed = pills.getEditText().getText().toString();

                if (dateMed.isEmpty() || hospitalMed.isEmpty() || docMed.isEmpty() || reasonMed.isEmpty() || pillsMed.isEmpty() ) {
                    Toast.makeText(getApplicationContext(), "Please fill all the fields!!", Toast.LENGTH_SHORT).show();
                }
                else {

                    listData.setDate(dateMed);
                    listData.setHospital(hospitalMed);
                    listData.setDoctor(docMed);
                    listData.setReason(reasonMed);
                    listData.setPills(pillsMed);

                    rootReference.push().setValue(listData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isComplete()) {
                                Toast.makeText(getApplicationContext(), "Add Data Successfully", Toast.LENGTH_SHORT).show();
                                finish();


                            }
                        }
                    });
                }

            }
        });

    }
}
