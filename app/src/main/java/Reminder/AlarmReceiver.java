package Reminder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.healthcare.R;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, @NonNull Intent intent) {
        int id = intent.getIntExtra("id", 0);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        //Intent
        Intent resultIntent = new Intent(context, AlertActivity.class);
        resultIntent.putExtra("medicine_name", intent.getStringExtra("medicine_name"));
        resultIntent.putExtra("Source", "Service");
        PendingIntent pIntent = PendingIntent.getActivity(context, id, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
/*
        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(ReminderActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        id,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
       // mBuilder.setContentIntent(resultPendingIntent);*/

        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context, "SAMPLE CHANNEL")
                        .setSmallIcon(R.drawable.reminder)
                        .setSound(sound)
                        .setContentIntent(pIntent)
                        .setContentTitle("HealthCare")
                        .setAutoCancel(true)
                        .setContentText("Take Your Medicines!\n" + intent.getStringExtra("medicine_name"));

        NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(context);
        mNotificationManager.notify(id, mBuilder.build());

    }
}
