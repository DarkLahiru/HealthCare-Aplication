package Appointments.Pending;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.shreyaspatil.MaterialDialog.MaterialDialog;

import java.util.List;

import Appointments.Booking.Model.BookingInformation;
import Common.Common;


public class PendingTimeSlotAdapter extends RecyclerView.Adapter<PendingTimeSlotAdapter.MyViewHolder> {

    Context context;
    List<BookingInformation> timeSlotList;
    BottomSheetDialog bottomSheetDialog;
    MaterialDialog mDialog;
    Button btnCancel;
    TextView txt_booking_time_slot, txt_booking_doctor, txt_booking_address_name, txt_booking_address, txt_booking_phone_number;

    public PendingTimeSlotAdapter(Context context, List<BookingInformation> timeSlotList, BottomSheetDialog bottomSheetDialog, MaterialDialog mDialog) {
        this.context = context;
        this.timeSlotList = timeSlotList;
        this.bottomSheetDialog = bottomSheetDialog;
        this.mDialog = mDialog;
    }

    @NonNull
    @Override
    public PendingTimeSlotAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.row_time_slot, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PendingTimeSlotAdapter.MyViewHolder holder, int position) {

        int slot = Integer.parseInt(timeSlotList.get(position).getSlot().toString());
        holder.txtTimeSlot.setText(Common.convertTimeSlotToString(slot));
        holder.cardTimeSlot.setCardBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
        holder.txtAvailability.setText("Pending");
        holder.txtAvailability.setTextColor(context.getResources().getColor(android.R.color.white));
        holder.txtTimeSlot.setTextColor(context.getResources().getColor(android.R.color.white));


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View bottomSheetView = LayoutInflater.from(v.getContext())
                        .inflate(
                                R.layout.layout_bottomsheet_pending_appointment,
                                (LinearLayout) v.findViewById(R.id.LayoutBottomSheet)
                        );
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
                txt_booking_time_slot = bottomSheetView.findViewById(R.id.txt_booking_time_slot);
                txt_booking_doctor = bottomSheetView.findViewById(R.id.txt_booking_doctor);
                txt_booking_address_name = bottomSheetView.findViewById(R.id.txt_booking_address_name);
                txt_booking_address = bottomSheetView.findViewById(R.id.txt_booking_address);
                txt_booking_phone_number = bottomSheetView.findViewById(R.id.txt_booking_phone_number);

                txt_booking_time_slot.setText(timeSlotList.get(position).getTime());
                txt_booking_doctor.setText(timeSlotList.get(position).getDoctorName());
                txt_booking_address_name.setText(timeSlotList.get(position).getLocationName());
                txt_booking_address.setText(timeSlotList.get(position).getLocationAddress());
                txt_booking_phone_number.setText(timeSlotList.get(position).getDocPhone());
                btnCancel = bottomSheetView.findViewById(R.id.btnCancelAppointment);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // Show Dialog
                        mDialog.show();
                    }
                });

            }
        });


    }

    @Override
    public int getItemCount() {
        return timeSlotList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtTimeSlot, txtAvailability;
        CardView cardTimeSlot;
        View mView;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;


            txtTimeSlot = itemView.findViewById(R.id.txtTimeSlot);
            txtAvailability = itemView.findViewById(R.id.txtAvailable);
            cardTimeSlot = itemView.findViewById(R.id.card_time_slot);
        }
    }
}
