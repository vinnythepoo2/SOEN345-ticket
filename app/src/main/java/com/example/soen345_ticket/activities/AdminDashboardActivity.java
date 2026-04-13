package com.example.soen345_ticket.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.soen345_ticket.R;
import com.example.soen345_ticket.databinding.ActivityAdminDashboardBinding;
import com.example.soen345_ticket.models.Event;
import com.example.soen345_ticket.repositories.EventRepository;
import com.example.soen345_ticket.repositories.UserRepository;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

public class AdminDashboardActivity extends AppCompatActivity {
    private ActivityAdminDashboardBinding binding;
    private EventRepository eventRepository;
    private UserRepository userRepository;
    private FirebaseRecyclerAdapter<Event, AdminEventViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Admin Dashboard");
        }

        eventRepository = createEventRepository();
        userRepository = createUserRepository();

        setupRecyclerView();

        binding.fabAddEvent.setOnClickListener(v -> {
            startActivity(new Intent(this, AddEditEventActivity.class));
        });

        binding.btnLogout.setOnClickListener(v -> {
            userRepository.logout();
            navigateToLogin();
        });
    }

    protected EventRepository createEventRepository() {
        return new EventRepository();
    }

    protected UserRepository createUserRepository() {
        return new UserRepository();
    }

    protected void navigateToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void setupRecyclerView() {
        FirebaseRecyclerOptions<Event> options = new FirebaseRecyclerOptions.Builder<Event>()
                .setQuery(eventRepository.getAllEventsForAdminQuery(), Event.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Event, AdminEventViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AdminEventViewHolder holder, int position, @NonNull Event model) {
                holder.tvTitle.setText(model.getTitle());
                holder.tvDate.setText(model.getDate());
                holder.tvStatus.setText(model.isCancelled() ? "CANCELLED" : "ACTIVE");
                holder.tvSeats.setText("Available: " + model.getAvailableSeats() + "/" + model.getTotalSeats());

                holder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(AdminDashboardActivity.this, AddEditEventActivity.class);
                    intent.putExtra("event", model);
                    startActivity(intent);
                });
            }

            @NonNull
            @Override
            public AdminEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_event, parent, false);
                return new AdminEventViewHolder(view);
            }

            @Override
            public void onError(@NonNull DatabaseError error) {
                super.onError(error);
                showToast("Database Error: " + error.getMessage());
            }
        };

        binding.rvEvents.setLayoutManager(new WrappedLinearLayoutManager(this));
        binding.rvEvents.setAdapter(adapter);
    }

    protected void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
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

    public static class AdminEventViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvStatus, tvSeats;

        public AdminEventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvEventTitle);
            tvDate = itemView.findViewById(R.id.tvEventDate);
            tvStatus = itemView.findViewById(R.id.tvEventStatus);
            tvSeats = itemView.findViewById(R.id.tvEventSeats);
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
