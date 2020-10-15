package Medicine;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.R;

class MyRecyclerAdapter extends RecyclerView.ViewHolder {
    TextView txtDate,txtReason;
    View v;
    public MyRecyclerAdapter(@NonNull View itemView) {
        super(itemView);
        txtDate = (TextView)itemView.findViewById(R.id.title);
        txtReason = (TextView)itemView.findViewById(R.id.description);
        v = itemView;
    }
}
