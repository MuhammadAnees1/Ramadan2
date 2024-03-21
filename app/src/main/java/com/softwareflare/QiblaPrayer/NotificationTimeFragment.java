package com.softwareflare.QiblaPrayer;

import static android.service.controls.ControlsProviderService.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NotificationTimeFragment extends BottomSheetDialogFragment {
    private NumberPicker numberPicker;
    TextView time_TextView;
    private int accumulatedMinutes = 0;
    int alarmCode = 9;
    int minutes;
    String name;
    String initialTimeName = null, initialTimeName1;
    Map<String, Integer> prayerMap = new HashMap<>();
    private int initialTime;
    AppCompatButton confirm_alarm;
    ImageView plusButton, closeButton, minButton;
    String sehriTime, iftariTime, fajrTime, dhuhrTime, asrTime, maghribTime, ishaTime;
    SharedPreferences sharedPreferences;
    NotificationActivity notificationActivity;
    static final String INITIAL_PRAYER_TIME_KEY = "initial_prayer_time";

    public NotificationTimeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_time, container, false);
        // Initialize UI components
        numberPicker = view.findViewById(R.id.editText);
        plusButton = view.findViewById(R.id.plus_button);
        minButton = view.findViewById(R.id.min_button);
        closeButton = view.findViewById(R.id.close);
        time_TextView = view.findViewById(R.id.time_TextView);
        confirm_alarm = view.findViewById(R.id.confirm_alarm);

        sehriTime = getArguments() != null ? getArguments().getString("sehriTime", "") : "";
        iftariTime = getArguments() != null ? getArguments().getString("iftariTime", "") : "";
        fajrTime = getArguments() != null ? getArguments().getString("fajrTime", "") : "";
        dhuhrTime = getArguments() != null ? getArguments().getString("dhuhrTime", "") : "";
        asrTime = getArguments() != null ? getArguments().getString("asrTime", "") : "";
        maghribTime = getArguments() != null ? getArguments().getString("maghribTime", "") : "";
        ishaTime = getArguments() != null ? getArguments().getString("ishaTime", "") : "";

        Log.d(TAG, "onCreateView: " + fajrTime + dhuhrTime + asrTime + maghribTime + ishaTime + sehriTime + iftariTime);
        notificationActivity = (NotificationActivity) getActivity();
        if (!sehriTime.isEmpty()) {
            time_TextView.setText(sehriTime);
            alarmCode = 6;
            initialTimeName = "Sehri Time";
        } else if (!fajrTime.isEmpty()) {
            alarmCode = 1;
            initialTimeName = "Fajr Time";
            time_TextView.setText(fajrTime);
        } else if (!dhuhrTime.isEmpty()) {
            time_TextView.setText(dhuhrTime);
            alarmCode = 2;
            initialTimeName = "Dhuhr Time";
        } else if (!asrTime.isEmpty()) {
            time_TextView.setText(asrTime);
            alarmCode = 3;
            initialTimeName = "Asr Time";
        } else if (!maghribTime.isEmpty()) {
            time_TextView.setText(maghribTime);
            alarmCode = 4;
            initialTimeName = "Maghrib Time";
        } else if (!ishaTime.isEmpty()) {
            time_TextView.setText(ishaTime);
            alarmCode = 5;
            initialTimeName = "Isha Time";
        } else if (!iftariTime.isEmpty()) {
            time_TextView.setText(iftariTime);
            alarmCode = 7;
            initialTimeName = "Iftari Time";
        } else {
            time_TextView.setText("No time available");
        }
        if (prayerMap != null) {

            // Set up NumberPicker
            numberPicker.setMinValue(0);
            numberPicker.setMaxValue(59);
            numberPicker.setWrapSelectorWheel(false);
            numberPicker.setOnValueChangedListener((picker, oldVal, newVal) -> accumulatedMinutes = newVal);

            prayerMap = retrieveFromSharedPreferences(getContext());
            if (prayerMap != null) {
                for (Map.Entry<String, Integer> entry : prayerMap.entrySet()) {
                    name = entry.getKey();
                    minutes = entry.getValue();
                    if(name.equals(initialTimeName)) {
                        numberPicker.setValue(minutes);
                    }
                    Log.d(TAG, "onCreateView: " + minutes + " " + name);
                }
            } else {
                Log.d(TAG, "Prayer map is null.");
            }
        }

        numberPicker.setOnScrollListener(new NumberPicker.OnScrollListener() {
            @Override
            public void onScrollStateChange(NumberPicker picker, int scrollState) {
                if (scrollState == NumberPicker.OnScrollListener.SCROLL_STATE_IDLE) {
                    int value = picker.getValue();
                    // The scrolling has stopped
                    onNumberPickerScrollStopped(picker.getValue());
                }
            }
        });
        // Set click listeners for buttons
        plusButton.setOnClickListener(v -> incrementNumberPicker());
        minButton.setOnClickListener(v -> decrementNumberPicker());
        closeButton.setOnClickListener(v -> dismiss());

        confirm_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (time_TextView.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Loading", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Get the time string from time_TextView
                String timeString = time_TextView.getText().toString();
                // Extracting the initial time
                int prayerMinutes = initialTime;
                String prayerNames = initialTimeName;
                // Ensure prayerMap is not null
                if (prayerMap == null) {
                    prayerMap = new HashMap<>();
                }
                // Check if the name already exists in the map and update or add accordingly
                boolean nameExists = false;
                Map.Entry<String, Integer> existingEntry = null;
                if (prayerMap != null) {
                    for (Map.Entry<String, Integer> entry : prayerMap.entrySet()) {
                        if (entry.getValue().equals(prayerNames)) {
                            // Name exists, store the existing entry and exit the loop
                            existingEntry = entry;
                            nameExists = true;
                            break;
                        }
                    }
                }
                if (nameExists && existingEntry != null) {
                    prayerMap.remove(existingEntry.getKey());
                    prayerMap.put(prayerNames, prayerMinutes);
                } else {
                    // If the name doesn't exist, add it to the map
                    prayerMap.put(prayerNames, prayerMinutes);
                }
                Log.d(TAG, "SAVE123 " +prayerNames + " "+ prayerMinutes);

                // Save the updated prayerMap to SharedPreferences
                saveToSharedPreferences(getContext(), new HashMap<>(prayerMap));

                NotificationActivity notificationActivity = (NotificationActivity) getActivity();
                notificationActivity.pre_alarm_layout_deleted.setVisibility(View.VISIBLE);
                String minutes1 = String.valueOf(numberPicker.getValue());
                notificationActivity.timer_textView.setText(minutes1);
//                // Parse the timeString to extract hours and minutes
                String[] timeParts = timeString.split(":");
                int hours = Integer.parseInt(timeParts[0]);
                int minutes = Integer.parseInt(timeParts[1].substring(0, 2));

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hours);
                calendar.set(Calendar.MINUTE, minutes);
                calendar.set(Calendar.SECOND, 0);

                if (calendar.before(Calendar.getInstance())) {
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                }

                Log.d(TAG, "onClick: calendar"+calendar);
                // Setting up the alarm
                AlarmHelper.setupAlarmWithVibration(getContext(), calendar, alarmCode);
                // Display a toast indicating the alarm is set
                Toast.makeText(getContext(), "Alarm set for: " + timeString, Toast.LENGTH_SHORT).show();
                saveTimeStringToSharedPreferences(getContext(), timeString);
                saveAlarmState("pre_alarm", true, NotificationTimeFragment.this);
                savePrayerState(alarmCode, true, getContext());


                // Remove the current fragment and pop back the stack
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().remove(NotificationTimeFragment.this).commit();
                fragmentManager.popBackStack();

            }
        });



        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });





        return view;
    }


    private void incrementNumberPicker () {
        if (numberPicker.getValue() < numberPicker.getMaxValue()) {
            int minutesToAdd = 1;

            // Parse the current time to a Calendar object
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());

            try {
                Date date = sdf.parse(time_TextView.getText().toString());
                calendar.setTime(date);

                // Add minutes to the current time
                calendar.add(Calendar.MINUTE, minutesToAdd);
                // Format and set the new time on time_TextView
                String newTime = sdf.format(calendar.getTime());
                time_TextView.setText(newTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // Increase the value in NumberPicker
            numberPicker.setValue(numberPicker.getValue() + 1);
        }
    }
    private void onNumberPickerScrollStopped(int finalValue) {

        if (finalValue < 0) {
            finalValue = 0;
        }
        // Calculate the accumulated minutes by subtracting the final value from the initial value
        accumulatedMinutes = initialTime - finalValue;
        initialTime = finalValue;
        // Parse the current time to a Calendar object
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        try {
            Date date = sdf.parse(time_TextView.getText().toString());

            calendar.setTime(date);

            // Subtract accumulated minutes from the current time
            calendar.add(Calendar.MINUTE, accumulatedMinutes);

            // Format and set the new time on time_TextView
            String newTime = sdf.format(calendar.getTime());

            time_TextView.setText(newTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    // Format and set the new time on time_TextView
    private void decrementNumberPicker() {
        if (numberPicker.getValue() > numberPicker.getMinValue()) {
            int minutesToSubtract = 1; // Set the step size to 1 minute

            // Parse the current time to a Calendar object
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());

            try {
                Date date = sdf.parse(time_TextView.getText().toString());
                calendar.setTime(date);

                // Subtract the minutes from the current time
                calendar.add(Calendar.MINUTE, -minutesToSubtract);

                // Format and set the new time on time_TextView
                String newTime = sdf.format(calendar.getTime());
                time_TextView.setText(newTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // Decrease the value in NumberPicker
            numberPicker.setValue(numberPicker.getValue() - 1);
        }
    }
    private void storeAccumulatedMinutesInPreferences(int minutes) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("initialTime", minutes);
        editor.putString("initialTimeName", initialTimeName);
        editor.apply();
    }
    private void storePrayerTimesInPreferences(int[] minutesArray, String[] prayerNamesArray) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (int i = 0; i < prayerNamesArray.length; i++) {
            editor.putInt(prayerNamesArray[i], minutesArray[i]);
        }
        editor.apply();
    }
    private void saveInitialPrayerTime(int initialTime) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("PrayerTimes", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(INITIAL_PRAYER_TIME_KEY, initialTime);
        editor.apply();
    }
    private int getPrayerTimeFromPreferences(String prayerName) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getInt(prayerName, 0);
    }

    // Method to save timeString to SharedPreferences
    private void saveTimeStringToSharedPreferences(Context context, String timeString) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("timeString", timeString);
        editor.apply();
    }
    // Method to save the HashMap to SharedPreferences
    public void saveToSharedPreferences(Context context, HashMap<String, Integer> dataMap) {
        // Initialize SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("your_preference_name", Context.MODE_PRIVATE);
        // Initialize editor
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Convert the HashMap to a JSON string
        Gson gson = new Gson();
        String json = gson.toJson(dataMap);

        // Save the JSON string to SharedPreferences
        editor.putString("prayer_map", json);
        editor.apply();
    }
    public HashMap<String, Integer> retrieveFromSharedPreferences(Context context) {
        // Initialize SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("your_preference_name", Context.MODE_PRIVATE);
        // Retrieve the JSON string from SharedPreferences
        String json = sharedPreferences.getString("prayer_map", "");
        // Convert the JSON string back to a HashMap
        Gson gson = new Gson();Type type = new TypeToken<HashMap<String, Integer>>(){}.getType();
        return gson.fromJson(json, type);
    }
    private void saveAlarmState(String key, boolean isAlarmOn, Fragment fragment) {
        SharedPreferences preferences = fragment.requireActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, isAlarmOn);
        editor.apply();
}

    private void savePrayerState(int prayerName, boolean isEnabled, Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("prayer_" + prayerName, isEnabled);
        Log.d(TAG, "Prayer: " + prayerName + ", Enabled: " + isEnabled);
        editor.apply();
    }
}