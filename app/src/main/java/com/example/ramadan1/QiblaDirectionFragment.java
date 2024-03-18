package com.example.ramadan1;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

public class QiblaDirectionFragment extends Fragment implements SensorEventListener, LocationListener {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int LOCATION_SETTINGS_REQUEST_CODE = 1002;
    private ProgressDialog progressDialog;
    ImageView imageView, imageView1;
    private SensorManager sensorManager;
    private LocationManager locationManager;
    private static final float ALPHA = 0.1f; // Smoothing factor
    private float[] smoothedGravity = new float[3];
    private float[] smoothedGeomagnetic = new float[3];
    private float azimuth = 0;
    private Location currentLocation;
    private final Location kaabaLocation = new Location("");
    private boolean isLocationUpdated = false;

    public QiblaDirectionFragment() {
        // Set Kaaba's location
        kaabaLocation.setLatitude(21.422487);
        kaabaLocation.setLongitude(39.826206);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qibla_direction, container, false);
        imageView = view.findViewById(R.id.compass);
        imageView1 = view.findViewById(R.id.qiblaImage);

        TextView english_date1 = view.findViewById(R.id.english_date1);
        TextView islamic_date = view.findViewById(R.id.Islamic_date);
        TextView location_place = view.findViewById(R.id.location_place);


        SharedPreferences preferences = requireActivity().getSharedPreferences("LocationPreferences", Context.MODE_PRIVATE);
        String city = preferences.getString("city", "");
        String country = preferences.getString("country", "");
        location_place.setText(city + ", " + country);


        String todayDateEnglish = DateHelper.getCurrentDateEnglish();
        english_date1.setText(todayDateEnglish);
        String todayDateIslamic = DateHelper.getCurrentDateIslamic();
        islamic_date.setText(todayDateIslamic);



        initializeSensors();
        checkLocationPermission();
        return view;
    }

    private void initializeSensors() {
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
        } else {
            // Check if location services are globally enabled
            if (isLocationEnabled()) {
                showProgressDialog();
                // Location services are enabled, proceed with getting the current location
                getCurrentLocation();
            } else {
                // Location services are not enabled, show dialog to enable
                showEnableLocationDialog();
                dismissProgressDialog();
            }
        }
    }

    private void getCurrentLocation() {
        // Check if location permissions are granted
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permissions not granted, you can request permissions here using ActivityCompat.requestPermissions
            // For example:
            // ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            Toast.makeText(getActivity(), "Location permission is required", Toast.LENGTH_SHORT).show();
            return; // Exit the method as we cannot proceed without permissions
        }

        if (!isLocationEnabled()) {
            // Location services are not enabled, show dialog to enable
            showEnableLocationDialog();

            return; // Exit the method as we cannot proceed without location services
        }
        // Permissions are granted, proceed with getting the location
        if (!isLocationUpdated) {
            Location lastKnownLocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location lastKnownLocationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);


            if (lastKnownLocationGPS != null) {
                currentLocation = lastKnownLocationGPS;
            } else if (lastKnownLocationNetwork != null) {
                currentLocation = lastKnownLocationNetwork;
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            }
        }

        if (currentLocation != null) {
            isLocationUpdated = true;
            updateCompassImage();
        }
        dismissProgressDialog();

    }



    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);
        getCurrentLocation();
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        locationManager.removeUpdates(this);
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Apply low-pass filter to accelerometer readings
            smoothedGravity[0] = ALPHA * event.values[0] + (1 - ALPHA) * smoothedGravity[0];
            smoothedGravity[1] = ALPHA * event.values[1] + (1 - ALPHA) * smoothedGravity[1];
            smoothedGravity[2] = ALPHA * event.values[2] + (1 - ALPHA) * smoothedGravity[2];
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            // Apply low-pass filter to magnetometer readings
            smoothedGeomagnetic[0] = ALPHA * event.values[0] + (1 - ALPHA) * smoothedGeomagnetic[0];
            smoothedGeomagnetic[1] = ALPHA * event.values[1] + (1 - ALPHA) * smoothedGeomagnetic[1];
            smoothedGeomagnetic[2] = ALPHA * event.values[2] + (1 - ALPHA) * smoothedGeomagnetic[2];
        }
        if (smoothedGravity != null && smoothedGeomagnetic != null) {
            updateDirection();
        }
    }

    private void updateDirection() {
        float[] R = new float[9];
        float[] I = new float[9];
        boolean success = SensorManager.getRotationMatrix(R, I, smoothedGravity, smoothedGeomagnetic);
        if (success) {
            float[] orientation = new float[3];
            SensorManager.getOrientation(R, orientation);
            azimuth = (float) Math.toDegrees(orientation[0]);
            azimuth = (azimuth + 360) % 360;
            updateCompassImage();
        }
    }
    private void updateCompassImage() {
        if (currentLocation != null) {
            float bearingToKaaba = currentLocation.bearingTo(kaabaLocation);
            float rotation = azimuth - bearingToKaaba;
            rotation = (rotation + 360) % 360;
            imageView.setRotation(-rotation);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showProgressDialog();  // Show loading bar while fetching location
            getCurrentLocation();
        } else {
            Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        updateCompassImage();
        dismissProgressDialog();  // Dismiss loading bar after location is fetched

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
    @Override
    public void onProviderEnabled(String provider) {}
    @Override
    public void onProviderDisabled(String provider) {}
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void requestLocationPermission() {
        new AlertDialog.Builder(requireActivity())
                .setTitle("Location Permission")
                .setMessage("This app needs access to your location to function properly.")
                .setPositiveButton("Grant Permission", (dialog, which) -> {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();

    }
    private void showEnableLocationDialog() {
        new AlertDialog.Builder(requireActivity())
                .setTitle("Enable Location")
                .setMessage("Location services are required for this app. Please enable location services.")
                .setPositiveButton("Enable", (dialog, which) -> {
                    // Open the location settings page
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent, LOCATION_SETTINGS_REQUEST_CODE);
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_SETTINGS_REQUEST_CODE) {
            // Check if location services are now enabled
            if (isLocationEnabled()) {
                getCurrentLocation();
            } else {
                // User did not enable location services
                showEnableLocationDialog();
            }
        }
    }
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage("Fetching location...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}