package ForDoctor.Appointments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.healthcare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
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
import ForDoctor.Messages.DocMessagesActivity;

public class EventOnSelectedDayAdapter extends RecyclerView.Adapter<EventOnSelectedDayAdapter.MyViewHolder> {

    Context context;
    List<BookingInformation> eventOnSelectedDay;
    BottomSheetDialog bottomSheetDialog;
    TextView txt_booking_time_slot, txt_booking_patient, txt_booking_phone_number;
    LottieAnimationView lottie_animation_cancel, lottie_animation_confirm;
    Button btn_contact;
    DatabaseReference rootReference;



    LinearLayout layout_confirm_appointment, layout_btn_contact;


    public EventOnSelectedDayAdapter(Context context, List<BookingInformation> eventOnSelectedDay, BottomSheetDialog bottomSheetDialog) {
        this.context = context;
        this.eventOnSelectedDay = eventOnSelectedDay;
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
        int slot = Integer.parseInt(eventOnSelectedDay.get(position).getSlot().toString());
        holder.txtTimeSlot.setText(Common.convertTimeSlotToString(slot));
        holder.cardTimeSlot.setCardBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
        holder.txtAvailability.setText(eventOnSelectedDay.get(position).getStatus());
        holder.txtAvailability.setTextColor(context.getResources().getColor(android.R.color.white));
        holder.txtTimeSlot.setTextColor(context.getResources().getColor(android.R.color.white));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View bottomSheetView = LayoutInflater.from(v.getContext())
                        .inflate(
                                R.layout.layout_bottomsheet_for_doctor,
                                (LinearLayout) v.findViewById(R.id.LayoutBSForDoc)
                        );
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
                initializeBottomSheetDialog(bottomSheetView);


                txt_booking_time_slot.setText(eventOnSelectedDay.get(position).getTime());
                rootReference = FirebaseDatabase.getInstance().getReference()
                        .child("Patients")
                        .child(eventOnSelectedDay.get(position).getPatientID())
                        .child("MyProfile");
                rootReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        txt_booking_patient.setText(snapshot.child("fullName").getValue().toString());
                        txt_booking_phone_number.setText(snapshot.child("phoneNum").getValue().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                if (eventOnSelectedDay.get(position).getStatus().equalsIgnoreCase("pending")) {
                    layout_confirm_appointment.setVisibility(View.VISIBLE);
                    layout_btn_contact.setVisibility(View.GONE);
                    cancelOrConfirm(eventOnSelectedDay.get(position));
                } else {
                    layout_btn_contact.setVisibility(View.VISIBLE);
                    layout_confirm_appointment.setVisibility(View.GONE);
                    btn_contact.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            contactPatient(eventOnSelectedDay.get(position));
                        }
                    });
                }



            }
        });
    }

    private void contactPatient(BookingInformation bookingInformation) {
        final DatabaseReference chatRef =FirebaseDatabase.getInstance().getReference("ChatList").child(bookingInformation.getPatientID()).child(bookingInformation.getDoctorID());
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

        Intent intent = new Intent(context, DocMessagesActivity.class);
        intent.putExtra("patientID",bookingInformation.getPatientID());
        context.startActivity(intent);

    }

    private void cancelOrConfirm(BookingInformation bookingInformation) {
        lottie_animation_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(context);
                View promptsCancelView = li.inflate(R.layout.popup_appointment_cancelation, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setView(promptsCancelView);

                final TextInputLayout noteForPatient = promptsCancelView.findViewById(R.id.txtCancellationNote);
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        String patientNote = noteForPatient.getEditText().getText().toString();
                                        if (!TextUtils.isEmpty(patientNote)) {

                                        }
                                    }
                                })
                        .setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        lottie_animation_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookingInformation.setStatus("confirmed");
                rootReference = FirebaseDatabase.getInstance().getReference();
                rootReference
                        .child("Appointments")
                        .child(bookingInformation.getNodeKey())
                        .setValue(bookingInformation).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(context,"Appointment Confirmed",Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                    }
                });

            }
        });

    }

    private void initializeBottomSheetDialog(@NonNull View bottomSheetView) {
        txt_booking_time_slot = bottomSheetView.findViewById(R.id.txt_booking_time_slot);
        txt_booking_patient = bottomSheetView.findViewById(R.id.txt_booking_patient);
        txt_booking_phone_number = bottomSheetView.findViewById(R.id.txt_booking_phone_number);
        layout_confirm_appointment = bottomSheetView.findViewById(R.id.layout_confirm_appointment);
        layout_btn_contact = bottomSheetView.findViewById(R.id.layout_btn_contact);
        lottie_animation_cancel = bottomSheetView.findViewById(R.id.lottie_animation_cancel);
        lottie_animation_confirm = bottomSheetView.findViewById(R.id.lottie_animation_confirm);
        btn_contact  = bottomSheetView.findViewById(R.id.btnContactPatient);
    }

    @Override
    public int getItemCount() {
        return eventOnSelectedDay.size();
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
