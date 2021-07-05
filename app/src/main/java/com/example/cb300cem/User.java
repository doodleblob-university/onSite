package com.example.cb300cem;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class User implements Sites.SitesCallback, Coords.LocationCallback {
    Sites sites;
    Coords coords;

    private Context context;
    private FirebaseFirestore db;
    private FirebaseUser usr;
    private FusedLocationProviderClient fusedLocationProviderClient;

    public String name;
    private String lon;
    private String lat;

    public String currentsite;
    private String siteId;
    private String sitelat;
    private String sitelon;

    public User(Context c) {
        sites = new Sites();
        coords = new Coords();

        context = c;
        usr = FirebaseAuth.getInstance().getCurrentUser();
        name = usr.getDisplayName();
        db = FirebaseFirestore.getInstance();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

    }

    public void checkInOut(String sId){
        coords.getLocation(fusedLocationProviderClient);
        coords.setCallback(this);
        sites.getSiteInformation(db, sId);
        sites.setCallback(this);
    }

    @Override
    public void coordHandler(String lat, String lon) {
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    public void siteHandler(String site, String sId, String sitelat, String sitelon) {
        if(siteId != null && siteId.equals(sId)){
            Toast.makeText(context, "Checking out at "+site, Toast.LENGTH_SHORT).show();
            // checkout

            // clear values
            this.currentsite = null;
            this.siteId = null;
            this.sitelat = null;
            this.sitelon = null;
        } else {
            Toast.makeText(context, "Checking in at "+site, Toast.LENGTH_SHORT).show();
            // checkin

            // change values

        }

        this.currentsite = site;
        this.siteId = sId;
        this.sitelat = sitelat;
        this.sitelon = sitelon;
        // change textview



    }

    private void checkIn(){

    }

    private void checkOut(){

    }

}

