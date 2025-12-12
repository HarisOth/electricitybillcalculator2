package com.example.electricitybillcalculator2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper; // Amalan terbaik: gunakan Looper.getMainLooper()

public class SplashActivity extends Activity {

    // Guna static final untuk nilai delay
    private static final int SPLASH_DELAY_MS = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Pastikan nama layout anda betul (R.layout.activity_splash)
        setContentView(R.layout.activity_splash);

        // Delay 2 seconds sebelum masuk MainActivity
        // Amalan Terbaik: Gunakan Handler dengan Looper untuk keselamatan
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }, SPLASH_DELAY_MS);
    }
}
