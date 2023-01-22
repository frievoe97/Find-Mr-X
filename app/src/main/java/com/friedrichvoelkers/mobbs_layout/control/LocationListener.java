package com.friedrichvoelkers.mobbs_layout.control;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Looper;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;

import java.util.Random;

public class LocationListener {

    private final Context context;
    private final LocationCallback locationCallback;
    private LocationRequest mLocationRequest;

    public LocationListener (Context context) {
        this.context = context;
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult (LocationResult locationResult) {
                onLocationChanged(locationResult.getLastLocation());
            }
        };
    }

    // Trigger new location updates at interval
    @SuppressLint ("MissingPermission")
    public void startLocationUpdates () {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        SettingsClient settingsClient = LocationServices.getSettingsClient(context);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        getFusedLocationProviderClient(context).requestLocationUpdates(mLocationRequest,
                locationCallback, Looper.myLooper());
    }

    //
    private void onLocationChanged (Location location) {
        Random r = new Random();
        double randomValue1 = (0.07) * r.nextDouble() - 0.035;
        double randomValue2 = (0.07) * r.nextDouble() - 0.035;
        double latitude = location.getLatitude() + randomValue1;
        double longitude = location.getLongitude() + randomValue2;
        FireBaseController.updateMyLocation(latitude, longitude);
    }

    public void stopLocationUpdates () {
        getFusedLocationProviderClient(context).removeLocationUpdates(locationCallback);
    }

}
