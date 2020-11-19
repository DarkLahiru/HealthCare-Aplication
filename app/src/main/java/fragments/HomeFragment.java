package fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import Appointments.AppointmentPatientActivity;
import Contact.ContactActivity;
import com.example.healthcare.NavigationActivity;
import com.example.healthcare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ContactDoctor.CheckDoctorsActivity;
import Reminder.ReminderActivity;
import Medicine.MedicineActivity;
import Profile.MyProfileActivity;
import TestResult.TestResultActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    public HomeFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((NavigationActivity) requireActivity()).setActionBarTitle("Home");
        View view = inflater.inflate(R.layout.activity_main, container, false);


        ImageView myProfile = (ImageView) view.findViewById(R.id.myProfile);
        myProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myProfile = new Intent(getActivity(), MyProfileActivity.class);
                startActivity(myProfile);
            }
        });

        ImageView doctors = (ImageView) view.findViewById(R.id.doctos);
        doctors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent doctors = new Intent(getActivity(), CheckDoctorsActivity.class);
                startActivity(doctors);
            }
        });

        ImageView medicine = (ImageView) view.findViewById(R.id.medicine);
        medicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent medicine = new Intent(getActivity(), MedicineActivity.class);
                startActivity(medicine);
            }
        });

        ImageView appointments = (ImageView) view.findViewById(R.id.appointments);
        appointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AppointmentPatientActivity.class);
                startActivity(intent);
            }
        });

        ImageView contact = (ImageView) view.findViewById(R.id.contact);
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent contact = new Intent(getActivity(), ContactActivity.class);
                startActivity(contact);
            }
        });


        ImageView testReport = (ImageView) view.findViewById(R.id.testReport);
        testReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent testResult = new Intent(getActivity(), TestResultActivity.class);
                startActivity(testResult);
            }
        });

        ImageView reminder = (ImageView) view.findViewById(R.id.reminder);
        reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent rem = new Intent(getActivity(), ReminderActivity.class);
                startActivity(rem);
            }
        });
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
        rootReference.child("Users").child(firebaseUser.getUid()).child("First Time Login").setValue("true");
        return view;
    }
}
