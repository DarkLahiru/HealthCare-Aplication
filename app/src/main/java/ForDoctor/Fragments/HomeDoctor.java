package ForDoctor.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.healthcare.R;

import ForDoctor.MyProfile.MyProfileDoctorActivity;
import ForDoctor.NavigationDoctor;

public class HomeDoctor extends Fragment {

    public HomeDoctor() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((NavigationDoctor) requireActivity()).setActionBarTitle("Home");
        View view = inflater.inflate(R.layout.fragment_home_doctor, container, false);

        ImageView myProfile = (ImageView) view.findViewById(R.id.myProfileDoctor);
        myProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myProfile = new Intent(getActivity(), MyProfileDoctorActivity.class);
                startActivity(myProfile);
            }
        });

        /*ImageView patient = (ImageView) view.findViewById(R.id.imgPatient);
        patient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent patient = new Intent(getActivity(), CheckDoctorsActivity.class);
                startActivity(patient);
            }
        });

        ImageView message = (ImageView) view.findViewById(R.id.imgMessage);
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent message = new Intent(getActivity(), MedicineActivity.class);
                startActivity(message);
            }
        });

        ImageView appointments = (ImageView) view.findViewById(R.id.imgAppointments);
        appointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent appointments = new Intent(getActivity(), ContactActivity.class);
                startActivity(appointments);
            }
        });*/
        return view;
    }
}
