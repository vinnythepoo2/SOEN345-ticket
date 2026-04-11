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
import com.example.soen345_ticket.models.User;
import com.example.soen345_ticket.repositories.ReservationRepository;
import com.example.soen345_ticket.repositories.UserRepository;
import com.example.soen345_ticket.services.EmailNotificationService;
import com.example.soen345_ticket.services.EmailService;
import com.example.soen345_ticket.services.NetworkChecker;
import com.example.soen345_ticket.services.NotificationService;
import com.example.soen345_ticket.services.ReservationEmailNotifier;
import com.example.soen345_ticket.services.ReservationService;
import com.example.soen345_ticket.utils.NetworkUtils;
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
    private ReservationService reservationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReservationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle("Confirm Reservation");
        }

        event = (Event) getIntent().getSerializableExtra("event");
        reservationRepository = createReservationRepository();
        userRepository = createUserRepository();
        reservationService = createReservationService();

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
                    showToast("Enter quantity", Toast.LENGTH_SHORT);
                    return;
                }
                int quantity = Integer.parseInt(qtyStr);
                if (quantity <= 0) {
                    showToast("Quantity must be at least 1", Toast.LENGTH_SHORT);
                    return;
                }
                if (quantity > event.getAvailableSeats()) {
                    showToast("Not enough seats available", Toast.LENGTH_SHORT);
                    return;
                }

                confirmBooking(quantity);
            });
            
            binding.btnBack.setOnClickListener(v -> onBackPressed());
        }
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
        FirebaseUser fbUser = getCurrentFirebaseUser();
        if (fbUser == null) return;

        User user = new User();
        user.setUserId(fbUser.getUid());
        user.setEmail(fbUser.getEmail());

        String date = getCurrentTimestamp();
        Reservation reservation = new Reservation(
                null,
                user.getUserId(),
                event.getEventId(),
                event.getTitle(),
                date,
                quantity,
                "active"
        );

        reservationService.processReservation(reservation, quantity, user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                showToast("Reservation confirmed!", Toast.LENGTH_LONG);
                navigateToMain();
            } else {
                showToast(
                        "Failed: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"),
                        Toast.LENGTH_SHORT
                );
            }
        });
    }

    protected ReservationRepository createReservationRepository() {
        return new ReservationRepository();
    }

    protected UserRepository createUserRepository() {
        return new UserRepository();
    }

    protected EmailService createEmailService() {
        return new EmailService();
    }

    protected ReservationEmailNotifier createReservationEmailNotifier() {
        return new ReservationEmailNotifier();
    }

    protected ReservationService createReservationService() {
        NotificationService notificationService = new EmailNotificationService(createEmailService(), createReservationEmailNotifier());
        NetworkChecker networkChecker = () -> NetworkUtils.isNetworkAvailable(this);
        return new ReservationService(createReservationRepository(), notificationService, networkChecker);
    }

    protected FirebaseUser getCurrentFirebaseUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    protected String getCurrentTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
    }

    protected void showToast(String message, int duration) {
        Toast.makeText(this, message, duration).show();
    }

    protected void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
