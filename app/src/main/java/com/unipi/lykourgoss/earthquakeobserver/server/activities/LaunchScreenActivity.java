package com.unipi.lykourgoss.earthquakeobserver.server.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.unipi.lykourgoss.earthquakeobserver.server.R;

public class LaunchScreenActivity extends AppCompatActivity {

    private static final String TAG = "LaunchScreenActivity";

    private static final long LAUNCH_SCREEN_TIME_OUT = 1 * 1000;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lanch_screen);

        firebaseAuth = FirebaseAuth.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(LaunchScreenActivity.this, SignInActivity.class));
                } else {
                    startActivity(new Intent(LaunchScreenActivity.this, MainActivity.class));
                }
                finish();
            }
        }, LAUNCH_SCREEN_TIME_OUT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}
