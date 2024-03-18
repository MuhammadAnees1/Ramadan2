// NotificationHandler.java

package com.example.ramadan1;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationHandler {
    private static final String CHANNEL_ID = "YourChannelId";
    private static final int NOTIFICATION_ID = 100;
    // Create notification channel (Call this method once)
    public static void createNotificationChannel(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Your Channel Name",
                    NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }
    public static void showAlarmNotification(Context context) {
        SoundPlayer.play(context, R.raw.alarmsound);
        // Notification channel creation (Call this method once)
//        if (!areNotificationsEnabled(context)) {
//            Log.i("NotificationHandler", "Notifications are disabled.");
//            return; // Don't proceed with notification if disabled
//        }
        createNotificationChannel(context);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(context.getApplicationContext(), com.example.ramadan1.AlarmAlertActivity.class);
        intent.putExtra("sound", R.raw.alarmsound);
        intent.putExtra("notification_cancel", true);
        intent.putExtra("notification_id", NOTIFICATION_ID);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent activity = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Custom sound URI
        Uri customSoundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.alarmsound);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Ramadan Alarm is on")
                .setContentText("Click to open")
                .setContentIntent(activity)
                .setOngoing(true) // Makes this notification unremovable
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.baseline_notifications_active_24)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSound(customSoundUri)  // Set custom sound
                .setDefaults(NotificationCompat.DEFAULT_LIGHTS) // Add default lights to the notification

                // Add ongoing notification actions for turning off the sound
                .addAction(R.drawable.baseline_access_alarm_24, "Stop Sound", getStopSoundPendingIntent(context));

        Notification notificationPopup = notificationBuilder.build();
        notificationManager.notify(NOTIFICATION_ID, notificationPopup); // Use the consistent ID here
    }

    private static PendingIntent getStopSoundPendingIntent(Context context) {
        SoundPlayer.stop();
        return null;
    }




    public static void showMissedAlarmNotification(Context context, Alarm alarm) {
        // Notification channel creation (Call this method once)
        createNotificationChannel(context);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(context.getApplicationContext(), MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent activity = PendingIntent.getActivity(
                context,
                (int) alarm.getId(),
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext());
        Notification notification = builder
                .setSmallIcon(R.drawable.baseline_notifications_active_24)
                .setAutoCancel(true)
                .setContentTitle("Missed alarm: " + alarm.getTitle())
                .setContentText(getFormattedDate(alarm.getDate()))
                .setContentIntent(activity)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(Settings.System.DEFAULT_ALARM_ALERT_URI)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();

        notificationManager.notify((int) alarm.getId(), notification);
    }

    private static String getFormattedDate(long date) {
        // Add your date formatting logic here
        return String.valueOf(date);
    }
    public static boolean areNotificationsEnabled(Context context) {
        return NotificationManagerCompat.from(context).areNotificationsEnabled();
    }

}
