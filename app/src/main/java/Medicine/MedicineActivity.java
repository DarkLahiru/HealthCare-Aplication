package Medicine;

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
import android.widget.Button;

import com.example.healthcare.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class MedicineActivity extends AppCompatActivity {

    /*private List<ListData>listData;
    private MyAdapter adapter;*/
    private RecyclerView recyclerView;
    FirebaseRecyclerOptions<ListData> options;
    FirebaseRecyclerAdapter<ListData,MyRecyclerAdapter>adapter;
    Button btnAdd;

    DatabaseReference rootReference;
    FirebaseUser firebaseUser;
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        btnAdd = (Button)findViewById(R.id.addMedDetails) ;


        recyclerView = (RecyclerView)findViewById(R.id.rVMedDetails);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        rootReference = FirebaseDatabase.getInstance().getReference().child("Medical Details").child(firebaseUser.getUid());

        /*listData = new ArrayList<>();

        rootReference = FirebaseDatabase.getInstance().getReference(firebaseUser.getUid()).child("Medical Details");
        rootReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        ListData i = snapshot.getValue(ListData.class);
                        listData.add(i);
                    }
                    adapter = new MyAdapter(listData);
                    recyclerView.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
        LoadData();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goAdd = new Intent(getApplicationContext(), AddMedRecordActivity.class);
                startActivity(goAdd);
            }
        });

    }

    private void LoadData() {
        options = new FirebaseRecyclerOptions.Builder<ListData>().setQuery(rootReference,ListData.class).build();
        adapter = new FirebaseRecyclerAdapter<ListData, MyRecyclerAdapter>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyRecyclerAdapter holder, final int position, @NonNull ListData model) {
                holder.txtDate.setText(model.getDate());
                holder.txtReason.setText(model.getReason());
                holder.v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent  intent = new Intent(getApplicationContext(), ViewMedicineActivity.class);
                        intent.putExtra("MedItem",getRef(position).getKey());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public MyRecyclerAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row,parent,false);
                return new MyRecyclerAdapter(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }
}
