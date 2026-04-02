package com.example.soen345_ticket.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
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

        eventRepository = new EventRepository();

        event = (Event) getIntent().getSerializableExtra("event");
        if (event != null) {
            isEditMode = true;
            setupEditMode();
        }

        binding.btnSave.setOnClickListener(v -> saveEvent());
        binding.btnDelete.setOnClickListener(v -> deleteEvent());
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
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int totalSeats = Integer.parseInt(seatsStr);
        double price = Double.parseDouble(priceStr);

        if (isEditMode) {
            event.setTitle(title);
            event.setDescription(description);
            event.setCategory(category);
            event.setLocation(location);
            event.setDate(date);
            // Updating total seats also updates available seats if simplified, but usually more logic needed.
            // For MVP:
            int diff = totalSeats - event.getTotalSeats();
            event.setTotalSeats(totalSeats);
            event.setAvailableSeats(event.getAvailableSeats() + diff);
            event.setPrice(price);
            event.setCancelled(binding.cbIsCancelled.isChecked());

            eventRepository.updateEvent(event).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Event updated", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to update event", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Event newEvent = new Event(
                    null, title, description, category, location, date,
                    totalSeats, totalSeats, price, false
            );
            eventRepository.addEvent(newEvent).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Event added", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to add event", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void deleteEvent() {
        if (event != null) {
            eventRepository.deleteEvent(event.getEventId()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Event deleted", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to delete event", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
