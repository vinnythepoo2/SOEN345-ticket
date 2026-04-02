package com.example.soen345_ticket.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.example.soen345_ticket.databinding.ActivitySplashBinding;
import com.example.soen345_ticket.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

public class SplashActivity extends AppCompatActivity {
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySplashBinding binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userRepository = new UserRepository();

        new Handler().postDelayed(() -> {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                userRepository.getCurrentUser().addOnSuccessListener(dataSnapshot -> {
                    if (dataSnapshot.exists()) {
                        String role = dataSnapshot.child("role").getValue(String.class);
                        if ("admin".equals(role)) {
                            startActivity(new Intent(SplashActivity.this, AdminDashboardActivity.class));
                        } else {
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        }
                    } else {
                        // User exists in Auth but not in Database, sign out
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    }
                    finish();
                }).addOnFailureListener(e -> {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                });
            } else {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        }, 2000);
    }
}
