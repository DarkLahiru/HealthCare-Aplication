package medicine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.healthcare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class AddMedRecordActivity extends AppCompatActivity {

    TextInputLayout date, hospital, doc, reason, pills;
    Button btnSave;
    DatabaseReference rootReference;
    FirebaseUser firebaseUser;
    FirebaseAuth mFirebaseAuth;

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

        final ListData listData = new ListData();



        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        rootReference = FirebaseDatabase.getInstance().getReference(firebaseUser.getUid()).child("Medical Details");
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
                                Toast.makeText(getApplicationContext(), "Update Data Successfully", Toast.LENGTH_SHORT).show();
                                finish();


                            }
                        }
                    });
                }

            }
        });

    }
}
