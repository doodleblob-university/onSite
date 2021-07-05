package com.example.cb300cem;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;
import android.widget.TextView;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private String activeSite;

    public User(Context c) {
        sites = new Sites();
        coords = new Coords();

        context = c;
        usr = FirebaseAuth.getInstance().getCurrentUser();
        name = usr.getDisplayName();
        db = FirebaseFirestore.getInstance();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

    }
    //region callback for updating interface when checking in
    User.UICallback uiCallback;
    public interface UICallback {
        void changeText(String text, int color);
    }
    public void setCallback(User.UICallback uiCallback){
        this.uiCallback = uiCallback;
    }
    //endregion

    public void checkInOut(String sId){ // called when qr code is recognised in an image
        // sId = what should be the site Id on a valid QR code
        coords.getLocation(fusedLocationProviderClient, sId); //get location
        coords.setCallback(this);
    }

    @Override
    public void coordHandler(String lat, String lon, String sId) {

        this.lat = lat;
        this.lon = lon;
        sites.getSiteInformation(db, sId);
        sites.setCallback(this);
    }

    @Override
    public void siteHandler(String site, String sId, String sitelat, String sitelon) {
        //
        //default ui values
        String uiText = "Not on site";
        int uiColor = Color.BLACK;

        if(siteId != null && siteId.equals(sId)){ // if the previous siteId equals the recently scanned one
            // CHECKING OUT OF SITE
            Toast.makeText(context, "Checking out at "+site, Toast.LENGTH_SHORT).show();
            // checkout
            checkOut(); //

            // reset values
            uiText = "Not on site";
            uiColor = Color.BLACK;
            this.currentsite = null;
            this.siteId = null;
            this.sitelat = null;
            this.sitelon = null;
        } else { // TODO: add if condition in order to alert user to sign out of previous site before entering a new one
            // CHECKING INTO SITE
            Toast.makeText(context, "Checking in at "+site, Toast.LENGTH_SHORT).show();
            // set values
            this.sitelat = sitelat;
            this.sitelon = sitelon;
            if (checkUserSiteLocation()){// if user is near site
                checkIn(sId); //
                // change values
                this.currentsite = site;
                this.siteId = sId;
                uiText = site;
                uiColor = Color.GREEN;
            }else{
                Toast.makeText(context, "You are too far away from the site to check in", Toast.LENGTH_SHORT).show();
            }
        }
        // change ui
        uiCallback.changeText(uiText, uiColor); // callback to Main, where the ui can be changed to express status

    }

    private Boolean checkUserSiteLocation(){
        //
        // parse strings -> turning coords back into doubles
        Double uLat = Double.parseDouble(this.lat);
        Double uLon = Double.parseDouble(this.lon);
        Double sLat = Double.parseDouble(this.sitelat);
        Double sLon = Double.parseDouble(this.sitelon);
        if( (uLat - 0.01) < sLat && sLat < (uLat + 0.01) ){ // ensure user is within 1km of site north-south
            if( (uLon - 0.01) < sLon && sLon < (uLon + 0.01) ){ // ensure user is within 1km of site east-west
                // gps coords are in degrees -> approximately 0.01° = 1.11 km
                // user is within approx 1km to the site
                return true;
            }
        }
        return false; // user out of range of site
    }

    private void checkIn(String siteId){
        //
        // compile data for entry into database
        DocumentReference userRef = db.document("users/"+this.usr.getUid()); // location of user's db document
        DocumentReference siteRef = db.document("sites/"+siteId); // location of site's db document
        // merge data into list of key-value pairs
        Map<String, Object> timesheet = new HashMap<>();
        timesheet.put("site", siteRef);
        timesheet.put("user", userRef);
        timesheet.put("in", getCurrentUnixStr());
        timesheet.put("out", null);
        //
        sites.checkIn(db, timesheet, usr.getUid());
        sites.setCallback(this);
    }

    @Override
    public void checkInHandler(String activeSite) {
        // callback from checkIn, with the returned active site document id (from the 'timetables' collection in db)
        this.activeSite = activeSite;
    }

    private void checkOut(){
        // call checkOut function in the 'Sites' class with the necessary args
        sites.checkOut(db, activeSite, getCurrentUnixStr(), usr.getUid());
    }

    private String getCurrentUnixStr(){
        // get current unix time, in seconds, as a string
        long currTime = System.currentTimeMillis() / 1000L;
        return Long.toString(currTime);
    }


}

