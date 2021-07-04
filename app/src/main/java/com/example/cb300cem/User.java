package com.example.cb300cem;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

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

    public String name;
    private Object active = null;

    public User(Context c){
        context = c;
        usr = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        setName();
    }

    private void setName(){
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
                }catch (NullPointerException e){
                    Log.d(TAG, "User's name not present in datastore");
                    changeName(tempname[0]);
                    // name field does not exist in database
                }catch (Exception e){
                    // unexpected exception
                    Log.d(TAG, "Unexpected Exception: "+e);

                }
            }
        }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                name = tempname[0];
                Toast.makeText(context, "Hello "+name+"!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void changeName(String n){
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

    public void checkIn(String site){

    }

    public String getSiteInformation(){
        return "";
    }

    public void signOut(){

    }

}
