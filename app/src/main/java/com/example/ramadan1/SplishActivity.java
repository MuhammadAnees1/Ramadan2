package com.example.ramadan1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplishActivity extends AppCompatActivity {
    private static final int SPLASH_DURATION = 3000; // 5 seconds


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splish);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                startActivity(new Intent(SplishActivity.this, MainActivity.class));
                finish(); // Finish the current activity to prevent going back to it
            }
        }, SPLASH_DURATION);
    }
}
