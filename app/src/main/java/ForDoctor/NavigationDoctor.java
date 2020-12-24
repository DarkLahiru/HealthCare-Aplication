package ForDoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.healthcare.PatientOrDoctorActivity;
import com.example.healthcare.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import ForDoctor.Fragments.HomeDoctor;
import de.hdodenhof.circleimageview.CircleImageView;
import fragments.AboutUsFragment;
import fragments.HomeFragment;

public class NavigationDoctor extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    CircleImageView headerImage;
    TextView headerUserName, headerEmailAddress;

    String email;
    FirebaseUser user;
    DatabaseReference rootReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_navigation);

        headerImage = headerLayout.findViewById(R.id.headerImgView);
        headerUserName = headerLayout.findViewById(R.id.txtHeaderName);
        headerEmailAddress = headerLayout.findViewById(R.id.txtHeaderEmail);

        headerDetails();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.nav_host_fragment, new HomeDoctor());
        ft.commit();
        navigationView.setCheckedItem(R.id.dashboard);
    }

    private void headerDetails() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        email = user.getEmail();

        rootReference = FirebaseDatabase.getInstance().getReference("Doctors").child(user.getUid()).child("MyProfile");
        rootReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                headerUserName.setText(Objects.requireNonNull(snapshot.child("displayName").getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        FirebaseStorage.getInstance().getReference()
                .child("Doctors")
                .child("ProfileImage")
                .child(user.getUid() + ".jpg")
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(getApplicationContext()).load(uri.toString()).resize(400, 600).centerInside().into(headerImage);
                    }
                });

        headerEmailAddress.setText(email);



    }


    public void setActionBarTitle(String title) {
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
        }
//        else {
//            super.onBackPressed();
//        }
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        NavigationView navigationView = findViewById(R.id.nav_view);

        if (id == R.id.dashboard) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.nav_host_fragment, new HomeDoctor());
            ft.commit();
            navigationView.setCheckedItem(R.id.dashboard);
        }
        if (id == R.id.signOut) {
            FirebaseAuth.getInstance().signOut();
            Intent intoLogin = new Intent(this, PatientOrDoctorActivity.class);
            startActivity(intoLogin);
        }
        return false;
    }
}
