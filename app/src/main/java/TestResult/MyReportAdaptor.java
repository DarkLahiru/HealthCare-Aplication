package TestResult;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import Appointments.Booking.Interface.IRecyclerItemSelectedListener;
import Common.Common;

public class MyReportAdaptor extends RecyclerView.Adapter<MyReportAdaptor.ReportViewHolder> {
    private Context context;
    private List<Upload> reportList;

    public MyReportAdaptor(Context context, List<Upload> reportList) {
        this.context = context;
        this.reportList = reportList;
    }

    @NonNull
    @Override
    public MyReportAdaptor.ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.row_report_list,parent,false);
        return new ReportViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull MyReportAdaptor.ReportViewHolder holder, int position) {
        holder.date.setText(reportList.get(position).getCheckedDate());
        holder.type.setText(reportList.get(position).getType());
        if (reportList.get(position).getDocumentType().equals("jpg")){
            Picasso.with(context)
                    .load(R.drawable.img)
                    .into(holder.reportImg);
        }
        else if (reportList.get(position).getDocumentType().equals("pdf")){
            Picasso.with(context)
                    .load(R.drawable.pdf)
                    .into(holder.reportImg);
        }

        holder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemSelectedListener(View view, int pos) {
                if (reportList.get(pos).getDocumentType().equals("jpg")){
                    Intent intent = new Intent(context,ReportIMGActivity.class);
                    intent.putExtra("type",reportList.get(pos).getType());
                    intent.putExtra("fileUrl",reportList.get(pos).getFileUrl());
                    intent.putExtra("note",reportList.get(pos).getNote());
                    intent.putExtra("checkedDate",reportList.get(pos).getCheckedDate());
                    context.startActivity(intent);
                }
                else if (reportList.get(pos).getDocumentType().equals("pdf")){
                    Intent intent = new Intent(context,ReportPDFActivity.class);
                    intent.putExtra("type",reportList.get(pos).getType());
                    intent.putExtra("fileUrl",reportList.get(pos).getFileUrl());
                    intent.putExtra("note",reportList.get(pos).getNote());
                    intent.putExtra("checkedDate",reportList.get(pos).getCheckedDate());
                    context.startActivity(intent);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView date,type;
        ImageView reportImg;

        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.txtReportDate);
            type = itemView.findViewById(R.id.txtReportType);
            reportImg = itemView.findViewById(R.id.imgReport);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            iRecyclerItemSelectedListener.onItemSelectedListener(v, getAdapterPosition());
        }
    }
}
