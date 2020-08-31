package Contact;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.R;

class ContactRecyclerAdapter extends RecyclerView.ViewHolder {
    TextView txtName,txtPhoneNumber;
    View v;
    public ContactRecyclerAdapter(@NonNull View itemView) {
        super(itemView);
        txtName = (TextView)itemView.findViewById(R.id.contactName);
        txtPhoneNumber = (TextView)itemView.findViewById(R.id.contactNumber);
        v = itemView;
    }
}
