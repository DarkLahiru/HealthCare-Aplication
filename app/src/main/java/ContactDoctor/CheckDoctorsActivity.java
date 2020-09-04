package ContactDoctor;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.healthcare.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import medicine.ListData;


public class CheckDoctorsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseRecyclerOptions<ListData> options;
    FirebaseRecyclerAdapter<ListData, DoctorViewHolder> adapter;

    DatabaseReference rootReference;
    FirebaseUser firebaseUser;
    FirebaseAuth mFirebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_doctors);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView)findViewById(R.id.rVResult_List);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        rootReference = FirebaseDatabase.getInstance().getReference(firebaseUser.getUid()).child("Medical Details");
        LoadData();
    }


    private void LoadData() {
        options = new FirebaseRecyclerOptions.Builder<ListData>().setQuery(rootReference,ListData.class).build();
        adapter = new FirebaseRecyclerAdapter<ListData, DoctorViewHolder>(options) {

            @NonNull
            @Override
            public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_doctor,parent,false);
                return new DoctorViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull DoctorViewHolder holder, int position, @NonNull ListData model) {
                holder.docName.setText(model.getDoctor());
                holder.docDescription.setText(model.getReason());
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BottomSheetDialog bottomSheetDialog =  new BottomSheetDialog(CheckDoctorsActivity.this,R.style.BottomSheetDialogTheme);
                        View bottomSheetView  = LayoutInflater.from(getApplicationContext())
                                .inflate(
                                        R.layout.layout_bottomsheet_doctor,
                                        (LinearLayout)findViewById(R.id.bottomSheetContainer)
                                );
                        bottomSheetDialog.setContentView(bottomSheetView);
                        bottomSheetDialog.show();
                    }
                });
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    public static class DoctorViewHolder extends RecyclerView.ViewHolder{

        TextView docName , docDescription;
        View mView;
        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            docName = (TextView)mView.findViewById(R.id.name_text);
            docDescription = (TextView)mView.findViewById(R.id.status_text);
        }
    }
}
