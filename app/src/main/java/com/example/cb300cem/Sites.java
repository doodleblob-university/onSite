package com.example.cb300cem;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import static android.content.ContentValues.TAG;

public class Sites {

    public Sites(){
    }

    SitesCallback sitesCallback;
    public interface SitesCallback {
        void siteHandler(String site, String siteId, String sitelat, String sitelon);
    }
    public void setCallback(SitesCallback sitesCallback){
        this.sitesCallback = sitesCallback;
    }

    public void getSiteInformation(FirebaseFirestore db, String sId) {

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
}
