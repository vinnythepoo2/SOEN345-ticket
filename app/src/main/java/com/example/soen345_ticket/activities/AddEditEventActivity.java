package com.example.soen345_ticket.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.soen345_ticket.databinding.ActivityAddEditEventBinding;
import com.example.soen345_ticket.models.Event;
import com.example.soen345_ticket.repositories.EventRepository;

public class AddEditEventActivity extends AppCompatActivity {
    private ActivityAddEditEventBinding binding;
    private EventRepository eventRepository;
    private Event event;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set the toolbar but disable the back arrow (Home button)
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle("Add/Edit Event");
        }

        eventRepository = createEventRepository();

        event = (Event) getIntent().getSerializableExtra("event");
        if (event != null) {
            isEditMode = true;
            setupEditMode();
        }

        binding.btnSave.setOnClickListener(v -> saveEvent());
        binding.btnDelete.setOnClickListener(v -> deleteEvent());
        
        // Manual back button listener - explicitly goes to AdminDashboard
        binding.btnBack.setOnClickListener(v -> goToDashboard());
    }

    protected EventRepository createEventRepository() {
        return new EventRepository();
    }

    private void goToDashboard() {
        Intent intent = new Intent(this, AdminDashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        goToDashboard();
    }

    private void setupEditMode() {
        binding.tvHeader.setText("Edit Event");
        binding.etTitle.setText(event.getTitle());
        binding.etDescription.setText(event.getDescription());
        binding.etCategory.setText(event.getCategory());
        binding.etLocation.setText(event.getLocation());
        binding.etDate.setText(event.getDate());
        binding.etTotalSeats.setText(String.valueOf(event.getTotalSeats()));
        binding.etPrice.setText(String.valueOf(event.getPrice()));
        binding.cbIsCancelled.setVisibility(View.VISIBLE);
        binding.cbIsCancelled.setChecked(event.isCancelled());
        binding.btnDelete.setVisibility(View.VISIBLE);
    }

    private void saveEvent() {
        String title = binding.etTitle.getText().toString().trim();
        String description = binding.etDescription.getText().toString().trim();
        String category = binding.etCategory.getText().toString().trim();
        String location = binding.etLocation.getText().toString().trim();
        String date = binding.etDate.getText().toString().trim();
        String seatsStr = binding.etTotalSeats.getText().toString().trim();
        String priceStr = binding.etPrice.getText().toString().trim();

        if (title.isEmpty() || seatsStr.isEmpty() || priceStr.isEmpty()) {
            showToast("Please fill in all required fields");
            return;
        }

        int totalSeats;
        double price;
        try {
            totalSeats = Integer.parseInt(seatsStr);
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            showToast("Invalid number format for seats or price");
            return;
        }

        if (totalSeats < 0) {
            showToast("Seats cannot be negative");
            return;
        }

        if (price < 0) {
            showToast("Price cannot be negative");
            return;
        }

        if (isEditMode) {
            event.setTitle(title);
            event.setDescription(description);
            event.setCategory(category);
            event.setLocation(location);
            event.setDate(date);
            int diff = totalSeats - event.getTotalSeats();
            event.setTotalSeats(totalSeats);
            event.setAvailableSeats(event.getAvailableSeats() + diff);
            event.setPrice(price);
            event.setCancelled(binding.cbIsCancelled.isChecked());

            eventRepository.updateEvent(event).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    showToast("Event updated");
                    goToDashboard();
                } else {
                    showToast("Failed to update event");
                }
            });
        } else {
            Event newEvent = new Event(
                    null, title, description, category, location, date,
                    totalSeats, totalSeats, price, false
            );
            eventRepository.addEvent(newEvent).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    showToast("Event added");
                    goToDashboard();
                } else {
                    showToast("Failed to add event");
                }
            });
        }
    }

    private void deleteEvent() {
        if (event != null) {
            eventRepository.deleteEvent(event.getEventId()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    showToast("Event deleted");
                    goToDashboard();
                } else {
                    showToast("Failed to delete event");
                }
            });
        }
    }

    protected void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
