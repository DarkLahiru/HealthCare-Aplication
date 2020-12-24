package Appointments.Confirmed;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import Appointments.Booking.Model.BookingInformation;
import Common.Common;
import ContactDoctor.MessageActivity;
import ForDoctor.Messages.DocMessagesActivity;

public class ConfirmedTimeSlotAdapter extends RecyclerView.Adapter<ConfirmedTimeSlotAdapter.MyViewHolder> {

    Context context;
    List<BookingInformation> timeSlotList;
    BottomSheetDialog bottomSheetDialog;
    TextView txt_booking_time_slot, txt_booking_doctor, txt_booking_address_name, txt_booking_address, txt_booking_phone_number;
    Button btnContactDoctor;




    public ConfirmedTimeSlotAdapter(Context context, List<BookingInformation> timeSlotList, BottomSheetDialog bottomSheetDialog) {
        this.context = context;
        this.timeSlotList = timeSlotList;
        this.bottomSheetDialog = bottomSheetDialog;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.row_time_slot, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        int slot = Integer.parseInt(timeSlotList.get(position).getSlot().toString());
        holder.txtTimeSlot.setText(Common.convertTimeSlotToString(slot));
        holder.cardTimeSlot.setCardBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
        holder.txtAvailability.setText("Confirmed");
        holder.txtAvailability.setTextColor(context.getResources().getColor(android.R.color.white));
        holder.txtTimeSlot.setTextColor(context.getResources().getColor(android.R.color.white));


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View bottomSheetView = LayoutInflater.from(v.getContext())
                        .inflate(
                                R.layout.layout_bottomsheet_confirmed_appointment,
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

                btnContactDoctor= bottomSheetView.findViewById(R.id.btnContactDoctor);

                btnContactDoctor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contactDoctor(timeSlotList.get(position));
                    }
                });


            }
        });
    }

    private void contactDoctor(BookingInformation bookingInformation) {

        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("ChatList").child(bookingInformation.getPatientID()).child(bookingInformation.getDoctorID());
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    chatRef.child("id").setValue(bookingInformation.getDoctorID());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        final DatabaseReference chatDocRef =FirebaseDatabase.getInstance().getReference("ChatList").child(bookingInformation.getDoctorID()).child(bookingInformation.getPatientID());
        chatDocRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    chatDocRef.child("id").setValue(bookingInformation.getPatientID());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Intent intent = new Intent(context, MessageActivity.class);
        intent.putExtra("docID",bookingInformation.getDoctorID());
        context.startActivity(intent);
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
