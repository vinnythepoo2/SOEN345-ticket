package com.example.soen345_ticket.services;

import com.example.soen345_ticket.models.Event;
import com.example.soen345_ticket.models.Reservation;

public class EmailNotificationService implements NotificationService {
    private final EmailService emailService;
    private final ReservationEmailNotifier notifier;

    public EmailNotificationService(EmailService emailService, ReservationEmailNotifier notifier) {
        this.emailService = emailService;
        this.notifier = notifier;
    }

    @Override
    public void sendReservationConfirmation(String email, Reservation reservation) {
        // Create a placeholder event with the title from reservation
        Event placeholderEvent = new Event();
        placeholderEvent.setTitle(reservation.getEventTitle());
        
        notifier.sendConfirmationEmail(
            emailService,
            placeholderEvent,
            email,
            reservation.getQuantity(),
            reservation.getReservationDate()
        );
    }
}
