package com.example.soen345_ticket.repositories;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.soen345_ticket.models.Reservation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;

public class ReservationRepository {
    private final DatabaseReference db;
    private static final String RESERVATIONS_PATH = "reservations";
    private static final String EVENTS_PATH = "events";

    public ReservationRepository() {
        db = FirebaseDatabase.getInstance().getReference();
    }

    public Task<Void> createReservation(Reservation reservation, int quantity) {
        DatabaseReference eventRef = db.child(EVENTS_PATH).child(reservation.getEventId());
        DatabaseReference reservationRef = db.child(RESERVATIONS_PATH).push();
        reservation.setReservationId(reservationRef.getKey());
        
        TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();

        eventRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Long availableSeats = currentData.child("availableSeats").getValue(Long.class);
                if (availableSeats == null) {
                    return Transaction.success(currentData);
                }

                if (availableSeats < quantity) {
                    return Transaction.abort();
                }

                currentData.child("availableSeats").setValue(availableSeats - quantity);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if (error != null) {
                    tcs.setException(error.toException());
                } else if (!committed) {
                    tcs.setException(new Exception("Transaction aborted: Not enough seats"));
                } else {
                    reservationRef.setValue(reservation).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            tcs.setResult(null);
                        } else {
                            tcs.setException(task.getException());
                        }
                    });
                }
            }
        });
        
        return tcs.getTask();
    }

    public Task<Void> cancelReservation(Reservation reservation) {
        DatabaseReference eventRef = db.child(EVENTS_PATH).child(reservation.getEventId());
        DatabaseReference reservationRef = db.child(RESERVATIONS_PATH).child(reservation.getReservationId());
        
        TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();

        eventRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Long availableSeats = currentData.child("availableSeats").getValue(Long.class);
                if (availableSeats != null) {
                    currentData.child("availableSeats").setValue(availableSeats + reservation.getQuantity());
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if (error != null) {
                    tcs.setException(error.toException());
                } else {
                    reservationRef.child("status").setValue("cancelled").addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            tcs.setResult(null);
                        } else {
                            tcs.setException(task.getException());
                        }
                    });
                }
            }
        });
        
        return tcs.getTask();
    }

    public Query getReservationsQueryByUser(String userId) {
        return db.child(RESERVATIONS_PATH).orderByChild("userId").equalTo(userId);
    }
}
