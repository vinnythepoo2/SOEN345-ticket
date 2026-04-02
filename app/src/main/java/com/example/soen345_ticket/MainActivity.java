package com.example.soen345_ticket;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.soen345_ticket.activities.SplashActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This is a placeholder; the app entry point is SplashActivity.
        // Redirecting to SplashActivity in case this is somehow launched.
        startActivity(new Intent(this, SplashActivity.class));
        finish();
    }
}
