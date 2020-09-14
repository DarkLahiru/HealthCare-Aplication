package ForDoctor.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    private List<String> mList;


    public UserAdapter(Context context, List<String> mList) {
        this.context = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_doctor, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String users  = mList.get(position);

        DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference("Patients").child(users);
        rootReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.patientName.setText(snapshot.child("MyProfile").child("displayName").getValue().toString());
                holder.patientPhone.setText(snapshot.child("MyProfile").child("phoneNum").getValue().toString());
                StorageReference storageReference = FirebaseStorage.getInstance().getReference("Patients").child("ProfileImage");
                storageReference.child(users + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(context).load(uri.toString()).resize(400, 600).centerInside().into(holder.patientFace);
                    }
                });

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BottomSheetDialog bottomSheetDialog =  new BottomSheetDialog(context,R.style.BottomSheetDialogTheme);
                        final View bottomSheetView  = LayoutInflater.from(context)
                                .inflate(
                                        R.layout.layout_bottomsheet_patient,
                                        v.findViewById(R.id.bottomSheetContainerPatient)
                                );
                        bottomSheetDialog.setContentView(bottomSheetView);
                        bottomSheetDialog.show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
}


    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView patientName, patientPhone;
        CircleImageView patientFace;
        View mView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            patientName = (TextView) mView.findViewById(R.id.name_text);
            patientPhone = (TextView) mView.findViewById(R.id.status_text);
            patientFace = (CircleImageView) mView.findViewById(R.id.profile_imageFace);
        }
    }
}
