package com.example.soen345_ticket.services;

import com.example.soen345_ticket.models.Reservation;

public interface NotificationService {
    void sendReservationConfirmation(String email, Reservation reservation);
}
