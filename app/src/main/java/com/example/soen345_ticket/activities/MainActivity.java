package com.example.soen345_ticket.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.soen345_ticket.R;
import com.example.soen345_ticket.databinding.ActivityMainBinding;
import com.example.soen345_ticket.models.Event;
import com.example.soen345_ticket.repositories.EventRepository;
import com.example.soen345_ticket.repositories.UserRepository;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.Query;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private EventRepository eventRepository;
    private UserRepository userRepository;
    private FirebaseRecyclerAdapter<Event, EventViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        eventRepository = new EventRepository();
        userRepository = new UserRepository();

        setupRecyclerView(eventRepository.getEventsQuery());

        binding.btnFilter.setOnClickListener(v -> {
            performSearch();
        });

        binding.spinnerFilterType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String searchText = binding.etSearch.getText().toString().trim();
                if (!searchText.isEmpty()) {
                    performSearch();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        binding.btnMyReservations.setOnClickListener(v -> {
            startActivity(new Intent(this, MyReservationsActivity.class));
        });

        binding.btnLogout.setOnClickListener(v -> {
            userRepository.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void performSearch() {
        String searchText = binding.etSearch.getText().toString().trim();
        String filterType = binding.spinnerFilterType.getSelectedItem().toString().toLowerCase();

        if (!searchText.isEmpty()) {
            Query query = eventRepository.getAllEventsForAdminQuery()
                    .orderByChild(filterType)
                    .startAt(searchText)
                    .endAt(searchText + "\uf8ff");
            updateRecyclerView(query);
        } else {
            updateRecyclerView(eventRepository.getEventsQuery());
        }
    }

    private void setupRecyclerView(Query query) {
        FirebaseRecyclerOptions<Event> options = new FirebaseRecyclerOptions.Builder<Event>()
                .setQuery(query, Event.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Event, EventViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull EventViewHolder holder, int position, @NonNull Event model) {
                holder.tvTitle.setText(model.getTitle());
                holder.tvDate.setText(model.getDate());
                holder.tvLocation.setText(model.getLocation());
                holder.tvPrice.setText("$" + model.getPrice());
                holder.tvSeats.setText("Seats: " + model.getAvailableSeats());
                holder.tvCategory.setText(model.getCategory());

                holder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, EventDetailActivity.class);
                    intent.putExtra("event", model);
                    startActivity(intent);
                });
            }

            @NonNull
            @Override
            public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
                return new EventViewHolder(view);
            }
        };

        binding.rvEvents.setLayoutManager(new WrappedLinearLayoutManager(this));
        binding.rvEvents.setAdapter(adapter);
    }

    private void updateRecyclerView(Query query) {
        FirebaseRecyclerOptions<Event> options = new FirebaseRecyclerOptions.Builder<Event>()
                .setQuery(query, Event.class)
                .build();
        adapter.updateOptions(options);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) adapter.stopListening();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvLocation, tvPrice, tvSeats, tvCategory;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvEventTitle);
            tvDate = itemView.findViewById(R.id.tvEventDate);
            tvLocation = itemView.findViewById(R.id.tvEventLocation);
            tvPrice = itemView.findViewById(R.id.tvEventPrice);
            tvSeats = itemView.findViewById(R.id.tvAvailableSeats);
            tvCategory = itemView.findViewById(R.id.tvEventCategory);
        }
    }

    private static class WrappedLinearLayoutManager extends LinearLayoutManager {
        public WrappedLinearLayoutManager(Context context) {
            super(context);
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                Log.e("RecyclerView", "IndexOutOfBoundsException caught in onLayoutChildren");
            }
        }
    }
}
