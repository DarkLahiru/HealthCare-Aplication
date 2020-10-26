package Appointments.Booking.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.healthcare.R;

import java.security.PrivateKey;
import java.util.ArrayList;

import Appointments.Booking.MyLocationAdapter;
import Common.Common;
import Common.SpacesItemDecoration;
import ForDoctor.Appointment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BookingStep2Fragment extends Fragment {

    Unbinder unbinder;
    LocalBroadcastManager localBroadcastManager;
    @BindView(R.id.recycler_Place)
    RecyclerView recyclerPlace;

    private BroadcastReceiver locationDoneReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<Appointment> appointmentArrayList = intent.getParcelableArrayListExtra(Common.KEY_LOCATION_LOAD_DONE);
            MyLocationAdapter adapter = new MyLocationAdapter(getContext(),appointmentArrayList);
            recyclerPlace.setAdapter(adapter);
        }
    };


    static BookingStep2Fragment instance;
    public static BookingStep2Fragment getInstance() {
        if (instance == null)
            instance = new BookingStep2Fragment();
        return instance;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(locationDoneReceiver,new IntentFilter(Common.KEY_LOCATION_LOAD_DONE));
    }

    @Override
    public void onDestroy() {
        localBroadcastManager.unregisterReceiver(locationDoneReceiver);
        super.onDestroy();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View itemView = inflater.inflate(R.layout.fragment_booking_step_two,container,false);
        unbinder = ButterKnife.bind(this,itemView);
        initView();
        return itemView;
    }

    private void initView() {
        recyclerPlace.setHasFixedSize(true);
        recyclerPlace.setLayoutManager(new GridLayoutManager(getActivity(),2));
        recyclerPlace.addItemDecoration(new SpacesItemDecoration(4));
    }
}
