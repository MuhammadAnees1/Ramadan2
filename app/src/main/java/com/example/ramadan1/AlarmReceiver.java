package com.example.ramadan1;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // Notifications are enabled, show the alarm notification
        NotificationHandler.showAlarmNotification(context);
        Alarm alarm = new Alarm(context);
        alarm.fromIntent(intent);
        // Create a PendingIntent for the Notification activity
        Intent newIntent = new Intent(context, AlarmAlertActivity.class);
        alarm.toIntent(newIntent);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                newIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        try {
            // Start the Notification activity using PendingIntent
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            Log.e("AlarmReceiver", "Error starting PendingIntent for Notification activity");
        }
    }

}
