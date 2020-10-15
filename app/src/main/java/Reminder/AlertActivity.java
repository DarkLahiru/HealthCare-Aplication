package Reminder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import Reminder.Fragments.AlertReminderDialogFragment;

public class AlertActivity extends FragmentActivity {
    PendingIntent pendingIntent;
    AlarmManager alarmManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlertReminderDialogFragment alertReminder = new AlertReminderDialogFragment();

        /** Opening the Alert Dialog Window. This will be opened when the alarm goes off */
        alertReminder.show(getSupportFragmentManager(), "AlertAlarm");
    }

    public void doNeutralClick(String medicineName){
        final int _id = (int) System.currentTimeMillis();
        final long minute = 60000;
        long snoozeLength = 1;
        long currTime = System.currentTimeMillis();
        long min = currTime + minute * snoozeLength;

        Intent intent = new Intent(getBaseContext(), AlertActivity.class);
        intent.putExtra("medicine_name", medicineName);

        pendingIntent = PendingIntent.getActivity(getBaseContext(), _id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager = (AlarmManager) getBaseContext().getSystemService(ALARM_SERVICE);

        assert alarmManager != null;
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, min, pendingIntent);
        Toast.makeText(getBaseContext(), "Alarm for " + medicineName + " was snoozed for 1 minute", Toast.LENGTH_SHORT).show();

        finish();

    }

    public void doPositiveClick(String medicationName){

        Calendar takeTime = Calendar.getInstance();
        int date = takeTime.get(Calendar.DATE);
        int hour = takeTime.get(Calendar.HOUR_OF_DAY);
        int minute = takeTime.get(Calendar.MINUTE);
        String am_pm = (hour < 12) ? "am" : "pm";
        String stringMinute;
        if (minute < 10)
            stringMinute = "0" + minute;
        else
            stringMinute = "" + minute;

        int nonMilitaryHour = hour % 12;
        if (nonMilitaryHour == 0)
            nonMilitaryHour = 12;
        Toast.makeText(getBaseContext(),  medicationName + " was taken at "+ nonMilitaryHour + ":" + stringMinute + " " + am_pm + ".", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getBaseContext(), ReminderActivity.class);
        startActivity(intent);
        finish();
    }

    public void doNegativeClick(){
        finish();
    }
}
