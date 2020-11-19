package ForDoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthcare.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class AppointmentLocationActivity extends AppCompatActivity {

    RecyclerView rvLocations;
    FloatingActionButton fab;
    TextInputLayout locName, locAddress;
    AlertDialog.Builder dialogBuilder;
    AlertDialog dialog;
    Button add,delete,cancel,finish;
    DatabaseReference rootReference;
    FirebaseUser firebaseUser;
    FirebaseAuth mFirebaseAuth;

    FirebaseRecyclerOptions<Appointment> options;
    FirebaseRecyclerAdapter<Appointment, UserViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_location_activity);

        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();

        rvLocations = findViewById(R.id.rvMeetLocations);
        rvLocations.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvLocations.setLayoutManager(linearLayoutManager);
        LoadData();
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUpLocation();
            }
        });

        finish  = findViewById(R.id.btnFinish);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),NavigationDoctor.class));
            }
        });
    }

    private void LoadData() {

        rootReference = FirebaseDatabase.getInstance().getReference("AppointmentLocation").child(firebaseUser.getUid());
        options = new FirebaseRecyclerOptions.Builder<Appointment>().setQuery(rootReference, Appointment.class).build();
        adapter = new FirebaseRecyclerAdapter<Appointment, UserViewHolder>(options) {
            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_meet_location, parent, false);
                return new UserViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull Appointment model) {
                holder.name.setText(model.getLocationName());
                holder.address.setText(model.getLocationAddress());
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder = new AlertDialog.Builder(AppointmentLocationActivity.this);
                        final View deletePopUp = getLayoutInflater().inflate(R.layout.row_delete_or_not, null);

                        delete = deletePopUp.findViewById(R.id.btnDeleteLoc);
                        cancel = deletePopUp.findViewById(R.id.btnCancelLoc);
                        dialogBuilder.setView(deletePopUp);
                        dialog = dialogBuilder.create();
                        dialog.show();

                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                rootReference.child(Objects.requireNonNull(getRef(position).getKey())).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(), "Remove Location Successfully", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                });
                            }
                        });

                    }
                });
            }
        };
        adapter.startListening();
        rvLocations.setAdapter(adapter);
    }

    private void popUpLocation() {
        dialogBuilder = new AlertDialog.Builder(this);
        final View locationPopUp = getLayoutInflater().inflate(R.layout.popup_meet_location, null);
        rvLocations = locationPopUp.findViewById(R.id.rvMeetLocations);
        locName = locationPopUp.findViewById(R.id.txtPlace);
        locAddress = locationPopUp.findViewById(R.id.txtLocAddress);
        add = locationPopUp.findViewById(R.id.btnAddLocation);

        dialogBuilder.setView(locationPopUp);
        dialog = dialogBuilder.create();
        dialog.show();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String locationName = Objects.requireNonNull(locName.getEditText()).getText().toString();
                String locationAddress = Objects.requireNonNull(locAddress.getEditText()).getText().toString();

                if (locationName.isEmpty() && !locationAddress.isEmpty()) {
                    locName.requestFocus();
                    Toast.makeText(getApplicationContext(), "Please fill all the fields!!", Toast.LENGTH_SHORT).show();
                }
                if (locationAddress.isEmpty() && !locationName.isEmpty()) {
                    locAddress.requestFocus();
                    Toast.makeText(getApplicationContext(), "Please fill all the fields!!", Toast.LENGTH_SHORT).show();
                }

                Appointment appointment = new Appointment();
                appointment.setLocationName(locationName);
                appointment.setLocationAddress(locationAddress);
                appointment.setLocationID(firebaseUser.getUid());

                mFirebaseAuth = FirebaseAuth.getInstance();
                firebaseUser = mFirebaseAuth.getCurrentUser();
                rootReference = FirebaseDatabase.getInstance().getReference().child("AppointmentLocation").child(firebaseUser.getUid());
                rootReference.push().setValue(appointment).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isComplete()) {
                            //Toast.makeText(getApplicationContext(), "Location Added", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });


            }
        });


    }

    private static class UserViewHolder extends RecyclerView.ViewHolder {

        TextView name, address;
        View mView;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            name = (TextView) mView.findViewById(R.id.txtLocation);
            address = (TextView) mView.findViewById(R.id.txtAddress);

        }
    }
}