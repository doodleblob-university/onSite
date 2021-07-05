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

import static android.content.ContentValues.TAG;

public class User {

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
        context = c;
        usr = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        setName();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

    }

    private void setName() {
        //gets name from datastore -> if not present, stores name provided from google
        final String[] tempname = {usr.getDisplayName()};
        db.collection("users").document(usr.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                try {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            tempname[0] = document.getData().get("name").toString();

                        } else {
                            // user does not exist
                            // should not occur during normal operation
                            Log.d(TAG, "User does not exist.  Consult system admin");
                        }
                    } else {
                        throw task.getException();
                    }
                } catch (NullPointerException e) {
                    Log.d(TAG, "User's name not present in datastore");
                    changeName(tempname[0]);
                    // name field does not exist in database
                } catch (Exception e) {
                    // unexpected exception
                    Log.d(TAG, "Unexpected Exception: " + e);

                }
            }
        }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                name = tempname[0];
                Toast.makeText(context, "Hello " + name + "!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void changeName(String n) {
        // updates name in database
        db.collection("users").document(usr.getUid())
                .update("name", n)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "User's name successfully updated!");
                        name = n; //update name locally too!
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating user's name", e);
                    }
                });
    }

    public void checkInOut(String site) {
        getSiteInformation(site);
        Log.d("10", currentsite); //throws error if current site not valid
        getLocation(); // 0.01° = 1.11 km accuracy -> 0.05+-



    }

    private void getSiteInformation(String site) {
        db.collection("sites").document(site).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        currentsite = document.getData().get("name").toString();
                        sitelat = document.getData().get("latitude").toString();
                        sitelon = document.getData().get("longitude").toString();

                    } else {
                        // does not exist
                        Log.d(TAG, "Site does not exist. Invalid QR code");
                        currentsite = null;
                    }
                } else {
                    //task.getException();
                    currentsite = null;
                }

            }
        }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                return;
            }
        });
    }

    @SuppressLint("MissingPermission") //TODO: Permissions
    private void getLocation() {

        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Location> task) {
                Location location = task.getResult();
                if( location != null ){
                    lat = String.valueOf(location.getLatitude());
                    lon = String.valueOf(location.getLongitude());

                }
            }
        }).addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Location> task) {
                return;
            }
        });
    }
    /*
    public void signOut(){

    }
    */

}
