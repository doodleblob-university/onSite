package com.example.cb300cem;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static android.content.ContentValues.TAG;

public class Sites {

    public Sites(){
    }

    // callbacks to handle data from async tasks
    SitesCallback sitesCallback;
    public interface SitesCallback {
        void siteHandler(String site, String siteId, String sitelat, String sitelon);
        void checkInHandler(String activeSite);
    }
    public void setCallback(SitesCallback sitesCallback){
        this.sitesCallback = sitesCallback;
    }

    public void getSiteInformation(FirebaseFirestore db, String sId) {
        // get document 'sId' from database collection 'sites'
        db.collection("sites").document(sId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String currentsite = document.getData().get("name").toString();
                        String sitelat = document.getData().get("latitude").toString();
                        String sitelon = document.getData().get("longitude").toString();
                        sitesCallback.siteHandler(currentsite, sId, sitelat, sitelon);

                    } else {
                        // does not exist
                        Log.d(TAG, "Site does not exist. Invalid QR code");

                    }
                } else {
                    //task.getException();

                }

            }
        });
    }

    public void checkIn(FirebaseFirestore db, Map<String, Object> timesheet, String uId){
        // add new document to the 'timesheet' collection with the data in the 'timesheet' map object
        // - reference to user's document in db
        // - reference to site's document in db
        // - time clocked in (unix seconds)
        // - time clocked out (unix seconds) -> null for now
        db.collection("timesheet")
                .add(timesheet)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String activeSite = documentReference.getId(); // the created document's id
                        setActive(db, uId, activeSite); // update user's document with their current active site
                        sitesCallback.checkInHandler(activeSite);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        //handle
                    }
                });

    }

    private void setActive(FirebaseFirestore db, String uId, String usId){
        // update user's document with their current active site
        db.collection("users").document(uId)
                .update("active", usId)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w(TAG, "Error updating document", e);
                    }
                });
    }

    public void checkOut(FirebaseFirestore db, String doc, String time, String uId){
        // update specific document in the 'timesheet' collection with the user's clock out time
        db.collection("timesheet").document(doc)
                .update("out", time)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        clearActive(db, uId); // remove the 'timesheet' document from the user's active site
                        //Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w(TAG, "Error updating document", e);
                    }
                });
    }

    private void clearActive(FirebaseFirestore db, String uId){
        // clear the user's active site
        db.collection("users").document(uId)
                .update("active", null)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w(TAG, "Error updating document", e);
                    }
                });
    }
}
