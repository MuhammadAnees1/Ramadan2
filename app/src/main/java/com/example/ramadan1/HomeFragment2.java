package com.example.ramadan1;

import static android.service.controls.ControlsProviderService.TAG;
import static com.example.ramadan1.NimazTimeFragment.convertDate;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.VibrationEffect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
public class HomeFragment2 extends Fragment {
    TextView textView;
    TextView sehriTime1;
    TextView iftariTime1;
    TextView sehriTime2;
    TextView iftariTime2;
    TextView jaffri_sehri_time;
    TextView jaffri_iftari_time;
    TextView location_place;
    RelativeLayout sehrialarmLayout, iftarialarmLayout;
    LinearLayout jaffri_sehri_time_alarm,jaffri_iftari_time_alarm;
    Spinner select_city1;
    private ProgressDialog progressDialog;
    boolean isJaffriShriAlarmSet;
    boolean isjaffriIftarAlarmSet2;
    boolean issehriAlarmSet;
    boolean isiftariAlarmSet ;
    private Location currentLocation;
    ImageView sehriTime_alarm , iftariTime_alarm;

    public HomeFragment2() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home2, container, false);

        // Initialize UI components
        sehrialarmLayout = view.findViewById(R.id.alarm_Layout);
        iftarialarmLayout = view.findViewById(R.id.iftar_alarm_Layout);
        textView = view.findViewById(R.id.textView12);
        sehriTime1 = view.findViewById(R.id.sehriTime1);
        iftariTime1 = view.findViewById(R.id.iftariTime1);
        sehriTime2 = view.findViewById(R.id.sehriTime2);
        iftariTime2 = view.findViewById(R.id.iftariTime2);
        jaffri_sehri_time = view.findViewById(R.id.jaffri_sehri_time);
        jaffri_iftari_time = view.findViewById(R.id.jaffri_iftar_time);
        jaffri_sehri_time_alarm = view.findViewById(R.id.jaffri_sehri_time_alarm);
        jaffri_iftari_time_alarm = view.findViewById(R.id.jaffri_Iftar_time_alarm);
//        select_city1 = view.findViewById(R.id.select_city1);
        sehriTime_alarm = view.findViewById(R.id.sehriTime_alarm);
        iftariTime_alarm = view.findViewById(R.id.iftariTime);
        location_place = view.findViewById(R.id.location_place);
        ViewPager viewPager = view.findViewById(R.id.viewPager);

        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;
        activity.getCurrentLocation();
        activity.checkLocationPermission();
        currentLocation = activity.currentLocation;
        loadData(currentLocation);

        if(issehriAlarmSet){
            sehriTime_alarm.setImageResource(R.drawable.baseline_notifications_active_24);
        }else{
            sehriTime_alarm.setImageResource(R.drawable.baseline_notifications_none_24);

        }
        isiftariAlarmSet = loadAlarmState("Iftari_Alarm");

        if (isiftariAlarmSet) {
            iftariTime_alarm.setImageResource(R.drawable.baseline_notifications_active_24);
        } else {
            iftariTime_alarm.setImageResource(R.drawable.baseline_notifications_none_24);
        }
        // Prepare your texts and images
        List<String> texts = Arrays.asList("يَا حَيُّ يَا قَيُّومُ بِرَحْمَتِكَ أَسْتَغيثُ", "Text 2", "Text 3");
        List<Integer> imageIds = Arrays.asList(R.drawable.qibla, R.drawable.qibla, R.drawable.qibla);

        CustomPagerAdapter adapter2 = new CustomPagerAdapter(getContext(), texts, imageIds);
        viewPager.setAdapter(adapter2);


        String[] cities = getResources().getStringArray(R.array.pakistan_cities);
        TextView english_date1 = view.findViewById(R.id.english_date1);
        TextView islamic_date = view.findViewById(R.id.Islamic_date);

        // Get today's date in the English calendar
        String todayDateEnglish = DateHelper.getCurrentDateEnglish();
        english_date1.setText(todayDateEnglish);

        // Get today's date in the Islamic calendar
        String todayDateIslamic = DateHelper.getCurrentDateIslamic();
        islamic_date.setText(todayDateIslamic);

//        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, cities);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        select_city1.setAdapter(adapter);


        // Fragment transitions
        sehrialarmLayout.setOnClickListener(v -> sendNotification(sehriTime1.getText().toString(), null));
        iftarialarmLayout.setOnClickListener(v -> sendNotification(null, iftariTime1.getText().toString()));

        iftariTime_alarm.setOnClickListener(v -> {
            if (!isiftariAlarmSet) {
                String iftariTime = iftariTime2.getText().toString();

                String[] iftariTimeparts = iftariTime.split(":");
                int IftariHour = Integer.parseInt(iftariTimeparts[0]);
                int iftariMinute = Integer.parseInt(iftariTimeparts[1].split("\\s+")[0]);
                String amPm = iftariTimeparts[1].split("\\s+")[1];
                // Adjust hours for PM
                if (amPm.equalsIgnoreCase("PM") && IftariHour != 12) {
                    IftariHour += 12;
                } else if (amPm.equalsIgnoreCase("AM") && IftariHour == 12) {
                    IftariHour = 0;
                }

                if (amPm.equalsIgnoreCase("PM") &&  IftariHour< 12) {
                    IftariHour += 12;
                }
                Calendar iftariCalendar = Calendar.getInstance();
                iftariCalendar.set(Calendar.HOUR_OF_DAY, IftariHour);
                iftariCalendar.set(Calendar.MINUTE, iftariMinute);
                iftariCalendar.set(Calendar.SECOND, 0);

                if (iftariCalendar.before(Calendar.getInstance())) {
                    iftariCalendar.add(Calendar.DAY_OF_MONTH, 1);
                }
                // Add logs for debugging
                Log.d("AlarmDebug", "Iftari Calendar Time: " + iftariCalendar.getTime());

                AlarmHelper.setupAlarmWithVibration(requireContext(), iftariCalendar, 7);
                saveAlarmState("Iftari_Alarm", true);
                iftariTime_alarm.setImageResource(R.drawable.baseline_notifications_active_24);
                isiftariAlarmSet = true;
            } else {
                AlarmHelper.cancelAlarm(requireContext(), 7);
                saveAlarmState("Iftari_Alarm", false);
                iftariTime_alarm.setImageResource(R.drawable.baseline_notifications_none_24);
                isiftariAlarmSet = false;
            }
        });
        sehriTime_alarm.setOnClickListener(v -> {
            if (!issehriAlarmSet) {
                String SehriTime = sehriTime2.getText().toString();
                String[] sehriTimeParts = SehriTime.split(":");
                int sehriHour = Integer.parseInt(sehriTimeParts[0]);
                int sehriMinute = Integer.parseInt(sehriTimeParts[1].split("\\s+")[0]);
                String amPm = sehriTimeParts[1].split("\\s+")[1];
                // Adjust hours for PM
                if (amPm.equalsIgnoreCase("PM") && sehriHour != 12) {
                    sehriHour += 12;
                } else if (amPm.equalsIgnoreCase("AM") && sehriHour == 12) {
                    sehriHour = 0;
                }
                Calendar sehriCalendar = Calendar.getInstance();
                sehriCalendar.set(Calendar.HOUR_OF_DAY, sehriHour);
                sehriCalendar.set(Calendar.MINUTE, sehriMinute);
                sehriCalendar.set(Calendar.SECOND, 0);

                if (sehriCalendar.before(Calendar.getInstance())) {
                    sehriCalendar.add(Calendar.DAY_OF_MONTH, 1);
                }
                // Add logs for debugging
                Log.d("AlarmDebug", "Sehri Calendar Time: " + sehriCalendar.getTime());

                AlarmHelper.setupAlarmWithVibration(requireContext(), sehriCalendar, 6);
                saveAlarmState("Sehri_Alarm", true);
                sehriTime_alarm.setImageResource(R.drawable.baseline_notifications_active_24);
                issehriAlarmSet = true;
            } else {
                AlarmHelper.cancelAlarm(requireContext(), 6);
                saveAlarmState("Sehri_Alarm", false);
                sehriTime_alarm.setImageResource(R.drawable.baseline_notifications_none_24);
                issehriAlarmSet = false;
            }
        });

//        jaffri_sehri_time_alarm.setOnClickListener(v -> {
//            if (!isJaffriShriAlarmSet) {
//                String SehriTime = jaffri_sehri_time.getText().toString();
//                String[] sehriTimeParts = SehriTime.split(":");
//                int sehriHour = Integer.parseInt(sehriTimeParts[0]);
//                int sehriMinute = Integer.parseInt(sehriTimeParts[1].split("\\s+")[0]);
//                String amPm = sehriTimeParts[1].split("\\s+")[1];
//                // Adjust hours for PM
//                if (amPm.equalsIgnoreCase("PM") && sehriHour != 12) {
//                    sehriHour += 12;
//                } else if (amPm.equalsIgnoreCase("AM") && sehriHour == 12) {
//                    sehriHour = 0;
//                }
//                Calendar sehriCalendar = Calendar.getInstance();
//                sehriCalendar.set(Calendar.HOUR_OF_DAY, sehriHour);
//                sehriCalendar.set(Calendar.MINUTE, sehriMinute);
//                sehriCalendar.set(Calendar.SECOND, 0);
//
//
//                if (sehriCalendar.before(Calendar.getInstance())) {
//                    sehriCalendar.add(Calendar.DAY_OF_MONTH, 1);
//                }
//                // Add logs for debugging
//                Log.d("AlarmDebug", "Sehri Calendar Time: " + sehriCalendar.getTime());
//
//                AlarmHelper.setupAlarmWithVibration(requireContext(), Calendar.getInstance(), 7);
//                saveAlarmState("Jaffri_Alarm", true);
//                isJaffriShriAlarmSet = true;
//            } else {
//                AlarmHelper.cancelAlarm(requireContext(), 5);
//                saveAlarmState("Jaffri_Alarm", false);
//                isJaffriShriAlarmSet = false;
//            }
//
//        });
//        jaffri_iftari_time_alarm.setOnClickListener(v -> {
//            if (!isJaffriShriAlarmSet) {
//
//                AlarmHelper.setupAlarmWithVibration(requireContext(), Calendar.getInstance(), 8);
//                saveAlarmState("Jaffri_Alarm", true);
//                isJaffriShriAlarmSet = true;
//            } else {
//                AlarmHelper.cancelAlarm(requireContext(), 4);
//                saveAlarmState("Jaffri_Alarm", false);
//                isJaffriShriAlarmSet = false;
//            }
//        });
//
        return view;
    }
    private void sendNotification(String sehriTime, String iftariTime) {
        Intent notificationIntent = new Intent(requireContext(), NotificationActivity.class);
        notificationIntent.putExtra("sehriTime", sehriTime);
        notificationIntent.putExtra("iftariTime", iftariTime);
        Log.d(TAG, "sendNotification: times"+sehriTime+iftariTime);
        startActivity(notificationIntent);
    }
    private void saveAlarmState(String key, boolean isAlarmOn) {
        SharedPreferences preferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, isAlarmOn);
        editor.apply();
    }
    private boolean loadAlarmState(String key) {
        SharedPreferences preferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }
    void loadData(Location location) {
        if (location != null) {
            Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (!addresses.isEmpty()) {
                    String city = addresses.get(0).getLocality();
                    String country = addresses.get(0).getCountryName();
                    location_place.setText(city + ", " + country);
                    saveLocation(city, country);
                    long millis = Calendar.getInstance().getTimeInMillis();
                    String customURL = "https://api.aladhan.com/v1/timingsByAddress/{day}-{MONTH}-{year}?address={city}%2C+{country}";
                    String url = customURL.replace("{year}", convertDate(String.valueOf(millis), "yyyy"))
                            .replace("{MONTH}", convertDate(String.valueOf(millis), "MM"))
                            .replace("{day}", convertDate(String.valueOf(millis), "dd"))
                            .replace("{city}", city)
                            .replace("{country}", country);

                    RequestQueue queue = Volley.newRequestQueue(requireContext());
                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
                        try {
                            String status = response.getString("status");
                            if (status.equals("OK")) {
                                JSONObject dataObject = response.getJSONObject("data");
                                JSONObject timingObject = dataObject.getJSONObject("timings");
                                String date = dataObject.getJSONObject("date").getString("readable");
                                // Update your UI or process the data as needed
                                updateUIWithData(timingObject, date);
                                dismissProgressDialog();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(requireContext(), "JSON Parsing Error", Toast.LENGTH_SHORT).show();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            Toast.makeText(requireContext(), "Network Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            showProgressDialog();

                        }
                    });

                    queue.add(request);
                }
            } catch (IOException e) {
                e.printStackTrace();
                // Service not available, show dialog box
                showDialogBox("Service Not Available", "The location service is not available. Please try again later.");
                dismissProgressDialog();
            } finally {
                // Your existing code...
            }
        }
        else {
            Toast.makeText(requireContext(), "Location not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveLocation(String city, String country) {
        SharedPreferences preferences = requireActivity().getSharedPreferences("LocationPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("city", city);
        editor.putString("country", country);
        editor.apply();
    }

    // Remove the timezone suffix (e.g., "(PKT)") and convert to 12-hour format
    private String convertTo12HourFormat(String timeWithSuffix) {
        // Assuming the input time is always in the HH:mm format
        SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());

        try {
            // Parse the input time and format it to 12-hour format
            Date date = inputFormat.parse(timeWithSuffix);
            if (date != null) {
                return outputFormat.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Return the original time if parsing fails
        return timeWithSuffix;
    }
    // Remove the timezone suffix (e.g., "(PKT)")

    private String removeTimeZoneSuffix(String timeWithSuffix) {
        int indexOfParentheses = timeWithSuffix.indexOf("(");
        if (indexOfParentheses != -1) {
            // Remove the portion starting from the first parenthesis
            return timeWithSuffix.substring(0, indexOfParentheses).trim();
        } else {
            return timeWithSuffix.trim();
        }
    }
    private String adjustTime(String timeWithSuffix, int minutes) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        try {
            // Parse the input time
            Date date = inputFormat.parse(timeWithSuffix);
            if (date != null) {
                calendar.setTime(date);
                // Adjust the time by adding or subtracting minutes
                calendar.add(Calendar.MINUTE, minutes);
                // Format the adjusted time back to the desired format
                return inputFormat.format(calendar.getTime());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // Return the original time if parsing fails
        return timeWithSuffix;
    }
    private void updateUIWithData(JSONObject timingObject, String date) {
        try {
            String fajrTime = convertTo12HourFormat(removeTimeZoneSuffix(timingObject.getString("Fajr")));
            String maghribTime = convertTo12HourFormat(removeTimeZoneSuffix(timingObject.getString("Maghrib")));
            String adjustedFajrTime = adjustTime(fajrTime, -10);  // Subtract 10 minutes
            String adjustedMaghribTime = adjustTime(maghribTime, 10);  // Add 10 minutes

            Log.d(TAG, "updateUIWithData: "+adjustedFajrTime+" "+adjustedMaghribTime);
            // Update UI components with the modified times
            sehriTime1.setText(fajrTime);
            iftariTime1.setText(maghribTime);
            sehriTime2.setText(fajrTime);
            iftariTime2.setText(maghribTime);
            jaffri_sehri_time.setText(adjustedFajrTime);
            jaffri_iftari_time.setText(adjustedMaghribTime);

            // Update any other UI components as needed
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...Please wait for a moment");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void showDialogBox(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Dismiss the dialog
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}