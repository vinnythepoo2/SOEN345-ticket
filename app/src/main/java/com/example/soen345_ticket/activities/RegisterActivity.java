package com.example.soen345_ticket.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.soen345_ticket.databinding.ActivityRegisterBinding;
import com.example.soen345_ticket.models.User;
import com.example.soen345_ticket.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private UserRepository userRepository;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userRepository = createUserRepository();
        auth = getFirebaseAuth();

        binding.btnRegister.setOnClickListener(v -> {
            String fullName = binding.etFullName.getText().toString().trim();
            String email = binding.etEmail.getText().toString().trim();
            String phone = binding.etPhone.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();
            String role = "customer"; // Default role is customer

            if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                showToast("Please fill in all fields");
                return;
            }

            if (password.length() < 6) {
                showToast("Password must be at least 6 characters");
                return;
            }

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String userId = auth.getUid();
                    User user = new User(userId, fullName, email, phone, role);
                    userRepository.saveUser(user).addOnCompleteListener(saveTask -> {
                        if (saveTask.isSuccessful()) {
                            showToast("Registration successful");
                            navigateToMain();
                        } else {
                            showToast("Failed to save user info: " + (saveTask.getException() != null ? saveTask.getException().getMessage() : "Unknown error"));
                        }
                    });
                } else {
                    showToast("Registration failed: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                }
            });
        });

        binding.tvLogin.setOnClickListener(v -> {
            finish();
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

    protected void navigateToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
