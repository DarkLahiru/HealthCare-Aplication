package Contact;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class ContactActivity extends AppCompatActivity {

    Button btnAddNew;
    private RecyclerView recyclerView;
    FirebaseRecyclerOptions<ContactDetails> options;
    FirebaseRecyclerAdapter<ContactDetails,ContactRecyclerAdapter> adapter;

    DatabaseReference rootReference;
    FirebaseUser firebaseUser;
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        btnAddNew = findViewById(R.id.addNewContact);

        recyclerView = (RecyclerView)findViewById(R.id.rVContact);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        rootReference = FirebaseDatabase.getInstance().getReference(firebaseUser.getUid()).child("Contact Details");

        LoadData();

        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(ContactActivity.this, AddNewContactActivity.class);
                startActivity(myIntent);
            }
        });


    }

    private void LoadData() {
        options = new FirebaseRecyclerOptions.Builder<ContactDetails>().setQuery(rootReference,ContactDetails.class).build();
        adapter = new FirebaseRecyclerAdapter<ContactDetails, ContactRecyclerAdapter>(options){

            @NonNull
            @Override
            public ContactRecyclerAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact,parent,false);
                return new ContactRecyclerAdapter(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ContactRecyclerAdapter holder, final int position, @NonNull ContactDetails model) {
                holder.txtName.setText(model.getName());
                holder.txtPhoneNumber.setText(model.getPhoneNumber());
                holder.v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent  intent = new Intent(getApplicationContext(), ViewContactDetails.class);
                        intent.putExtra("ContactItem",getRef(position).getKey());
                        startActivity(intent);
                    }
                });
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }
}
