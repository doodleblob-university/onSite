package com.example.cb300cem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoadApp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load);

        // check if user is logged in from a previous session
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(null == user) {
            startActivity(new Intent(this, Main.class));
        }else{
            // user NOT logged in locally -> go to login page
            startActivity(new Intent(this, Login.class));
        }
        //startActivity(new Intent(this, Main.class));
        finish(); // close loading activity
    }
}

