package com.example.cb300cem;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;

public class Coords {

    public Coords(){
    }

    Coords.LocationCallback locationCallback;
    public interface LocationCallback {
        void coordHandler(String lat, String lon, String sId);
    }
    public void setCallback(Coords.LocationCallback locationCallback){
        this.locationCallback = locationCallback;
    }

    @SuppressLint("MissingPermission")
    public void getLocation(FusedLocationProviderClient fusedLocationProviderClient, String sId) {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<android.location.Location>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<android.location.Location> task) {
                android.location.Location location = task.getResult();
                String lat = String.valueOf(location.getLatitude());
                String lon = String.valueOf(location.getLongitude());
                locationCallback.coordHandler(lat, lon, sId);
            }
        });
    }



}
