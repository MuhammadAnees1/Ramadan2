package com.softwareflare.QiblaPrayer;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    private static final int ALARM_CODE = 123;

    @Override
    public void onReceive(Context context, Intent intent) {

        // Notifications are enabled, show the alarm notification
        NotificationHandler.showAlarmNotification(context);
        Alarm alarm = new Alarm(context);
        alarm.fromIntent(intent);
        Calendar calendar = (Calendar) intent.getSerializableExtra("date");
        Log.d(TAG, "onReceive: "+ calendar);
        // Reset the alarm for the next day with the unique code
        int uniqueCode = intent.getIntExtra("uniqueCode", -1);
        if (uniqueCode != -1 && calendar != null) {
            resetAlarmForNextDay(context,calendar, uniqueCode);
        } else {
            Log.e(TAG, "Unique code not found in intent extras" + uniqueCode);
        }

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

    private void resetAlarmForNextDay(Context context,Calendar calendar, int uniqueCode) {
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        Log.d(TAG, "agya "+hours+" "+minutes);
        // Creating a Calendar instance for the next day
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, 0);

        // Setting up the alarm for the next day with the unique code
        AlarmHelper.setupAlarmWithVibration(context, calendar, uniqueCode);
    }
}
