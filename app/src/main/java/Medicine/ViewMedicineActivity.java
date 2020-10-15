package Medicine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.healthcare.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ViewMedicineActivity extends AppCompatActivity {

    TextInputLayout date, hospital, doc, reason, pills;
    Button btnDelete;

    DatabaseReference rootReference;
    FirebaseUser firebaseUser;
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_medicine);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        date = findViewById(R.id.medViewDate);
        hospital = findViewById(R.id.medViewPlace);
        doc = findViewById(R.id.medViewDoc);
        reason = findViewById(R.id.medViewReason);
        pills = findViewById(R.id.medViewPills);
        btnDelete = findViewById(R.id.btnDeleteMed);

        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        rootReference = FirebaseDatabase.getInstance().getReference().child("Medical Details").child(firebaseUser.getUid());

        final String MedUID = getIntent().getStringExtra("MedItem");
        assert MedUID != null;
        rootReference.child(MedUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String dateView = Objects.requireNonNull(snapshot.child("date").getValue()).toString();
                    String hospitalView = Objects.requireNonNull(snapshot.child("hospital").getValue()).toString();
                    String docView = Objects.requireNonNull(snapshot.child("doctor").getValue()).toString();
                    String reasonView = Objects.requireNonNull(snapshot.child("reason").getValue()).toString();
                    String pillsView = Objects.requireNonNull(snapshot.child("pills").getValue()).toString();

                    Objects.requireNonNull(date.getEditText()).setText(dateView);
                    Objects.requireNonNull(hospital.getEditText()).setText(hospitalView);
                    Objects.requireNonNull(doc.getEditText()).setText(docView);
                    Objects.requireNonNull(reason.getEditText()).setText(reasonView);
                    Objects.requireNonNull(pills.getEditText()).setText(pillsView);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootReference.child(MedUID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Delete Medical Record Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),MedicineActivity.class));
                    }
                });
            }
        });
    }
}
