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

        userRepository = new UserRepository();
        auth = FirebaseAuth.getInstance();

        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    userRepository.getCurrentUser().addOnSuccessListener(dataSnapshot -> {
                        if (dataSnapshot.exists()) {
                            String role = dataSnapshot.child("role").getValue(String.class);
                            if ("admin".equals(role)) {
                                startActivity(new Intent(this, AdminDashboardActivity.class));
                            } else {
                                startActivity(new Intent(this, MainActivity.class));
                            }
                            finish();
                        }
                    }).addOnFailureListener(e -> {
                        Toast.makeText(this, "Error getting user info", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show();
                }
            });
        });

        binding.tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }
}
