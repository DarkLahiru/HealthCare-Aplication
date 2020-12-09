package TestResult;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.healthcare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import Common.SpacesItemDecoration;
import Reminder.AddReminderActivity;
import co.ceryle.radiorealbutton.RadioRealButton;
import co.ceryle.radiorealbutton.RadioRealButtonGroup;

public class StoredReportActivity extends AppCompatActivity {

    Spinner spinner;
    EditText searchField;
    RadioRealButton radioDate, radioType;
    RadioRealButtonGroup radioGroup;
    private String report_type = "";;
    String[] report_types;
    RecyclerView rVReport;
    private List<Upload> reportList;
    FirebaseUser firebaseUser;
    DatabaseReference rootReference;
    private MyReportAdaptor myReportAdaptor;

    ImageView btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stored_report);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        initialize();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rVReport.setLayoutManager(layoutManager);


        loadData();
        searchField.setCursorVisible(false);
        searchField.setKeyListener(null);
        searchField.requestFocus();
        searchField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentDate = Calendar.getInstance();
                int year = mcurrentDate.get(Calendar.YEAR);
                int month = mcurrentDate.get(Calendar.MONTH);
                int day = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker;
                datePicker = new DatePickerDialog(StoredReportActivity.this, new DatePickerDialog.OnDateSetListener() {

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String strMonth = "", strDay = "";
                        if (month < 10) {
                            strMonth = "0" + month;
                        } else {
                            strMonth = "" + month;
                        }
                        if (dayOfMonth < 10) {
                            strDay = "0" + dayOfMonth;
                        } else {
                            strDay = "" + dayOfMonth;
                        }
                        searchField.setText(strMonth + "/" + strDay + "/" + year);
                    }
                }, year, month, day);
                datePicker.setTitle("Set Start Date");
                datePicker.show();
            }
        });
        radioGroup.setOnClickedButtonListener(new RadioRealButtonGroup.OnClickedButtonListener() {
            @Override
            public void onClickedButton(RadioRealButton button, int position) {
                if (radioDate.isChecked()) {
                    searchField.setEnabled(true);
                    searchField.setHint("Select Date");
                    searchField.setText("");
                    searchField.requestFocus();
                    spinner.setVisibility(View.GONE);
                    report_type = "";


                }
                if (radioType.isChecked()) {
                    searchField.setEnabled(false);
                    searchField.setHint("");
                    searchField.setText("");

                    spinner.setVisibility(View.VISIBLE);
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(StoredReportActivity.this,
                            R.array.report_types, android.R.layout.simple_spinner_item);

                    adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                    report_types = getResources().getStringArray(R.array.report_types);
                    spinner.setAdapter(adapter);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            report_type = report_types[ position ];
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                }
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(searchField.getText().toString())) {
                    searchDataByDate(searchField.getText().toString());
                    searchField.setText("");
                }

                if (!report_type.isEmpty()) {
                    searchDataByType(report_type);
                    report_type = "";
                }

            }
        });


    }

    private void loadData() {
        reportList = new ArrayList<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        rootReference = FirebaseDatabase.getInstance().getReference().child("TestReports").child(firebaseUser.getUid());
        rootReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Upload upload = dataSnapshot.getValue(Upload.class);
                    reportList.add(upload);
                }
                myReportAdaptor = new MyReportAdaptor(StoredReportActivity.this, reportList);
                rVReport.setAdapter(myReportAdaptor);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void searchDataByDate(String search) {
        List<Upload> reportByDate = new ArrayList<>();
        try {
            for (int i = 0; i < reportList.size(); i++) {
                if (reportList.get(i).getCheckedDate().equals(search)) {
                    reportByDate.add(reportList.get(i));
                }
            }
            myReportAdaptor = new MyReportAdaptor(StoredReportActivity.this, reportByDate);
            rVReport.setAdapter(myReportAdaptor);
        } catch (Exception e) {
            Log.d("Exception!", Objects.requireNonNull(e.getLocalizedMessage()));
        }

    }

    private void searchDataByType(String search) {
        List<Upload> reportByType = new ArrayList<>();
        try {
            for (int i = 0; i < reportList.size(); i++) {
                if (reportList.get(i).getType().equals(search)) {
                    reportByType.add(reportList.get(i));
                }
            }
            myReportAdaptor = new MyReportAdaptor(StoredReportActivity.this, reportByType);
            rVReport.setAdapter(myReportAdaptor);
        } catch (Exception e) {
            Log.d("Exception!", Objects.requireNonNull(e.getLocalizedMessage()));
        }
    }


    private void initialize() {
        spinner = findViewById(R.id.spinnerType);
        searchField = findViewById(R.id.search_field);
        radioType = findViewById(R.id.radioType);
        radioDate = findViewById(R.id.radioDate);
        radioGroup = findViewById(R.id.radio_group);
        rVReport = findViewById(R.id.rVReport_List);
        btnSearch = findViewById(R.id.btn_search_report);

    }
}