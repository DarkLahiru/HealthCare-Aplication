package Reminder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthcare.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Medicine.MedicineActivity;

public class ReminderActivity extends AppCompatActivity {

    private FloatingActionButton fab;

    private RecyclerView recyclerView;
    DatabaseReference rootReference;
    FirebaseUser firebaseUser;
    FirebaseAuth mFirebaseAuth;
    SwipeController swipeController = null;
    List<Reminder> reminders;
    ReminderAdaptor reminderAdaptor;

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
                Intent intentManual = new Intent(getApplicationContext(), AddReminderActivity.class);
                startActivity(intentManual);
            }
        });


        setupFirebaseDatabase();
        setupRecyclerView();
        LoadData();
    }

    private void setupFirebaseDatabase() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        rootReference = FirebaseDatabase.getInstance().getReference().child("Reminders").child(firebaseUser.getUid());
    }

    private void setupRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.rV_Reminder);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void LoadData() {
        reminders = new ArrayList<>();
        rootReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Reminder reminder = dataSnapshot.getValue(Reminder.class);
                        reminders.add(reminder);

                        reminderAdaptor = new ReminderAdaptor(getApplicationContext(), reminders);
                        recyclerView.setAdapter(reminderAdaptor);

                        swipeController = new SwipeController(new SwipeControllerActions() {
                            @Override
                            public void onRightClicked(int position) {
                                rootReference.child(reminders.get(position).getNodeKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(), "Delete Reminder Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                reminderAdaptor.reminders.remove(position);
                                reminderAdaptor.notifyItemRemoved(position);
                                reminderAdaptor.notifyItemRangeChanged(position, reminderAdaptor.getItemCount());

                                finish();
                                overridePendingTransition(0, 0);
                                startActivity(getIntent());
                                overridePendingTransition(0, 0);




                            }
                        });
                        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
                        itemTouchhelper.attachToRecyclerView(recyclerView);
                        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
                            @Override
                            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                                swipeController.onDraw(c);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
