package com.example.soen345_ticket.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.soen345_ticket.databinding.ActivityLoginBinding;
import com.example.soen345_ticket.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private UserRepository userRepository;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userRepository = createUserRepository();
        auth = getFirebaseAuth();

        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                showToast("Please fill in all fields");
                return;
            }

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    userRepository.getCurrentUser().addOnSuccessListener(dataSnapshot -> {
                        if (dataSnapshot.exists()) {
                            String role = dataSnapshot.child("role").getValue(String.class);
                            if ("admin".equals(role)) {
                                navigateToAdminDashboard();
                            } else {
                                navigateToMain();
                            }
                            finish();
                        }
                    }).addOnFailureListener(e -> {
                        showToast("Error getting user info");
                    });
                } else {
                    showToast("Authentication failed");
                }
            });
        });

        binding.tvRegister.setOnClickListener(v -> {
            navigateToRegister();
        });
    }

    protected UserRepository createUserRepository() {
        return new UserRepository();
    }

    protected FirebaseAuth getFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    protected void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    protected void navigateToAdminDashboard() {
        startActivity(new Intent(this, AdminDashboardActivity.class));
    }

    protected void navigateToMain() {
        startActivity(new Intent(this, MainActivity.class));
    }

    protected void navigateToRegister() {
        startActivity(new Intent(this, RegisterActivity.class));
    }
}
