package com.example.soen345_ticket.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.soen345_ticket.R;
import com.example.soen345_ticket.databinding.ActivityMyReservationsBinding;
import com.example.soen345_ticket.models.Reservation;
import com.example.soen345_ticket.repositories.ReservationRepository;
import com.example.soen345_ticket.repositories.UserRepository;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class MyReservationsActivity extends AppCompatActivity {
    private ActivityMyReservationsBinding binding;
    private ReservationRepository reservationRepository;
    private UserRepository userRepository;
    private FirebaseRecyclerAdapter<Reservation, ReservationViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyReservationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Reservations");
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        reservationRepository = new ReservationRepository();
        userRepository = new UserRepository();

        setupRecyclerView();
        
        // Manual back button listener
        binding.btnBack.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void setupRecyclerView() {
        String userId = userRepository.getCurrentUserId();
        FirebaseRecyclerOptions<Reservation> options = new FirebaseRecyclerOptions.Builder<Reservation>()
                .setQuery(reservationRepository.getReservationsQueryByUser(userId), Reservation.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Reservation, ReservationViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ReservationViewHolder holder, int position, @NonNull Reservation model) {
                holder.tvTitle.setText(model.getEventTitle());
                holder.tvDate.setText("Reserved on: " + model.getReservationDate());
                holder.tvQty.setText("Quantity: " + model.getQuantity());
                holder.tvStatus.setText("Status: " + model.getStatus());

                if ("active".equals(model.getStatus())) {
                    holder.btnCancel.setVisibility(View.VISIBLE);
                    holder.btnCancel.setOnClickListener(v -> {
                        reservationRepository.cancelReservation(model).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(MyReservationsActivity.this, "Reservation cancelled", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MyReservationsActivity.this, "Failed to cancel: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"), Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
                } else {
                    holder.btnCancel.setVisibility(View.GONE);
                }
            }

            @NonNull
            @Override
            public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reservation, parent, false);
                return new ReservationViewHolder(view);
            }
        };

        binding.rvReservations.setLayoutManager(new LinearLayoutManager(this));
        binding.rvReservations.setAdapter(adapter);
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public static class ReservationViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvQty, tvStatus;
        Button btnCancel;

        public ReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvEventTitle);
            tvDate = itemView.findViewById(R.id.tvReservationDate);
            tvQty = itemView.findViewById(R.id.tvQuantity);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnCancel = itemView.findViewById(R.id.btnCancel);
        }
    }
}
