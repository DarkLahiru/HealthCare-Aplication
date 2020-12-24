package ForDoctor.Appointments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.healthcare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import Appointments.Booking.Model.BookingInformation;
import Common.Common;

public class CheckAppointmentAdapter extends RecyclerView.Adapter<CheckAppointmentAdapter.MyViewHolder> {
    Context context;
    List<BookingInformation> appointmentList;
    DatabaseReference rootReference;

    public CheckAppointmentAdapter(Context context, List<BookingInformation> appointmentList) {
        this.context = context;
        this.appointmentList  = appointmentList;
    }

    @NonNull
    @Override
    public CheckAppointmentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.row_check_pending, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckAppointmentAdapter.MyViewHolder holder, int position) {
        int slot = Integer.parseInt(appointmentList.get(position).getSlot().toString());
        holder.txtTimeSlot.setText(Common.convertTimeSlotToString(slot));
        holder.txtDate.setText(appointmentList.get(position).getDate());

        holder.lottie_animation_cancel.setOnClickListener(new View.OnClickListener() {
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

        holder.lottie_animation_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appointmentList.get(position).setStatus("confirmed");
                rootReference = FirebaseDatabase.getInstance().getReference();
                rootReference
                        .child("Appointments")
                        .child(appointmentList.get(position).getNodeKey())
                        .setValue(appointmentList.get(position)).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(context,"Appointment Confirmed",Toast.LENGTH_SHORT).show();
                        appointmentList.remove(position);
                        notifyItemRemoved(position);
                    }
                });

            }
        });

    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtTimeSlot, txtDate;
        CardView cardPendingAppointment;
        LottieAnimationView lottie_animation_cancel, lottie_animation_confirm;
        View mView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            txtTimeSlot = itemView.findViewById(R.id.txtAppointmentSlot);
            txtDate = itemView.findViewById(R.id.txtAppointmentDate);
            cardPendingAppointment = itemView.findViewById(R.id.card_check_appointment);
            lottie_animation_cancel = itemView.findViewById(R.id.lottie_animation_cancel);
            lottie_animation_confirm = itemView.findViewById(R.id.lottie_animation_confirm);

        }
    }
}
