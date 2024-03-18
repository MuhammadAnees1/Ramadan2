package com.example.ramadan1;


import static android.app.PendingIntent.getActivity;
import static android.service.controls.ControlsProviderService.TAG;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationActivity extends AppCompatActivity {
    ImageView backbutton;
    TextView namz_name,Namaz_time_View,timer_textView;
//    Switch notification_off_on_Switch;
    RelativeLayout pre_time_alarm_layout, custom_time_alarm_layout;
    private final List<soundsModel> dataList1 = new ArrayList<>();
    String initialTimeName = null,initialTimeName1;
    private int initialTime; // Initially set time
    private SharedPreferences sharedPreferences;
    private Switch vibrationSwitch;
    private String sehriTime;
    private String iftariTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Toolbar toolbar = findViewById(R.id.toolbar);
        namz_name = findViewById(R.id.namz_name);
        Namaz_time_View = findViewById(R.id.Namaz_time_View);
        backbutton = findViewById(R.id.back_button);
        timer_textView = findViewById(R.id.timer_textView);


        String sehriTime = getIntent().getStringExtra("sehriTime");
        String iftariTime = getIntent().getStringExtra("iftariTime");

        String fajrTime = getIntent().getStringExtra("FajrTime");
        String dhuhrTime = getIntent().getStringExtra("DhuhrTime");
        String asrTime = getIntent().getStringExtra("AsrTime");
        String maghribTime = getIntent().getStringExtra("MaghribTime");
        String ishaTime = getIntent().getStringExtra("IshaTime");


        SharedPreferences sharedPreferences = NotificationActivity.this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        initialTime = sharedPreferences.getInt("initialTime", 0);
        initialTimeName1 = sharedPreferences.getString("initialTimeName", "");
        Log.d(TAG, "onCreateView: initialTimeName2 " + initialTimeName1 + " " + initialTime);





//        if (sehriTime!= null) {
//            namz_name.setText("Sehri Time Notification");
//            Namaz_time_View.setText(sehriTime);
//
//        } else {
            if (fajrTime != null) {
                namz_name.setText("Fajr Time Notification");
                Namaz_time_View.setText(fajrTime);

            } else if (dhuhrTime!= null) {
                namz_name.setText("Dhuhr Time Notification");
                Namaz_time_View.setText(dhuhrTime);

            } else if (asrTime!= null) {
                namz_name.setText("Asr Time Notification");
                Namaz_time_View.setText(asrTime);

            } else if (maghribTime!= null) {
                namz_name.setText("Maghrib Time Notification");
                Namaz_time_View.setText(maghribTime);
            } else if (ishaTime!= null) {
                namz_name.setText("Isha Time Notification");
                Namaz_time_View.setText(ishaTime);
            }
            else if(sehriTime!= null) {
                namz_name.setText("Sehri Time Notification");
                Namaz_time_View.setText(sehriTime);
                timer_textView.setText(String.valueOf(initialTime));

            }else if (iftariTime!= null) {
                namz_name.setText("Iftari Time Notification");
                Namaz_time_View.setText(iftariTime);
                timer_textView.setText(String.valueOf(initialTime));

            } else {
                namz_name.setText("No time available");
            }

        // Retrieve data from Intent
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);

        // Initialize UI elements
        pre_time_alarm_layout = findViewById(R.id.pre_time_alarm_layout);
        custom_time_alarm_layout = findViewById(R.id.custom_time_alarm_layout);
        TextView english_date1 = findViewById(R.id.english_date1);
        TextView islamic_date = findViewById(R.id.Islamic_date);
        vibrationSwitch = findViewById(R.id.vibration_off);
//        notification_off_on_Switch = findViewById(R.id.notification_off_on_Switch);

        english_date1.setText(DateHelper.getCurrentDateEnglish());
        islamic_date.setText(DateHelper.getCurrentDateIslamic());

////        RecyclerView soundRecyclerView = findViewById(R.id.sound_recyclerView);
//        soundRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        populateDataList();
//        notification_adaptor adapter = new notification_adaptor(dataList1);
//        soundRecyclerView.setAdapter(adapter);

        pre_time_alarm_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if any time is not null
                if (sehriTime != null || iftariTime != null || fajrTime != null || dhuhrTime != null || asrTime != null || maghribTime != null || ishaTime != null) {
                    // Logic to decide on a shadePreference based on available time

                    // Store the shadePreference
                    // Prepare bundle for the next fragment
                    Bundle bundle = new Bundle();
                    bundle.putString("sehriTime", sehriTime);
                    bundle.putString("iftariTime", iftariTime);
                    bundle.putString("fajrTime", fajrTime);
                    bundle.putString("dhuhrTime", dhuhrTime);
                    bundle.putString("asrTime", asrTime);
                    bundle.putString("maghribTime", maghribTime);
                    bundle.putString("ishaTime", ishaTime);
                    getInitialPrayerTime("SehriTime");
                    Log.d(TAG, "onClick: sehriTime"+getInitialPrayerTime(""));
                    NotificationTimeFragment notificationTimeFragment = new NotificationTimeFragment();
                    notificationTimeFragment.setArguments(bundle);

                    // Show the NotificationTimeFragment
                    notificationTimeFragment.show(getSupportFragmentManager(), notificationTimeFragment.getTag());
                } else {
                    // Handle the case where all times are null
                    Toast.makeText(NotificationActivity.this, "All times are null", Toast.LENGTH_SHORT).show();
                }
            }
        });

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        custom_time_alarm_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotificationActivity.this, Custom_alarm_activity.class);
                startActivity(intent);
            }
        });

//        notification_off_on_Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                handleNotificationSwitch(isChecked);
//            }
//        });

        vibrationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                handleVibrationSwitch(isChecked);
            }
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean vibrationEnabled = prefs.getBoolean("vibration_enabled", true);
        vibrationSwitch.setChecked(vibrationEnabled);


        // Inside onCreate method
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        boolean notificationsEnabled = prefs.getBoolean("notifications_enabled", true);
//        notification_off_on_Switch.setChecked(notificationsEnabled);

    }

    private void populateDataList() {
        dataList1.add(new soundsModel(0, "Alarm Sound 1"));
        dataList1.add(new soundsModel(1, "Alarm Sound 2"));
        dataList1.add(new soundsModel(2, "Alarm Sound 3"));
        dataList1.add(new soundsModel(3, "Alarm Sound 4"));
        dataList1.add(new soundsModel(4, "Alarm Sound 5"));
    }

//    private void showTimePicker() {
//        Calendar currentTime = Calendar.getInstance();
//        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
//        int minute = currentTime.get(Calendar.MINUTE);
//
//        TimePickerDialog timePickerDialog = new TimePickerDialog(
//                this,
//                new TimePickerDialog.OnTimeSetListener() {
//                    @Override
//                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                        // Save the selected time in SharedPreferences
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                        editor.putInt("alarmHour", hourOfDay);
//                        editor.putInt("alarmMinute", minute);
//                        editor.apply();
//
//                        // Set the alarm
//                        setAlarm(hourOfDay, minute);
//                    }
//                },
//                hour,
//                minute,
//                true
//        );
//        timePickerDialog.show();
//    }

    @SuppressLint({"WakelockTimeout", "ScheduleExactAlarm"})
//    private void setAlarm(int hour, int minute) {
//        Calendar alarmTime = Calendar.getInstance();
//        alarmTime.set(Calendar.HOUR_OF_DAY, hour);
//        alarmTime.set(Calendar.MINUTE, minute);
//        alarmTime.set(Calendar.SECOND, 0);
//
//        if (alarmTime.before(Calendar.getInstance())) {
//            alarmTime.add(Calendar.DAY_OF_MONTH, 1);
//
//        }
//
//        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE);
//
//        // Acquire a WakeLock to wake up the device
//        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(
//                PowerManager.PARTIAL_WAKE_LOCK |
//                        PowerManager.ACQUIRE_CAUSES_WAKEUP,
//                "myapp:AlarmWakeLock"
//        );
//        wakeLock.acquire();
//        try {
//            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                AlarmManager.AlarmClockInfo alarmClockInfo =
//                        new AlarmManager.AlarmClockInfo(alarmTime.getTimeInMillis(), pendingIntent);
//                alarmManager.setAlarmClock(alarmClockInfo, pendingIntent);
//            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), pendingIntent);
//            } else {
//                alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), pendingIntent);
//            }
//        } finally {
//            // Release the WakeLock after scheduling the alarm
//            if (wakeLock.isHeld()) {
//                wakeLock.release();
//            }
//        }
//    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle the Up button click
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    private void handleNotificationSwitch(boolean isChecked) {
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        prefs.edit().putBoolean("notifications_enabled", isChecked).apply();
//    }

    private void handleVibrationSwitch(boolean isChecked) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putBoolean("vibration_enabled", isChecked).apply();
    }
//    public static boolean areNotificationsEnabled(Context context) {
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//        // Assuming default is true, change if necessary
//        return prefs.getBoolean("notifications_enabled", true);
//    }

    public static boolean areVibrationEnabled(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        // Assuming default is true, change if necessary
        return prefs.getBoolean("vibration_enabled", true);
    }
    private int getInitialPrayerTime(String prayerName) {
        SharedPreferences sharedPreferences = NotificationActivity.this.getSharedPreferences("PrayerTimes", Context.MODE_PRIVATE);
        return sharedPreferences.getInt(prayerName, 0);
    }
}
