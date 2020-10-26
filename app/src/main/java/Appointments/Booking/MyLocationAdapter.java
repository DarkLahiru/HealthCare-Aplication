package Appointments.Booking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.R;

import java.util.ArrayList;
import java.util.List;

import ForDoctor.Appointment;

public class MyLocationAdapter extends RecyclerView.Adapter<MyLocationAdapter.MyViewHolder> {

    Context context;
    List<Appointment> locationList;
    List<CardView> cardViewList;
    LocalBroadcastManager localBroadcastManager;


    public MyLocationAdapter(Context context, List<Appointment> locationList) {
        this.context = context;
        this.locationList = locationList;
        cardViewList = new ArrayList<>();
        localBroadcastManager =LocalBroadcastManager.getInstance(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.row_meet_location,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.txtLocation.setText(locationList.get(position).getLocationName());
        holder.txtAddress.setText(locationList.get(position).getLocationAddress());
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtLocation , txtAddress;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtLocation = itemView.findViewById(R.id.txtLocation);
            txtAddress = itemView.findViewById(R.id.txtAddress);

        }
    }
}
