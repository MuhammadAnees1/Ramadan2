package com.example.ramadan1;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.util.Calendar;

public class Custom_alarm_activity extends AppCompatActivity {
    ImageView back_button1;
    private TextView alarmTimeTextView;
    private TextView currentTimeText;
    AppCompatButton cancelAlarmButton,setAlarmButton;
    private CheckBox[] dayCheckboxes;

    // It the number or identifier
    // for alarm 1 you can use 100 for alarm 2 you can use like 150 or whatever you want.
    // you can also use 1, for alarm 1, 2 for alarm 2 and so on
    public static Integer alarmIdentifier = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_alarm);
        cancelAlarmButton = findViewById(R.id.cancelAlarmButton);
        TimePicker timePicker = findViewById(R.id.timePicker);
        setAlarmButton = findViewById(R.id.setAlarmButton);
        alarmTimeTextView = findViewById(R.id.alarmTimeTextView);

        back_button1 = findViewById(R.id.back_button1);
        dayCheckboxes = new CheckBox[7];
        dayCheckboxes[0] = findViewById(R.id.sundayCheckbox);
        dayCheckboxes[1] = findViewById(R.id.mondayCheckbox);
        dayCheckboxes[2] = findViewById(R.id.tuesdayCheckbox);
        dayCheckboxes[3] = findViewById(R.id.wednesdayCheckbox);
        dayCheckboxes[4] = findViewById(R.id.thursdayCheckbox);
        dayCheckboxes[5] = findViewById(R.id.fridayCheckbox);
        dayCheckboxes[6] = findViewById(R.id.saturdayCheckbox);

        back_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Check and request permission for exact alarms on Android 12 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !getSystemService(AlarmManager.class).canScheduleExactAlarms()) {
            Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            startActivity(intent);
        }

        Calendar currentTime = Calendar.getInstance();
        currentTimeText = findViewById(R.id.currentTimeText);
        updateCurrentTimeTextView(currentTime);


        updateOldAlarmOnReopen();

        setAlarmButton.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
            calendar.set(Calendar.MINUTE, timePicker.getMinute());
//            calendar.set(Calendar.SECOND, 0); // It will set 0 seconds, like 10:05:00 PM

            // Add 15 seconds for testing purpose
            calendar.add(Calendar.SECOND, 15);

            // Add a custom day to the calendar
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

            // Check which days are selected for repeat alarm
            boolean[] repeatDays = new boolean[8];
            repeatDays[Calendar.SUNDAY] = dayCheckboxes[0].isChecked();
            repeatDays[Calendar.MONDAY] = dayCheckboxes[1].isChecked();
            repeatDays[Calendar.TUESDAY] = dayCheckboxes[2].isChecked();
            repeatDays[Calendar.WEDNESDAY] = dayCheckboxes[3].isChecked();
            repeatDays[Calendar.THURSDAY] = dayCheckboxes[4].isChecked();
            repeatDays[Calendar.FRIDAY] = dayCheckboxes[5].isChecked();
            repeatDays[Calendar.SATURDAY] = dayCheckboxes[6].isChecked();

            // Set repeat alarms for selected days
            for (int i = 0; i < repeatDays.length; i++) {
                if (repeatDays[i]) {
                    Calendar dayOfWeek = (Calendar) calendar.clone();
                    // Set the alarm for the next occurrence of the selected day
                    while (dayOfWeek.get(Calendar.DAY_OF_WEEK) != i + 1) {
                        dayOfWeek.add(Calendar.DAY_OF_MONTH, 1);
                    }
                    if (dayOfWeek.after(now)) {
                        // Call setupAlarmWithVibration method from AlarmHelper
                         setAlarm(calendar.getTimeInMillis());

                        updateAlarmTextView(dayOfWeek);

                    } else {
                        Toast.makeText(Custom_alarm_activity.this, "Cannot set alarm for past time.", Toast.LENGTH_SHORT).show();
                        Log.d("Custom_alarm_activity", "Attempted to set an alarm for the past. Operation cancelled.");
                    }
                }
            }

            setAlarmButton.setVisibility(View.GONE);
                    cancelAlarmButton.setVisibility(View.VISIBLE);
        });


//        setAlarmButton.setOnClickListener(v -> {
//            Calendar now = Calendar.getInstance();
//            Calendar calendar = Calendar.getInstance();
//            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
//            calendar.set(Calendar.MINUTE, timePicker.getMinute());
//            calendar.set(Calendar.SECOND, 0); // It will set 0 seconds, like 10:05:00 PM
//
//            // Add 15 seconds for testing purpose
//            calendar.add(Calendar.SECOND, 15);
//
//            // Add a custom day to the calendar
//            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
//            calendar.add(Calendar.DAY_OF_MONTH, 14);
//
//            // Check which days are selected for repeat alarm
//            boolean[] repeatDays = new boolean[8];
//            repeatDays[Calendar.SUNDAY] = dayCheckboxes[0].isChecked();
//            repeatDays[Calendar.MONDAY] = dayCheckboxes[1].isChecked();
//            repeatDays[Calendar.TUESDAY] = dayCheckboxes[2].isChecked();
//            repeatDays[Calendar.WEDNESDAY] = dayCheckboxes[3].isChecked();
//            repeatDays[Calendar.THURSDAY] = dayCheckboxes[4].isChecked();
//            repeatDays[Calendar.FRIDAY] = dayCheckboxes[5].isChecked();
//            repeatDays[Calendar.SATURDAY] = dayCheckboxes[6].isChecked();
//
//            // Set repeat alarms for selected days
//            for (int i = 0; i < repeatDays.length; i++) {
//                if (repeatDays[i]) {
//                    Calendar dayOfWeek = (Calendar) calendar.clone();
//                    // Set the alarm for the next occurrence of the selected day
//                    while (dayOfWeek.get(Calendar.DAY_OF_WEEK) != i + 1) {
//                        dayOfWeek.add(Calendar.DAY_OF_MONTH, 1);
//                    }
//                    if (dayOfWeek.after(now)) {
//                        // Call setupAlarmWithVibration method from AlarmHelper
//                        AlarmHelper.setupAlarmWithVibration(Custom_alarm_activity.this, dayOfWeek, alarmIdentifier);
//                        updateAlarmTextView(dayOfWeek);
//
//                        Log.d("Custom_alarm_activity", "Alarm set for: " + formatTime(dayOfWeek));
//                    } else {
//                        Toast.makeText(Custom_alarm_activity.this, "Cannot set alarm for past time.", Toast.LENGTH_SHORT).show();
//                        Log.d("Custom_alarm_activity", "Attempted to set an alarm for the past. Operation cancelled.");
//                    }
//                }
//            }
//            cancelAlarmButton.setVisibility(View.VISIBLE);
//        });


        cancelAlarmButton.setOnClickListener(v -> {
            AlarmHelper.cancelAlarm(this, alarmIdentifier);
            cancelAlarmButton.setVisibility(View.GONE);
            setAlarmButton.setVisibility(View.VISIBLE);
        });
    }

    private void setAlarm(long timeInMillis) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarmIdentifier, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);

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
        getSharedPreferences(getPackageName()+"AlarmOpenSourceApp", MODE_PRIVATE)
                .edit()
                .putLong("alarmTime" + alarmIdentifier, timeInMillis)
                .apply();
    }
    private void cancelAlarm() {
        cancelAlarmButton.setVisibility(View.GONE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarmIdentifier, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        // Clear saved alarm time from SharedPreferences
        getSharedPreferences(getPackageName() + "AlarmOpenSourceApp", MODE_PRIVATE)
                .edit()
                .remove("alarmTime" + alarmIdentifier)
                .apply();

        alarmTimeTextView.setText("Alarm not set");
    }
    private void updateOldAlarmOnReopen(){

        // Check if there is a saved alarm time and display it
        long savedAlarmTime = getSharedPreferences(getPackageName()+"AlarmOpenSourceApp", MODE_PRIVATE).getLong("alarmTime" + alarmIdentifier, 0);

        if (savedAlarmTime != 0) {
            // There is a saved alarm time, update the TextView
            Calendar alarmTime = Calendar.getInstance();
            alarmTime.setTimeInMillis(savedAlarmTime);
            updateAlarmTextView(alarmTime);
        }
    }
    private void updateCurrentTimeTextView(Calendar calendar){
        String alarmText = formatTime(calendar);
        currentTimeText.setText(alarmText);
    }
    private void updateAlarmTextView(Calendar calendar) {
        String alarmText = "Alarm set for: " + formatTime(calendar);
        alarmTimeTextView.setText(alarmText);
    }
    static String formatTime(Calendar calendar) {
        // Format the calendar time to a more readable form
        String timeFormat = "hh:mm:ss a"; // Example "03:00 PM"
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(timeFormat);
        return sdf.format(calendar.getTime());
    }
    // Just for tickering the time.

    private final Handler handler = new Handler();
    private final Runnable ticker = new Runnable() {
        @Override
        public void run() {
            // Update the current time text view
            Calendar currentTime = Calendar.getInstance();
            updateCurrentTimeTextView(currentTime);
            // Post the ticker to run again after a delay of 1000ms (1 second)
            handler.postDelayed(this, 1000);
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        // Start the ticker when the activity comes into the foreground
        ticker.run();
    }
    @Override
    protected void onPause() {
        super.onPause();
        // Stop the ticker when the activity goes into the background
        handler.removeCallbacks(ticker);
    }
}
