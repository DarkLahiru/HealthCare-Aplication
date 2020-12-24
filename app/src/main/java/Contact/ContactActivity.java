package Contact;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Objects;

import ContactDoctor.CheckDoctorsActivity;

public class ContactActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    FirebaseRecyclerOptions<ContactDetails> options;
    FirebaseRecyclerAdapter<ContactDetails, ContactRecyclerAdapter> adapter;

    DatabaseReference rootReference;
    FirebaseUser firebaseUser;
    FirebaseAuth mFirebaseAuth;
    ExtendedFloatingActionButton fabAdd;
    ImageButton imageButton;
    EditText mSearchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorBlack));
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        mSearchText= findViewById(R.id.search_field);
        imageButton = findViewById(R.id.search_btn);
        fabAdd = findViewById(R.id.extended_fab);
        recyclerView = (RecyclerView) findViewById(R.id.rVContact);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        rootReference = FirebaseDatabase.getInstance().getReference().child("Contact Details").child(firebaseUser.getUid());

        LoadData();

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(ContactActivity.this, AddNewContactActivity.class);
                startActivity(myIntent);
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = mSearchText.getText().toString();
                searchNumber(searchText);
            }
        });


    }

    private void searchNumber(String searchText) {
        Toast.makeText(getApplicationContext(), "Searching", Toast.LENGTH_LONG).show();
        Query firebaseSearchQuery = rootReference.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff");
        options = new FirebaseRecyclerOptions.Builder<ContactDetails>().setQuery(firebaseSearchQuery, ContactDetails.class).build();
        adapter = new FirebaseRecyclerAdapter<ContactDetails, ContactRecyclerAdapter>(options) {

            @NonNull
            @Override
            public ContactRecyclerAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
                return new ContactRecyclerAdapter(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ContactRecyclerAdapter holder, final int position, @NonNull ContactDetails model) {
                holder.txtName.setText(model.getName());
                holder.txtPhoneNumber.setText(model.getPhoneNumber());
                holder.v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), ViewContactDetails.class);
                        intent.putExtra("ContactItem", getRef(position).getKey());
                        startActivity(intent);
                    }
                });

                holder.lottieAnimationView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        String p = "tel:" + model.getPhoneNumber();
                        callIntent.setData(Uri.parse(p));
                        if (ActivityCompat.checkSelfPermission(ContactActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        startActivity(callIntent);


                    }
                });

            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }

    private void LoadData() {
        options = new FirebaseRecyclerOptions.Builder<ContactDetails>().setQuery(rootReference, ContactDetails.class).build();
        adapter = new FirebaseRecyclerAdapter<ContactDetails, ContactRecyclerAdapter>(options) {

            @NonNull
            @Override
            public ContactRecyclerAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
                return new ContactRecyclerAdapter(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ContactRecyclerAdapter holder, final int position, @NonNull ContactDetails model) {
                holder.txtName.setText(model.getName());
                holder.txtPhoneNumber.setText(model.getPhoneNumber());
                holder.v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), ViewContactDetails.class);
                        intent.putExtra("ContactItem", getRef(position).getKey());
                        startActivity(intent);
                    }
                });

                holder.lottieAnimationView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        String p = "tel:" + model.getPhoneNumber();
                        callIntent.setData(Uri.parse(p));
                        if (ActivityCompat.checkSelfPermission(ContactActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        startActivity(callIntent);


                    }
                });

            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }
}
