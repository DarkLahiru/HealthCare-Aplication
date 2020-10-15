package Reminder.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;


import java.io.IOException;
import java.util.Objects;


import Reminder.AlertActivity;

public class AlertReminderDialogFragment extends DialogFragment {
    private MediaPlayer mMediaPlayer;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("HealthCare");
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(getActivity(),sound);
            mMediaPlayer.prepare(); // might take long! (for buffering, etc)
        } catch (IOException e) {
            e.printStackTrace();
    }

        mMediaPlayer.start();


        setCancelable(false);

        final String pill_name = getActivity().getIntent().getStringExtra("medicine_name");

        builder.setMessage("Did you take your "+ pill_name + " ?");

        builder.setPositiveButton("I took it", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mMediaPlayer.stop();

                AlertActivity act = (AlertActivity)getActivity();
                assert act != null;
                act.doPositiveClick(pill_name);
                getActivity().finish();
            }
        });

        builder.setNeutralButton("Snooze", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if(mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
                //}
                AlertActivity act = (AlertActivity)getActivity();
                assert act != null;
                act.doNeutralClick(pill_name);
                getActivity().finish();
            }
        });

        builder.setNegativeButton("I won't take", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if(mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
                //}
                AlertActivity act = (AlertActivity)getActivity();
                assert act != null;
                act.doNegativeClick();
                getActivity().finish();
            }
        });

        return builder.create();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().finish();
    }
}
