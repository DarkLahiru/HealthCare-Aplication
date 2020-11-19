package Appointments.Booking;

import android.content.Context;
import android.content.Intent;
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

import Appointments.Booking.Interface.IRecyclerItemSelectedListener;
import Common.Common;
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
        if (!cardViewList.contains(holder.meet_location))
            cardViewList.add(holder.meet_location);

        holder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemSelectedListener(View view, int pos) {
                //for not selected card background
                for (CardView cardView:cardViewList)
                    cardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));

                //set selected card background
                holder.meet_location.setCardBackgroundColor(context.getResources().getColor(android.R.color.holo_orange_dark));

                //send local broadcast
                Intent intent = new Intent(Common.KEY_ENABLE_BUTTON_NEXT);
                intent.putExtra(Common.KEY_LOCATION_SELECTED,locationList.get(position).getLocationID());
                intent.putExtra(Common.KEY_STEP,2);
                localBroadcastManager.sendBroadcast(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtLocation , txtAddress;
        CardView meet_location;

        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtLocation = itemView.findViewById(R.id.txtLocation);
            txtAddress = itemView.findViewById(R.id.txtAddress);
            meet_location =itemView.findViewById(R.id.card_meet_location);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            iRecyclerItemSelectedListener.onItemSelectedListener(v,getAdapterPosition());
        }
    }
}
