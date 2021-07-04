package com.example.cb300cem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

public class LoadApp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load);


        Intent intent = new Intent(this, Login.class);
        startActivity(intent);

        //Intent intent = new Intent(this, Main.class);
        //startActivity(intent);




        finish(); // close loading activity
    }
}

