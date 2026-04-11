package com.example.soen345_ticket.services;

import com.example.soen345_ticket.models.Reservation;
import com.example.soen345_ticket.models.User;
import com.example.soen345_ticket.repositories.ReservationRepository;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final NotificationService notificationService;
    private final NetworkChecker networkChecker;

    public ReservationService(ReservationRepository reservationRepository, 
                              NotificationService notificationService,
                              NetworkChecker networkChecker) {
        this.reservationRepository = reservationRepository;
        this.notificationService = notificationService;
        this.networkChecker = networkChecker;
    }

    public Task<Void> processReservation(Reservation reservation, int quantity, User user) {
        TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();
        
        if (networkChecker != null && !networkChecker.isAvailable()) {
            tcs.setException(new Exception("Network unavailable. Please check your connection."));
            return tcs.getTask();
        }

        reservationRepository.createReservation(reservation, quantity).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                notificationService.sendReservationConfirmation(user.getEmail(), reservation);
                tcs.setResult(null);
            } else {
                tcs.setException(task.getException());
            }
        });
        
        return tcs.getTask();
    }
}
