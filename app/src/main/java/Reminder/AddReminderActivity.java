package Reminder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.healthcare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Objects;

public class AddReminderActivity extends AppCompatActivity{

    //OnClickDays
    private String days;
    private String daysOfWeek = "Everyday";
    private boolean dayOfWeekList[] = new boolean[7];
    ArrayList<String> selectedDays;
    private String instructions ="No Food Instructions";

    //getTodayDate
    private String startDate;

    private TextInputLayout medicineName;
    private Button saveMedication;
    TextView textDosage, textStartTime, textStartDate;
    RadioGroup radioGroupDays;
    RadioGroup radioGroupInstructions;
    LinearLayout dosagelayout, startTimeLayout, startDateLayout;
    int medicineId;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    private String dosageUnit;
    private int startHour;
    private int startMinute;
    private int dosageValue=1;
    int currentHour, currentMinute;
    String[] dosage_options;
    String dosage;

    //Database
    DatabaseReference rootReference;
    FirebaseUser firebaseUser;
    FirebaseAuth mFirebaseAuth;

    public void initialize(){

        medicineName = (TextInputLayout) findViewById(R.id.cardview_name_text);
        dosagelayout = (LinearLayout) findViewById(R.id.layoutDosage);
        startTimeLayout = (LinearLayout) findViewById(R.id.layoutStartTime);
        startDateLayout = (LinearLayout) findViewById(R.id.layoutStartDate);
        textDosage = (TextView) findViewById(R.id.dosage);
        textStartTime = (TextView) findViewById(R.id.startTime);
        textStartDate = (TextView) findViewById(R.id.startDate);
        radioGroupDays = (RadioGroup) findViewById(R.id.radioGroupDays);
        radioGroupInstructions = (RadioGroup) findViewById(R.id.radioGroupInstructions);
        saveMedication = (Button) findViewById(R.id.buttonSaveMedication);
        Arrays.fill(dayOfWeekList, true);

    }

    public void getCurrentHourMinute(){
        Calendar calendar = Calendar.getInstance();
        currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        currentMinute = calendar.get(Calendar.MINUTE);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        initialize();

        /*Intent intent=getIntent();
        medName=intent.getStringExtra("Medicine Name");
        if(medName!=null)
            medicineName.setText(medName);*/

        dosageUnit = getResources().getStringArray(R.array.dosage_options)[0];

        textStartTime.setText(setTime(currentHour,currentMinute));
        startTimeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog timePicker;
                timePicker = new TimePickerDialog(AddReminderActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        textStartTime.setText(setTime(selectedHour, selectedMinute));
                        startHour = selectedHour;
                        startMinute = selectedMinute;
                    }
                }, hour, minute, true);//Yes 24 hour time
                timePicker.setTitle("Select Start Time");
                timePicker.show();
            }
        });
        textStartDate.setText(getTodayDate());
        startDateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentDate = Calendar.getInstance();
                int year = mcurrentDate.get(Calendar.YEAR);
                int month = mcurrentDate.get(Calendar.MONTH);
                int day = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker;
                datePicker = new DatePickerDialog(AddReminderActivity.this, new DatePickerDialog.OnDateSetListener(){

                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String strMonth="",strDay="";
                        if(month<10){
                            strMonth = "0"+month;
                        }
                        else{
                            strMonth  = ""+month;
                        }
                        if(dayOfMonth<10){
                            strDay = "0"+dayOfMonth;
                        }
                        else {
                            strDay = ""+dayOfMonth;
                        }
                        textStartDate.setText(strMonth+"/"+strDay+"/"+year);
                        startDate = strMonth+"/"+strDay+"/"+year;
                    }
                }, year, month, day);
                datePicker.setTitle("Set Start Date");
                datePicker.show();
            }
        });

        textDosage.setText(dosageValue+" "+dosageUnit);
        dosagelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDosagePicker();

            }
        });

        saveMedication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveReminderForMedication();

                //save all data
            }
        });
    }

    private void saveReminderForMedication() {
        int checkBoxCounter = 0;
        boolean cancel = false;

        if(medicineName.getEditText().getText().toString().trim().equals("")){
            medicineName.setError("Medicine name is required!");
            cancel = true;
        }

        Reminder reminder = new Reminder();
        String med_name = medicineName.getEditText().getText().toString();


        if (startHour == 0 || startMinute == 0 ||  dosageUnit.isEmpty() || instructions.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please fill all the fields!!", Toast.LENGTH_SHORT).show();
        }
        else {
            reminder.setMedicineName(med_name);
            reminder.setHour(startHour);
            reminder.setMinute(startMinute);
            reminder.setDosageQuantity(dosageValue+"");
            reminder.setDosageUnit(dosageUnit);
            reminder.setInstructions(instructions);
            //reminder.setRepeatTime(reminderTimeQuntity+" "+reminderTimeUnit);
            reminder.setDaysOfWeek(daysOfWeek);

            mFirebaseAuth = FirebaseAuth.getInstance();
            firebaseUser = mFirebaseAuth.getCurrentUser();
            rootReference = FirebaseDatabase.getInstance().getReference("Patients").child(firebaseUser.getUid()).child("Reminders");
            rootReference.push().setValue(reminder).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    task.isComplete();
                }
            });

            Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
            intent.putExtra("medicine_name", med_name);
            intent.putExtra("id",checkBoxCounter);

            pendingIntent = PendingIntent.getBroadcast(AddReminderActivity.this, checkBoxCounter, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmManager = (AlarmManager) getBaseContext().getSystemService(ALARM_SERVICE);

            Calendar calendar = Calendar.getInstance();

            calendar.set(Calendar.HOUR_OF_DAY, startHour);
            calendar.set(Calendar.MINUTE, startMinute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            long alarm_time = calendar.getTimeInMillis();
            alarmManager.set(AlarmManager.RTC_WAKEUP,alarm_time,pendingIntent);



            /*for(int i=0; i<7; i++) {
                if (dayOfWeekList[i] && med_name.length() != 0) {

                    int dayOfWeek = i;


                    Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
                    intent.putExtra("medicine_name", med_name);
                    intent.putExtra("id",checkBoxCounter);


                    pendingIntent = PendingIntent.getBroadcast(getBaseContext(), checkBoxCounter, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager = (AlarmManager) getBaseContext().getSystemService(ALARM_SERVICE);
                    checkBoxCounter++;
                    Calendar calendar = Calendar.getInstance();

                    calendar.set(Calendar.HOUR_OF_DAY, startHour);
                    calendar.set(Calendar.MINUTE, startMinute);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);

                    long alarm_time = calendar.getTimeInMillis();

                    if (calendar.before(Calendar.getInstance()))
                        alarm_time += AlarmManager.INTERVAL_DAY * 7;

                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarm_time,
                            AlarmManager.INTERVAL_DAY * 7, pendingIntent);
                }
            }*/
            if(!cancel) { // Input form is completely filled out
                Toast.makeText(getBaseContext(), "Reminder for " + med_name + " is set successfully", Toast.LENGTH_SHORT).show();
                Intent intentGo = new Intent(this, ReminderActivity.class);
                startActivity(intentGo);
                finish();
            }
        }
    }

    public String getTodayDate(){
        String strMonth="", strDay="";
        Calendar mcurrentDate = Calendar.getInstance();
        int year = mcurrentDate.get(Calendar.YEAR);
        int month = mcurrentDate.get(Calendar.MONTH);
        if(month<10)
            strMonth = "0"+month;
        else
            strMonth = ""+month;
        int day = mcurrentDate.get(Calendar.DAY_OF_MONTH);
        if(day<10)
            strDay = "0"+day;
        else
            strDay = ""+day;
        startDate =strMonth+"/"+strDay+"/"+year;
        return strMonth+"/"+strDay+"/"+year;
    }

    public String setTime(int hour, int minute) {
        String am_pm = (hour < 12) ? "am" : "pm";
        int nonMilitaryHour = hour % 12;
        if (nonMilitaryHour == 0)
            nonMilitaryHour = 12;
        String minuteWithZero;
        if (minute < 10)
            minuteWithZero = "0" + minute;
        else
            minuteWithZero = "" + minute;
        return nonMilitaryHour + ":" + minuteWithZero + am_pm;
    }

    public void onClickDays(@NonNull View view){
        final String[] daysofweek = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
        switch(view.getId())
        {
            case R.id.radioEveryDay:
                RadioButton button = (RadioButton) findViewById(R.id.radioEveryDay);
                days = button.getText().toString();
                daysOfWeek = days;
                for(int i=0;i<daysofweek.length;i++){
                    dayOfWeekList[i] = true;
                }
                break;
            case R.id.radioSpecificDay:
                for(int i=0;i<daysofweek.length;i++){
                    dayOfWeekList[i] = false;
                }
                selectedDays = new ArrayList<String>();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle(R.string.select_days);
                builder.setCancelable(true);
                builder.setMultiChoiceItems(daysofweek, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        if (isChecked) {
                            // If the user checked the item, add it to the selected items
                            selectedDays.add(daysofweek[indexSelected]);
                            if(indexSelected+1 == 7)
                                dayOfWeekList[0] = true;
                            else
                                dayOfWeekList[indexSelected+1] = true;
                        } else if (selectedDays.contains(indexSelected)) {
                            // Else, if the item is already in the array, remove it
                            selectedDays.remove(Integer.valueOf(indexSelected));
                            dayOfWeekList[indexSelected] = false;
                        }
                    }
                })
                        // Add action buttons
                        .setPositiveButton("SET", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                StringBuilder sb = new StringBuilder();
                                String separator = ",";
                                for (String s : selectedDays) {
                                    sb.append(separator).append(s);
                                }

                                daysOfWeek = sb.substring(separator.length()); // remove leading separator
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                //  Your code when user clicked on Cancel
                                dialog.dismiss();
                            }
                        });

                builder.create();
                builder.show();
                break;
        }
    }

    public void onClickInstructions(@NonNull View view){
        RadioButton button;
        switch(view.getId())
        {
            case R.id.radioBefore:
                button = (RadioButton) findViewById(R.id.radioBefore);
                instructions = button.getText().toString();
                break;
            case R.id.radioWith:
                button = (RadioButton) findViewById(R.id.radioWith);
                instructions = button.getText().toString();
                break;
            case R.id.radioAfter:
                button = (RadioButton) findViewById(R.id.radioAfter);
                instructions = button.getText().toString();
                break;
            case R.id.radioNo:
                button = (RadioButton) findViewById(R.id.radioNo);
                instructions = button.getText().toString();
                break;
        }
    }

    private void displayDosagePicker(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Dosage");
        builder.setCancelable(true);

        LayoutInflater inflater = (this).getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_dialog_dosage,null);
        NumberPicker numberPicker = (NumberPicker) layout.findViewById(R.id.numberPicker);
        loadNumberPicker(numberPicker,500);

        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                dosageValue = newVal;
            }
        });
        Spinner spinnerDosage = (Spinner) layout.findViewById(R.id.spinnerDosage);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(AddReminderActivity.this,
                R.array.dosage_options, android.R.layout.simple_spinner_item);
        dosage_options = getResources().getStringArray(R.array.dosage_options);
        spinnerDosage.setAdapter(adapter);
        spinnerDosage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dosageUnit = dosage_options[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        builder = new AlertDialog.Builder(this);
        builder.setView(layout);
        builder.setPositiveButton("SET", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dosage = dosageValue+" "+dosageUnit;
                textDosage.setText(dosage);
                dialog.dismiss();
                //  You can write the code  to save the selected item here
            }
        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //  Your code when user clicked on Cancel
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }

    public void loadNumberPicker(NumberPicker numberPicker,int size){
        String[] nums = new String[size];
        for(int i=0; i<nums.length; i++)
            nums[i] = Integer.toString(i+1);

        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(nums.length);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setDisplayedValues(nums);
        numberPicker.setValue(1);
    }



}
