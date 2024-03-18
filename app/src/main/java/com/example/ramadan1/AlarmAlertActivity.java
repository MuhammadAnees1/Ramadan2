package com.example.ramadan1;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

public class AlarmAlertActivity extends Activity {
    private PowerManager.WakeLock wakeLock;
    static MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private boolean VIBRATION_IS_ENABLE = true;
    private TextView currentTimeText;
    private Integer SNOOZE_TIME = 10; // Seconds
    private Integer PLAY_SOUND_COUNT = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This will add alert to lock screen.
        addToLockScreen();

        VIBRATION_IS_ENABLE = NotificationActivity.areVibrationEnabled(getApplicationContext());

        int extraSound = getIntent().getIntExtra("sound", 0);
        if (extraSound != 0) {
            SoundPlayer.stop();
        }
        // Set view will be always below the lock screen flags

        if (getIntent().getBooleanExtra("notification_cancel", true)) {
            // Get the NotificationManager service
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // Cancel the notification with ID 100
            notificationManager.cancel(100);
            // Optionally, if you want to close the activity after cancelling the notification
        }
        setContentView(R.layout.activity_alarm_alert);

        Calendar currentTime = Calendar.getInstance();
        currentTimeText = findViewById(R.id.currentTimeText);
        updateCurrentTimeTextView(currentTime);
        try {
            // Play ringtone sound and vibration
            SoundPlayer.stop();
            playSound();
            if (VIBRATION_IS_ENABLE) {
                startVibration();
            }
            startVibration();

            // Button to stop the alarm
            Button stopAlarmButton = findViewById(R.id.stopAlarmButton);
            stopAlarmButton.setOnClickListener(v->stopAlarm());

            Button snoozeAlarmButton = findViewById(R.id.snoozeAlarmButton);
            snoozeAlarmButton.setOnClickListener(v->snoozeAlarm()); // Lambada Expression for Java v8+
            snoozeAlarmButton.setText(SNOOZE_TIME);

        } catch (Exception e) {
            e.printStackTrace(); // Log or handle the exception as needed
        }
    }


    private void startVibration(){
        if(VIBRATION_IS_ENABLE){
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            // Check if the device has a vibrator
            if (vibrator != null && vibrator.hasVibrator()) {
                long[] vibrationPattern = {0, 500, 500, 500}; // Example pattern: wait 0ms, vibrate 500ms, sleep 500ms
                vibrator.vibrate(vibrationPattern, 0); // Start repeating from the first element
            }else{
                Log.d(TAG, "startVibration: Device has no vibrator");
            }
        }
    }

    private void stopVibration(){
        if (vibrator != null) {
            vibrator.cancel(); // Cancel the vibration
        }
    }

    private void playSound(){

        // Play the alarm sound
        mediaPlayer = MediaPlayer.create(this,R.raw.alarmsound);

        mediaPlayer.setOnCompletionListener(mp -> {
            PLAY_SOUND_COUNT--; // Decrement the play count
            if (PLAY_SOUND_COUNT > 0) {
                mp.start(); // Restart the sound for the next loop
            } else {
                stopVibration(); // Optionally stop vibration when playback finally completes
                stopSound(); // Stop sound to release the sound
            }
        });

        mediaPlayer.start(); // Start Playback
    }

    private void stopSound(){
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void addToLockScreen(){

        // Make sure this activity appears over the lock screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        // Turn the screen on when this activity launches
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        // Starting from Android 27 (Android 8.1, Oreo), the following flags are needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        }
        // This is for the safety of battery draining, it ensure to hold the CPU awake for just acquired time.
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "AlarmApp::AlarmWakeLockTag");
        wakeLock.acquire(10*60*1000L /*10 minutes*/);
    }
    private void removeWakeLock(){
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }
    private void stopAlarm() {
        removeWakeLock();
        stopSound();
        stopVibration();
        finish();
    }

    private void snoozeAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, Custom_alarm_activity.alarmIdentifier, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        // Calculate the snooze time
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, SNOOZE_TIME);

        AlarmManager.AlarmClockInfo alarmClockInfo =
                new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alarmManager.setAlarmClock(alarmClockInfo, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }

        // Stop the current alarm
        stopAlarm();
    }

    private void updateCurrentTimeTextView(Calendar calendar){
        String alarmText = Custom_alarm_activity.formatTime(calendar);
        currentTimeText.setText(alarmText);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop the current alarm
        stopAlarm();
    }
}