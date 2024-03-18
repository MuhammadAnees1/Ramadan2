package com.example.ramadan1;

import static android.service.controls.ControlsProviderService.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ibm.icu.util.IslamicCalendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NimazTimeFragment extends Fragment {
    TextView fajrTimeTextView, dhuhrTimeTextView, asrTimeTextView, maghribTimeTextView, ishaTimeTextView, english_date1, islamic_date;
    ImageView fajar_time_alarm, fajar_time_alarm_0ff, dhuhr_Time_Alarm, dhuhr_time_alarm_0ff, asr_Time_Alarm, asr_time_alarm_0ff, maghrib_time_alarm, maghrib_time_alarm_0ff, isha_time_alarm, isha_time_alarm_0ff;
    LinearLayout linearLayout1, linearLayout2, linearLayout3, linearLayout4, linearLayout5;
    int requestCodeFajr = 1;
    int requestCodeDhuhr = 2;
    int requestCodeAsr = 3;
    int requestCodeMaghrib = 4;
    int requestCodeIsha = 5;
    TextView location_place;
    private Location currentLocation;


    public NimazTimeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nimaz_time, container, false);
        fajrTimeTextView = view.findViewById(R.id.fajar_time);
        dhuhrTimeTextView = view.findViewById(R.id.Dhuhr_time);
        dhuhr_time_alarm_0ff = view.findViewById(R.id.dhuhr_time_alarm_0ff);
        dhuhr_Time_Alarm = view.findViewById(R.id.dhuhr_Time_Alarm);
        asrTimeTextView = view.findViewById(R.id.asr_time);
        asr_time_alarm_0ff = view.findViewById(R.id.asr_time_Alarm_off);
        asr_Time_Alarm = view.findViewById(R.id.asr_time_Alarm);
        maghribTimeTextView = view.findViewById(R.id.magrib_time);
        maghrib_time_alarm_0ff = view.findViewById(R.id.magrib_time_alarm_off);
        maghrib_time_alarm = view.findViewById(R.id.magrib_time_alarm);
        ishaTimeTextView = view.findViewById(R.id.isha_time);
        isha_time_alarm_0ff = view.findViewById(R.id.isha_time_alarm_off);
        isha_time_alarm = view.findViewById(R.id.isha_time_alarm);
        english_date1 = view.findViewById(R.id.english_date1);
        islamic_date = view.findViewById(R.id.Islamic_date);
        fajar_time_alarm = view.findViewById(R.id.fajar_time_alarm);
        fajar_time_alarm_0ff = view.findViewById(R.id.fajar_time_alarm_0ff);
        linearLayout1 = view.findViewById(R.id.linearLayout1);
        linearLayout2 = view.findViewById(R.id.linearLayout2);
        linearLayout3 = view.findViewById(R.id.linearLayout3);
        linearLayout4 = view.findViewById(R.id.linearLayout4);
        linearLayout5 = view.findViewById(R.id.linearLayout6);
        location_place = view.findViewById(R.id.location_place);




        boolean isFajrAlarmOn = loadAlarmState("fajr_alarm");
        if (isFajrAlarmOn) {
            fajar_time_alarm.setVisibility(View.GONE);
            fajar_time_alarm_0ff.setVisibility(View.VISIBLE);
        } else {
            fajar_time_alarm.setVisibility(View.VISIBLE);
            fajar_time_alarm_0ff.setVisibility(View.GONE);
        }

        // Get today's date in the English calendar
        String todayDateEnglish = DateHelper.getCurrentDateEnglish();
        english_date1.setText(todayDateEnglish);

        // Get today's date in the Islamic calendar
        String todayDateIslamic = DateHelper.getCurrentDateIslamic();
        islamic_date.setText(todayDateIslamic);

        // Load JSON data from a file or a string (you can adapt this based on your approach)
        String jsonString = loadJsonData("namzTime.json");
//        try {
//            JSONObject jsonObject = new JSONObject(jsonString);
//            JSONArray prayerTimesArray = jsonObject.getJSONArray("prayerTimes");
//
//            for (int i = 0; i < prayerTimesArray.length(); i++) {
//                JSONObject prayerObject = prayerTimesArray.getJSONObject(i);
//                String date = prayerObject.getString("date");
//
//                if (date.equals(todayDateEnglish)) {
//                    fajrTimeTextView.setText(prayerObject.getString("fajr"));
//                    dhuhrTimeTextView.setText(prayerObject.getString("dhuhr"));
//                    asrTimeTextView.setText(prayerObject.getString("asr"));
//                    maghribTimeTextView.setText(prayerObject.getString("maghrib"));
//                    ishaTimeTextView.setText(prayerObject.getString("isha"));
//                    break;
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;
        activity.getCurrentLocation();
        activity.checkLocationPermission();
        currentLocation = activity.currentLocation;
        loadData(currentLocation);


        linearLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current Fajr time
                String fajrTime = fajrTimeTextView.getText().toString();
                Intent intent = new Intent(getActivity(), NotificationActivity.class);
                intent.putExtra("FajrTime", fajrTime);
                startActivity(intent);
            }
        });
        linearLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current Fajr time
                String dhuhrTime = dhuhrTimeTextView.getText().toString();
                Intent intent = new Intent(getActivity(), NotificationActivity.class);
                intent.putExtra("DhuhrTime", dhuhrTime);
                startActivity(intent);
            }
        });
        linearLayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current Fajr time
                String asrTime = asrTimeTextView.getText().toString();
                Intent intent = new Intent(getActivity(), NotificationActivity.class);
                intent.putExtra("AsrTime", asrTime);
                startActivity(intent);
            }
        });
        linearLayout4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current Fajr time
                String maghribTime = maghribTimeTextView.getText().toString();
                Intent intent = new Intent(getActivity(), NotificationActivity.class);
                intent.putExtra("MaghribTime", maghribTime);
                startActivity(intent);
            }
        });
        linearLayout5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current Fajr time
                String ishaTime = ishaTimeTextView.getText().toString();
                Intent intent = new Intent(getActivity(), NotificationActivity.class);
                intent.putExtra("IshaTime", ishaTime);
                startActivity(intent);
            }
        });


        fajar_time_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fajar_time_alarm.setVisibility(View.GONE);
                fajar_time_alarm_0ff.setVisibility(View.VISIBLE);
                // Get the Fajr time
                String fajrTime = fajrTimeTextView.getText().toString();

                // Parse the Fajr time to extract hours, minutes, and AM/PM
                String[] fajrTimeParts = fajrTime.split(":");
                int fajrHour = Integer.parseInt(fajrTimeParts[0]);
                int fajrMinute = Integer.parseInt(fajrTimeParts[1].split("\\s+")[0]);
                String amPm = fajrTimeParts[1].split("\\s+")[1];
                // Adjust hours for PM
                if (amPm.equalsIgnoreCase("PM") && fajrHour != 12) {
                    fajrHour += 12;
                } else if (amPm.equalsIgnoreCase("AM") && fajrHour == 12) {
                    fajrHour = 0;
                }
                // Create a Calendar object for the Fajr time
                Calendar fajrCalendar = Calendar.getInstance();
                fajrCalendar.set(Calendar.HOUR_OF_DAY, fajrHour);
                fajrCalendar.set(Calendar.MINUTE, fajrMinute);
                fajrCalendar.set(Calendar.SECOND, 0);

                if (fajrCalendar.before(Calendar.getInstance())) {
                    fajrCalendar.add(Calendar.DAY_OF_MONTH, 1);
                }

                // Add logs for debugging
                Log.d("AlarmDebug", "Fajr Calendar Time: " + fajrCalendar.getTime());

                // Set up the alarm for Fajr time
                AlarmHelper.setupAlarmWithVibration(requireContext(), fajrCalendar, requestCodeFajr);
                saveAlarmState("fajr_alarm", true);

                Toast.makeText(getContext(), "Fajr alarm set", Toast.LENGTH_SHORT).show();
            }
        });
        fajar_time_alarm_0ff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fajar_time_alarm.setVisibility(View.VISIBLE);
                fajar_time_alarm_0ff.setVisibility(View.GONE);
                // Cancel the Fajr alarm
                AlarmHelper.cancelAlarm(requireContext(), requestCodeFajr);
                saveAlarmState("fajr_alarm", false);
                Toast.makeText(getContext(), "Fajr alarm cancelled", Toast.LENGTH_SHORT).show();
            }
        });
// Repeat the above pattern for other alarms
        dhuhr_Time_Alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dhuhr_Time_Alarm.setVisibility(View.GONE);
                dhuhr_time_alarm_0ff.setVisibility(View.VISIBLE);
                // Get the Dhuhr time
                String dhuhrTime = dhuhrTimeTextView.getText().toString();

                // Parse the Dhuhr time to extract hours, minutes, and AM/PM
                String[] dhuhrTimeParts = dhuhrTime.split(":");
                int dhuhrHour = Integer.parseInt(dhuhrTimeParts[0]);
                int dhuhrMinute = Integer.parseInt(dhuhrTimeParts[1].split("\\s+")[0]); // Remove "AM" or "PM"
                String amPm = dhuhrTimeParts[1].split("\\s+")[1];

                // Adjust hours for PM
                if (amPm.equalsIgnoreCase("PM") && dhuhrHour < 12) {
                    dhuhrHour += 12;
                }
                // Create a Calendar object for the Dhuhr time
                Calendar dhuhrCalendar = Calendar.getInstance();
                dhuhrCalendar.set(Calendar.HOUR_OF_DAY, dhuhrHour);
                dhuhrCalendar.set(Calendar.MINUTE, dhuhrMinute);
                dhuhrCalendar.set(Calendar.SECOND, 0);

                if (dhuhrCalendar.before(Calendar.getInstance())) {
                    dhuhrCalendar.add(Calendar.DAY_OF_MONTH, 1);
                }
                // Add logs for debugging
                Log.d("AlarmDebug", "Dhuhr Calendar Time: " + dhuhrCalendar.getTime());

                // Set up the alarm for Dhuhr time
                AlarmHelper.setupAlarmWithVibration(requireContext(), dhuhrCalendar, requestCodeDhuhr);


                Toast.makeText(getContext(), "Dhuhr alarm set", Toast.LENGTH_SHORT).show();
            }
        });
        dhuhr_time_alarm_0ff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dhuhr_Time_Alarm.setVisibility(View.VISIBLE);
                dhuhr_time_alarm_0ff.setVisibility(View.GONE);

                AlarmHelper.cancelAlarm(requireContext(), requestCodeDhuhr);

                Toast.makeText(getContext(), "Dhuhr alarm cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        asr_Time_Alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asr_Time_Alarm.setVisibility(View.GONE);
                asr_time_alarm_0ff.setVisibility(View.VISIBLE);
                // Get the Asr time
                String asrTime = asrTimeTextView.getText().toString();

                // Parse the Asr time to extract hours, minutes, and AM/PM
                String[] asrTimeParts = asrTime.split(":");
                int asrHour = Integer.parseInt(asrTimeParts[0]);
                int asrMinute = Integer.parseInt(asrTimeParts[1].split("\\s+")[0]); // Remove "AM" or "PM"
                String amPm = asrTimeParts[1].split("\\s+")[1];

                // Adjust hours for PM
                if (amPm.equalsIgnoreCase("PM") && asrHour < 12) {
                    asrHour += 12;
                }

                // Create a Calendar object for the Asr time
                Calendar asrCalendar = Calendar.getInstance();
                asrCalendar.set(Calendar.HOUR_OF_DAY, asrHour);
                asrCalendar.set(Calendar.MINUTE, asrMinute);
                asrCalendar.set(Calendar.SECOND, 0);

                if (asrCalendar.before(Calendar.getInstance())) {
                    asrCalendar.add(Calendar.DAY_OF_MONTH, 1);
                }
                // Add logs for debugging
                Log.d("AlarmDebug", "Asr Calendar Time: " + asrCalendar.getTime());

                // Set up the alarm for Asr time
                AlarmHelper.setupAlarmWithVibration(requireContext(), asrCalendar, requestCodeAsr);


                Toast.makeText(getContext(), "Asr alarm set", Toast.LENGTH_SHORT).show();
            }
        });
        asr_time_alarm_0ff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asr_Time_Alarm.setVisibility(View.VISIBLE);
                asr_time_alarm_0ff.setVisibility(View.GONE);

                AlarmHelper.cancelAlarm(requireContext(), requestCodeAsr);

                Toast.makeText(getContext(), "Isr alarm cancelled", Toast.LENGTH_SHORT).show();
            }
        });


        maghrib_time_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maghrib_time_alarm.setVisibility(View.GONE);
                maghrib_time_alarm_0ff.setVisibility(View.VISIBLE);
                // Get the Maghrib time
                String maghribTime = maghribTimeTextView.getText().toString();

                // Parse the Maghrib time to extract hours, minutes, and AM/PM
                String[] maghribTimeParts = maghribTime.split(":");
                int maghribHour = Integer.parseInt(maghribTimeParts[0]);
                int maghribMinute = Integer.parseInt(maghribTimeParts[1].split("\\s+")[0]); // Remove "AM" or "PM"
                String amPm = maghribTimeParts[1].split("\\s+")[1];

                // Adjust hours for PM
                if (amPm.equalsIgnoreCase("PM") && maghribHour < 12) {
                    maghribHour += 12;
                }

                // Create a Calendar object for the Maghrib time
                Calendar maghribCalendar = Calendar.getInstance();
                maghribCalendar.set(Calendar.HOUR_OF_DAY, maghribHour);
                maghribCalendar.set(Calendar.MINUTE, maghribMinute);
                maghribCalendar.set(Calendar.SECOND, 0);

                if (maghribCalendar.before(Calendar.getInstance())) {
                    maghribCalendar.add(Calendar.DAY_OF_MONTH, 1);
                }

                // Add logs for debugging
                Log.d("AlarmDebug", "Maghrib Calendar Time: " + maghribCalendar.getTime());

                // Set up the alarm for Maghrib time
                AlarmHelper.setupAlarmWithVibration(requireContext(), maghribCalendar, requestCodeMaghrib);

                Toast.makeText(getContext(), "Maghrib alarm set", Toast.LENGTH_SHORT).show();
            }
        });
        maghrib_time_alarm_0ff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maghrib_time_alarm_0ff.setVisibility(View.GONE);
                maghrib_time_alarm.setVisibility(View.VISIBLE);

                AlarmHelper.cancelAlarm(requireContext(), requestCodeMaghrib);

                Toast.makeText(getContext(), "Maghrib alarm cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        isha_time_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isha_time_alarm.setVisibility(View.GONE);
                isha_time_alarm_0ff.setVisibility(View.VISIBLE);
                // Get the Isha time
                String ishaTime = ishaTimeTextView.getText().toString();

                // Parse the Isha time to extract hours, minutes, and AM/PM
                String[] ishaTimeParts = ishaTime.split(":");
                int ishaHour = Integer.parseInt(ishaTimeParts[0]);
                int ishaMinute = Integer.parseInt(ishaTimeParts[1].split("\\s+")[0]); // Remove "AM" or "PM"
                String amPm = ishaTimeParts[1].split("\\s+")[1];

                // Adjust hours for PM
                if (amPm.equalsIgnoreCase("PM") && ishaHour < 12) {
                    ishaHour += 12;
                }

                // Create a Calendar object for the Isha time
                Calendar ishaCalendar = Calendar.getInstance();
                ishaCalendar.set(Calendar.HOUR_OF_DAY, ishaHour);
                ishaCalendar.set(Calendar.MINUTE, ishaMinute);
                ishaCalendar.set(Calendar.SECOND, 0);

                if (ishaCalendar.before(Calendar.getInstance())) {
                    ishaCalendar.add(Calendar.DAY_OF_MONTH, 1);
                }
                // Add logs for debugging
                Log.d("AlarmDebug", "Isha Calendar Time: " + ishaCalendar.getTime());

                // Set up the alarm for Isha time
                AlarmHelper.setupAlarmWithVibration(requireContext(), ishaCalendar, requestCodeIsha);


                Toast.makeText(getContext(), "Isha alarm set", Toast.LENGTH_SHORT).show();
            }
        });
        isha_time_alarm_0ff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isha_time_alarm.setVisibility(View.VISIBLE);
                isha_time_alarm_0ff.setVisibility(View.GONE);

                AlarmHelper.cancelAlarm(requireContext(), requestCodeIsha);

                Toast.makeText(getContext(), "Isha alarm cancelled", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    private String loadJsonData(String filename) {
        try {
            InputStream is = requireActivity().getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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


    public static String convertDate(String dateInMilliseconds, String dateFormat) {
        return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
    }

    void loadData(Location location) {
        if (location != null) {
            Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (!addresses.isEmpty()) {
                    String city = addresses.get(0).getLocality();
                    String country = addresses.get(0).getCountryName();
                    long millis = Calendar.getInstance().getTimeInMillis();
                    location_place.setText(city + ", " + country);

                    String customURL = "https://api.aladhan.com/v1/calendarByCity/{YEAR}/{MONTH}?city={CITY}&country={COUNTRY}&method=2";
                    String url = customURL.replace("{YEAR}", convertDate(String.valueOf(millis), "yyyy"))
                            .replace("{MONTH}", convertDate(String.valueOf(millis), "MM"))
                            .replace("{CITY}", city)
                            .replace("{COUNTRY}", country);

                    RequestQueue queue = Volley.newRequestQueue(requireContext());
                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String status = response.getString("status");
                                if (status.equals("OK")) {
                                    JSONArray dataArray = response.getJSONArray("data");

                                    for (int i = 0; i < dataArray.length(); i++) {
                                        JSONObject timingObject = dataArray.getJSONObject(i).getJSONObject("timings");
                                        String date = dataArray.getJSONObject(i).getJSONObject("date").getString("readable");

                                        // Update your UI or process the data as needed
                                        updateUIWithData(timingObject, date);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(requireContext(), "JSON Parsing Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            Toast.makeText(requireContext(), "Network Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    queue.add(request);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {

            }
        }
        else {
            Toast.makeText(requireContext(), "Location not found", Toast.LENGTH_SHORT).show();
        }
    }
    // Update the UI with data from the API response
                private void updateUIWithData(JSONObject timingObject, String date) {
                    try {
                        String fajrTime = convertTo12HourFormat(removeTimeZoneSuffix(timingObject.getString("Fajr")));
                        String dhuhrTime = convertTo12HourFormat(removeTimeZoneSuffix(timingObject.getString("Dhuhr")));
                        String asrTime = convertTo12HourFormat(removeTimeZoneSuffix(timingObject.getString("Asr")));
                        String maghribTime = convertTo12HourFormat(removeTimeZoneSuffix(timingObject.getString("Maghrib")));
                        String ishaTime = convertTo12HourFormat(removeTimeZoneSuffix(timingObject.getString("Isha")));

                        // Update UI components with the modified times
                        fajrTimeTextView.setText(fajrTime);
                        dhuhrTimeTextView.setText(dhuhrTime);
                        asrTimeTextView.setText(asrTime);
                        maghribTimeTextView.setText(maghribTime);
                        ishaTimeTextView.setText(ishaTime);

                        // Update any other UI components as needed
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
                    }
                    else {
                        return timeWithSuffix.trim();
                    }
    }
}