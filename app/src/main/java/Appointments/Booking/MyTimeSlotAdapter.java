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
import Appointments.Booking.Model.TimeSlot;
import Common.Common;

public class MyTimeSlotAdapter extends RecyclerView.Adapter<MyTimeSlotAdapter.MyViewHolder> {
    Context context;
    List<TimeSlot> timeSlotList;
    List<CardView> cardViewList;
    LocalBroadcastManager localBroadcastManager;

    public MyTimeSlotAdapter(Context context) {
        this.context = context;
        this.timeSlotList = new ArrayList<>();
        this.localBroadcastManager = LocalBroadcastManager.getInstance(context);
        cardViewList = new ArrayList<>();
    }

    public MyTimeSlotAdapter(Context context, List<TimeSlot> timeSlotList) {
        this.context = context;
        this.timeSlotList = timeSlotList;
        this.localBroadcastManager = LocalBroadcastManager.getInstance(context);
        cardViewList = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.row_time_slot, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.txtTimeSlot.setText(Common.convertTimeSlotToString(position));
        if (timeSlotList.size() == 0) {
            holder.txtAvailability.setText("Available");
            holder.txtAvailability.setTextColor(context.getResources().getColor(android.R.color.black));
            holder.txtTimeSlot.setTextColor(context.getResources().getColor(android.R.color.black));
            holder.cardTimeSlot.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));
        } else {
            for (TimeSlot slotValue : timeSlotList) {


                int slot = Integer.parseInt(slotValue.getSlot().toString());
                if (slot == position) {
                    holder.cardTimeSlot.setTag(Common.DISABLE_TAG);
                    holder.cardTimeSlot.setCardBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
                    holder.txtAvailability.setText("Full");
                    holder.txtAvailability.setTextColor(context.getResources().getColor(android.R.color.white));
                    holder.txtTimeSlot.setTextColor(context.getResources().getColor(android.R.color.white));
                }
            }
        }
        if (!cardViewList.contains(holder.cardTimeSlot))
            cardViewList.add(holder.cardTimeSlot);

        holder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemSelectedListener(View view, int pos) {
                for (CardView cardView : cardViewList) {
                    if (cardView.getTag() == null)
                        cardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));

                }
                holder.cardTimeSlot.setCardBackgroundColor(context.getResources().getColor(android.R.color.holo_orange_dark));
                Intent intent = new Intent(Common.KEY_ENABLE_BUTTON_NEXT);
                intent.putExtra(Common.KEY_TIME_SLOT,position);
                intent.putExtra(Common.KEY_STEP,3);
                localBroadcastManager.sendBroadcast(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return Common.TIME_SLOT_TOTAL;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtTimeSlot, txtAvailability;
        CardView cardTimeSlot;

        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTimeSlot = itemView.findViewById(R.id.txtTimeSlot);
            txtAvailability = itemView.findViewById(R.id.txtAvailable);
            cardTimeSlot = itemView.findViewById(R.id.card_time_slot);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            iRecyclerItemSelectedListener.onItemSelectedListener(v, getAdapterPosition());
        }
    }
}
