package com.example.ramadan1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements LocationListener {
    BottomNavigationView bottomNavigationView;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int LOCATION_SETTINGS_REQUEST_CODE = 1002;
    private ProgressDialog progressDialog;
    private LocationManager locationManager;
    Fragment currentFragment;
    private boolean isLocationUpdated = false;
    Location currentLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment2()).commit();

        //        startService(new Intent(this, AlarmService.class));

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.menu_prayer) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NimazTimeFragment()).commit();
                    return true;
                } else if (itemId == R.id.menu_compass) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new QiblaDirectionFragment()).commit();
                    return true;
                } else if (itemId == R.id.menu_home) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment2()).commit();
                    return true;
                } else if (itemId == R.id.menu_Sehri_time) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SehriIftarFragment()).commit();
                    return true;
                } else if (itemId == R.id.menu_calendar) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingFragment()).commit();
                    return true;
                }

                return false;
            }
        });
    }
    void getCurrentFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        currentFragment =   fragmentManager.findFragmentById(R.id.fragment_container);
    }
    void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
        } else {
            // Check if location services are globally enabled
            if (isLocationEnabled()) {
                showProgressDialog();
                // Location services are enabled, proceed with getting the current location
                getCurrentLocation();
                getCurrentFragment();

            } else {
                // Location services are not enabled, show dialog to enable
                dismissProgressDialog();
                showEnableLocationDialog();
            }

        }
        getCurrentLocation();
        if(currentFragment instanceof HomeFragment2){
            showProgressDialog();
            ((HomeFragment2) currentFragment).loadData(currentLocation);
        dismissProgressDialog();
        }
    }
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
    private void requestLocationPermission() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Location Permission")
                .setMessage("This app needs access to your location to function properly.")
                .setPositiveButton("Grant Permission", (dialog, which) -> {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_PERMISSION_REQUEST_CODE);
                    checkLocationPermission();
                    getCurrentLocation();
                    if(currentFragment instanceof HomeFragment2){
                        ((HomeFragment2) currentFragment).loadData(currentLocation);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> {

                    dialog.dismiss();
                    onCancel();
                })
                .create()
                .show();
    }
    private void onCancel() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Permission Denied")
                .setMessage("This app needs access to your location to function properly.")
                .setPositiveButton("Grant Permission", (dialog, which) -> {
                    checkLocationPermission();
                })
                .create()
                .show();
    }
    private void showEnableLocationDialog() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Enable Location")
                .setMessage("Location services are required for this app. Please enable location services.")
                .setPositiveButton("Enable", (dialog, which) -> {
                    // Open the location settings page
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent, LOCATION_SETTINGS_REQUEST_CODE);
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                    showProgressDialog();
                    onCancel();
                })
                .create()
                .show();
    }
    void getCurrentLocation() {
        // Check if location permissions are granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permissions not granted, you can request permissions here using ActivityCompat.requestPermissions
            // For example:
            // ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show();
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
        dismissProgressDialog();
    }


    private void showProgressDialog() {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Fetching location...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }
    public void onStatusChanged(String provider, int status, Bundle extras) {}
    @Override
    public void onProviderEnabled(String provider) {}
    @Override
    public void onProviderDisabled(String provider) {}
}
