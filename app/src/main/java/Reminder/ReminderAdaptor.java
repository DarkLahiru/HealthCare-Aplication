package Reminder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.R;

import java.util.List;

public class ReminderAdaptor extends RecyclerView.Adapter<ReminderAdaptor.MyViewHolder> {
    Context context;
    List<Reminder> reminders;

    public ReminderAdaptor(Context context, List<Reminder> reminders) {
        this.context = context;
        this.reminders = reminders;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_list_layout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.medName.setText(reminders.get(position).getMedicineName());
        holder.medInstruction.setText(reminders.get(position).getInstructions());
        holder.dosage.setText(reminders.get(position).getDosageQuantity() + reminders.get(position).getDosageUnit());

    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView medName , medInstruction, dosage;
        View mView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            medName = (TextView)mView.findViewById(R.id.txtMedName);
            medInstruction = (TextView)mView.findViewById(R.id.txtInstruction);
            dosage = (TextView)mView.findViewById(R.id.txtDosage);
        }
    }
}
