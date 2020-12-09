package TestResult;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthcare.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Objects;

import Profile.MyProfileActivity;


public class NewReportActivity extends AppCompatActivity {


    private static final int PICK_IMAGE_REQUEST = 1;
    DatabaseReference rootReference;
    FirebaseUser firebaseUser;
    private StorageReference mStorageRef;

    Spinner spinnerReport;
    CardView card_test_type;
    LinearLayout layout_test_type, layout_start_date, layout_choose_image;

    private String report_type;
    String[] report_types;
    Button btnReportSubmit;

    TextView textStartDate;

    private TextInputLayout reportNote;
    String note;

    private Uri mImageUri;
    private ImageView mImageView;

    ProgressDialog progressDialog;
    StorageReference fileReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_report);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        initialize();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        rootReference = FirebaseDatabase.getInstance().getReference().child("TestReports").child(firebaseUser.getUid());
        mStorageRef = FirebaseStorage.getInstance().getReference().child("TestReports").child(firebaseUser.getUid());
        progressDialog = new ProgressDialog(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.report_types, R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        report_types = getResources().getStringArray(R.array.report_types);
        spinnerReport.setAdapter(adapter);
        spinnerReport.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                report_type = report_types[ position ];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        textStartDate.setText(getTodayDate());
        layout_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentDate = Calendar.getInstance();
                int year = mcurrentDate.get(Calendar.YEAR);
                int month = mcurrentDate.get(Calendar.MONTH);
                int day = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker;
                datePicker = new DatePickerDialog(NewReportActivity.this, new DatePickerDialog.OnDateSetListener() {

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
                        textStartDate.setText(strMonth + "/" + strDay + "/" + year);
                    }
                }, year, month, day);
                datePicker.setTitle("Set Start Date");
                datePicker.show();
            }
        });
        note = Objects.requireNonNull(reportNote.getEditText()).getText().toString();

        layout_choose_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();

            }
        });

        btnReportSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });

    }

    private void uploadFile() {
        if (mImageUri != null) {
            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Upload upload = new Upload(report_type, uri.toString(), reportNote.getEditText().getText().toString(), textStartDate.getText().toString(),firebaseUser.getUid(),getFileExtension(mImageUri));
                    String uploadID = rootReference.push().getKey();
                    rootReference.child(uploadID).setValue(upload);
                    Toast.makeText(getApplicationContext(), "Upload successfully", Toast.LENGTH_LONG).show();
                    finish();
                    Intent myIntent = new Intent(getApplicationContext(), TestResultActivity.class);
                    startActivity(myIntent);


                }
            });

        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        String[] mimetypes = {"image/*", "application/pdf"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            //Picasso.with(getApplicationContext()).load(mImageUri).centerInside().into(mImageView);
            fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));
            progressDialog.setMessage("Uploading File");
            progressDialog.show();
            fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "File Ready to Upload", Toast.LENGTH_SHORT).show();
                            Picasso.with(getApplicationContext())
                                    .load(R.drawable.in_progress_128)
                                    .into(mImageView);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    public String getTodayDate() {
        String strMonth = "", strDay = "";
        Calendar mcurrentDate = Calendar.getInstance();
        int year = mcurrentDate.get(Calendar.YEAR);
        int month = mcurrentDate.get(Calendar.MONTH);
        if (month < 10)
            strMonth = "0" + month;
        else
            strMonth = "" + month;
        int day = mcurrentDate.get(Calendar.DAY_OF_MONTH);
        if (day < 10)
            strDay = "0" + day;
        else
            strDay = "" + day;
        return strMonth + "/" + strDay + "/" + year;
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void initialize() {
        layout_test_type = (LinearLayout) findViewById(R.id.layout_test_type);
        layout_start_date = (LinearLayout) findViewById(R.id.layoutCheckedDate);
        spinnerReport = (Spinner) findViewById(R.id.codeSpinner);
        card_test_type = (CardView) findViewById(R.id.card_test_type);
        btnReportSubmit = (Button) findViewById(R.id.btnReportSubmit);
        textStartDate = (TextView) findViewById(R.id.CheckedDate);
        reportNote = (TextInputLayout) findViewById(R.id.card_view_note_text);
        layout_choose_image = (LinearLayout) findViewById(R.id.layoutReportImage);
        mImageView = findViewById(R.id.imageReport);

    }
}