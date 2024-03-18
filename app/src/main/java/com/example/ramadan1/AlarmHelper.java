package com.example.ramadan1;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import java.util.Calendar;

public class AlarmHelper {

    @SuppressLint("ScheduleExactAlarm")
    public static void setupAlarmWithVibration(Context context, Calendar calendar, int uniqueCode) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Unique requestCode for each alarm
        int requestCode = uniqueCode;

        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Acquire a wake lock to ensure the alarm goes off even if the phone is locked
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp:WakeLock");
        wakeLock.acquire();

        try {
            // Set the alarm for the specified time
            AlarmManager.AlarmClockInfo alarmClockInfo =
                    new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), pendingIntent);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                alarmManager.setAlarmClock(alarmClockInfo, pendingIntent);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }

            // Save alarm set time to SharedPreferences
            context.getSharedPreferences(context.getPackageName() + "AlarmOpenSourceApp", Context.MODE_PRIVATE)
                    .edit()
                    .putLong("alarmTime" + uniqueCode, calendar.getTimeInMillis())
                    .apply();

        } catch (Exception e) {
            Log.e("AlarmHelper", "Error setting alarm: " + e.getMessage());
        } finally {
            // Release the wake lock once the alarm is set
            wakeLock.release();
        }
    }

    public static void cancelAlarm(Context context, int uniqueCode) {
        int requestCode = uniqueCode;

        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);

        if (pendingIntent != null) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();

            // Clear saved alarm time from SharedPreferences
            context.getSharedPreferences(context.getPackageName() + "AlarmOpenSourceApp", Context.MODE_PRIVATE)
                    .edit()
                    .remove("alarmTime" + uniqueCode)
                    .apply();
        }
    }
}
