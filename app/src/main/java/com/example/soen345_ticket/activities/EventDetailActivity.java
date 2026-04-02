package com.example.soen345_ticket.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.soen345_ticket.databinding.ActivityEventDetailBinding;
import com.example.soen345_ticket.models.Event;

public class EventDetailActivity extends AppCompatActivity {
    private ActivityEventDetailBinding binding;
    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set the toolbar but disable the back arrow (Home button)
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle("Event Details");
        }

        event = (Event) getIntent().getSerializableExtra("event");

        if (event != null) {
            binding.tvTitle.setText(event.getTitle());
            binding.tvCategory.setText(event.getCategory());
            binding.tvDate.setText(event.getDate());
            binding.tvLocation.setText(event.getLocation());
            binding.tvDescription.setText(event.getDescription());
            binding.tvPrice.setText("Price: $" + event.getPrice());
            binding.tvAvailableSeats.setText("Available Seats: " + event.getAvailableSeats());

            if (event.getAvailableSeats() <= 0) {
                binding.btnBookNow.setEnabled(false);
                binding.btnBookNow.setText("Sold Out");
            }

            binding.btnBookNow.setOnClickListener(v -> {
                Intent intent = new Intent(this, ReservationActivity.class);
                intent.putExtra("event", event);
                startActivity(intent);
            });
            
            // Fixed back button: explicitly return to MainActivity
            binding.btnBack.setOnClickListener(v -> {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        } else {
            Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
