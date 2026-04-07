package com.example.soen345_ticket.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.soen345_ticket.databinding.ActivityReservationBinding;
import com.example.soen345_ticket.models.Event;
import com.example.soen345_ticket.models.Reservation;
import com.example.soen345_ticket.repositories.ReservationRepository;
import com.example.soen345_ticket.repositories.UserRepository;
import com.example.soen345_ticket.services.EmailService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReservationActivity extends AppCompatActivity {
    private ActivityReservationBinding binding;
    private Event event;
    private ReservationRepository reservationRepository;
    private UserRepository userRepository;
    private EmailService emailService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReservationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set the toolbar but disable the back arrow (Home button)
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle("Confirm Reservation");
        }

        event = (Event) getIntent().getSerializableExtra("event");
        reservationRepository = new ReservationRepository();
        userRepository = new UserRepository();
        emailService = new EmailService();

        if (event != null) {
            binding.tvEventTitle.setText(event.getTitle());
            binding.tvEventPrice.setText("Price per ticket: $" + event.getPrice());
            updateTotal();

            binding.etQuantity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    updateTotal();
                }
            });

            binding.btnConfirm.setOnClickListener(v -> {
                String qtyStr = binding.etQuantity.getText().toString();
                if (qtyStr.isEmpty()) {
                    Toast.makeText(this, "Enter quantity", Toast.LENGTH_SHORT).show();
                    return;
                }
                int quantity = Integer.parseInt(qtyStr);
                if (quantity <= 0) {
                    Toast.makeText(this, "Quantity must be at least 1", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (quantity > event.getAvailableSeats()) {
                    Toast.makeText(this, "Not enough seats available", Toast.LENGTH_SHORT).show();
                    return;
                }

                confirmBooking(quantity);
            });
            
            // Explicit Back button listener
            binding.btnBack.setOnClickListener(v -> {
                onBackPressed();
            });
        }
    }

    private void sendConfirmationEmail(int quantity, String bookingDate) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null || currentUser.getEmail() == null) return;

        double totalPrice = quantity * event.getPrice();

        emailService.sendBookingConfirmation(
                currentUser.getEmail(),
                event.getTitle(),
                event.getDate(),
                event.getLocation(),
                quantity,
                totalPrice,
                bookingDate,
                new EmailService.EmailCallback() {
                    @Override public void onSuccess() {}
                    @Override public void onFailure(String error) {
                        android.util.Log.e("ReservationActivity", "Email failed: " + error);
                    }
                }
        );
    }

    private void updateTotal() {
        String qtyStr = binding.etQuantity.getText().toString();
        if (!qtyStr.isEmpty()) {
            int quantity = Integer.parseInt(qtyStr);
            double total = quantity * event.getPrice();
            binding.tvTotalPrice.setText(String.format(Locale.getDefault(), "Total: $%.2f", total));
        } else {
            binding.tvTotalPrice.setText("Total: $0.00");
        }
    }

    private void confirmBooking(int quantity) {
        String userId = userRepository.getCurrentUserId();
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());

        Reservation reservation = new Reservation(
                null,
                userId,
                event.getEventId(),
                event.getTitle(),
                date,
                quantity,
                "active"
        );

        reservationRepository.createReservation(reservation, quantity).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Reservation confirmed!", Toast.LENGTH_LONG).show();
                sendConfirmationEmail(quantity, date);
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Failed: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
