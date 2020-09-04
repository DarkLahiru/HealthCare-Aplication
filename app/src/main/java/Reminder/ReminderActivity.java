package Reminder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.healthcare.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class ReminderActivity extends AppCompatActivity {

    private FloatingActionButton fab;

    private RecyclerView recyclerView;
    FirebaseRecyclerOptions<Reminder> options;
    FirebaseRecyclerAdapter<Reminder,ReminderViewHolder>adapter;

    DatabaseReference rootReference;
    FirebaseUser firebaseUser;
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentManual = new Intent(getApplicationContext(),AddReminderActivity.class);
                startActivity(intentManual);
                ;
            }
        });

        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        rootReference = FirebaseDatabase.getInstance().getReference("Patients").child(firebaseUser.getUid()).child("Reminders");

        recyclerView = (RecyclerView) findViewById(R.id.rV_Reminder);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        LoadData();
    }

    private void LoadData() {
        options = new FirebaseRecyclerOptions.Builder<Reminder>().setQuery(rootReference,Reminder.class).build();
        adapter = new FirebaseRecyclerAdapter<Reminder, ReminderViewHolder>(options) {
            @NonNull
            @Override
            public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_list_layout,parent,false);
                return new ReminderViewHolder(view);

            }

            @Override
            protected void onBindViewHolder(@NonNull ReminderViewHolder holder, int position, @NonNull Reminder model) {
                holder.medName.setText(model.getMedicineName());
                holder.medInstruction.setText(model.getInstructions());
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }


    public static class ReminderViewHolder extends RecyclerView.ViewHolder{

        TextView medName , medInstruction;
        View mView;
        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            medName = (TextView)mView.findViewById(R.id.txtMedName);
            medInstruction = (TextView)mView.findViewById(R.id.txtInstruction);
        }
    }
}
