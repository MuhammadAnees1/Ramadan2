package com.example.ramadan1;

import static android.service.controls.ControlsProviderService.TAG;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;
public class Sehri_iftar_JsonHelper {
     static List <Sehri_iftari_class> sehriIftarList = new ArrayList<>();
    TreeMap<String, Sehri_iftari_class> sehriIftarMap = new TreeMap<>();
    int i=0;

void updateData(Context context, Location currentLocation,DataLoadListener listener, SehriIftarFragment fragment) {
    sehriIftarList.clear();
    // Define date range from 11th March to 9th April
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.YEAR, 2024); // Set the year to 2024
    calendar.set(Calendar.MONTH, Calendar.MARCH); // Set the month to March (March is indexed from 0)
    calendar.set(Calendar.DAY_OF_MONTH, 12); // Set the starting day to 11th March

    Calendar endDate = Calendar.getInstance();
    endDate.set(Calendar.YEAR, 2024); // Set the year to 2024
    endDate.set(Calendar.MONTH, Calendar.APRIL); // Set the month to April (April is indexed from 3)
    endDate.set(Calendar.DAY_OF_MONTH, 9); // Set the ending day to 9th April

    // Loop through the date range and call loadData for each date
    while (!calendar.after(endDate)) {
        Date date = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String dateString = sdf.format(date);
        // Call loadData function with the current location and application context
        i++;
         loadData(currentLocation, context, i, dateString, fragment);


        // Move to the next day
        calendar.add(Calendar.DAY_OF_MONTH, 1);
    }
    listener.onDataLoaded(sehriIftarList);
}



    interface DataLoadListener {
        void onDataLoaded(List<Sehri_iftari_class> sehriIftarList);
    }

    void loadData(Location location, Context context, int id, String dateString, SehriIftarFragment fragment) {
        if (location != null) {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (!addresses.isEmpty()) {
                    String city = addresses.get(0).getLocality();
                    String country = addresses.get(0).getCountryName();
                    String customURL = "https://api.aladhan.com/v1/timingsByAddress/{day}-{MONTH}-{year}?address={city}%2C+{country}";
                    String[] dateParts = dateString.split("-");
                    String day = dateParts[0];
                    String month = dateParts[1];
                    String year = dateParts[2];

                    String url = customURL.replace("{year}", year)
                            .replace("{MONTH}", month)
                            .replace("{day}", day)
                            .replace("{city}", city)
                            .replace("{country}", country);

                    Log.d("url", "loadData: " + url);

                    RequestQueue queue = Volley.newRequestQueue(context);
                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
                        try {
                            String status = response.getString("status");
                            if (status.equals("OK")) {
                                JSONObject dataObject = response.getJSONObject("data");
                                JSONObject timingObject = dataObject.getJSONObject("timings");
                                String date = dataObject.getJSONObject("date").getString("readable");
                                updateUIWithData(timingObject, date, id, fragment);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });

                    queue.add(request);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {

            }
        } else {
            // Handle the case where location is null
        }
    }
    private void updateUIWithData(JSONObject timingObject, String date, int id, SehriIftarFragment fragment) {
        try {
            String fajrTime = convertTo12HourFormat(removeTimeZoneSuffix(timingObject.getString("Fajr")));
            String maghribTime = convertTo12HourFormat(removeTimeZoneSuffix(timingObject.getString("Maghrib")));

            if ("Jaffari".equals(fragment.selectedRadioButton)) {
                Log.d(TAG, "updateUIWithData: "+fragment.selectedRadioButton);
                // Adjust iftar and sehri times based on the selected Jaffari radio button
                fajrTime = adjustTime(fajrTime, -10);
                maghribTime = adjustTime(maghribTime, 10);
                String day = getDayFromDate(date);

                // Clear the list only if it's the first entry for "Jaffari"
                if (sehriIftarList.isEmpty()) {
                    sehriIftarList.clear();
                }

                sehriIftarList.add(new Sehri_iftari_class(date, fajrTime, maghribTime, day, id));
                sehriIftarList.sort(Comparator.comparingInt(Sehri_iftari_class::getId));
                fragment.updateAdapter(sehriIftarList);
            } else {
                // Get the day corresponding to the date
                String day = getDayFromDate(date);

                // Create an instance of Sehri_iftari_class and add it to the list
                sehriIftarList.add(new Sehri_iftari_class(date, fajrTime, maghribTime, day, id));
                sehriIftarList.sort(Comparator.comparingInt(Sehri_iftari_class::getId));
                fragment.updateAdapter(sehriIftarList);
            }

            // Update any other UI components as needed
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getDayFromDate(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        try {
            Date date = sdf.parse(dateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

            // Convert day of week to the corresponding name
            String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
            return days[dayOfWeek - 1];
        } catch (Exception e) {
            e.printStackTrace();
            return ""; // Return empty string in case of error
        }
    }

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
}
