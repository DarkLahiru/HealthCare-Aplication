package fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import Contact.ContactActivity;
import com.example.healthcare.NavigationActivity;
import com.example.healthcare.R;

import medicine.MedicineActivity;
import profile.MyProfileActivity;


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

        ImageView medicine = (ImageView) view.findViewById(R.id.medicine);
        medicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent medicine = new Intent(getActivity(), MedicineActivity.class);
                startActivity(medicine);
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

        /*ImageView testReport = (ImageView) view.findViewById(R.id.testReport);
        testReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent contact = new Intent(getActivity(), TestingActivity.class);
                startActivity(contact);
            }
        });*/

        /*ImageView reminder = (ImageView) view.findViewById(R.id.reminder);
        reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent rem = new Intent(getActivity(), MedicineRemActivity.class);
                startActivity(rem);
            }
        });*/
        return view;
    }
}
