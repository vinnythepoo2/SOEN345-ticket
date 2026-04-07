package com.example.soen345_ticket.services;

import com.example.soen345_ticket.models.Event;
import com.example.soen345_ticket.utils.BookingHelper;
import java.util.logging.Logger;

public class ReservationEmailNotifier {
    private static final Logger LOGGER = Logger.getLogger(ReservationEmailNotifier.class.getName());

    public void sendConfirmationEmail(
            EmailService emailService,
            Event event,
            String userEmail,
            int quantity,
            String bookingDate
    ) {
        if (emailService == null || event == null || userEmail == null) {
            return;
        }

        double totalPrice = BookingHelper.calculateTotal(quantity, event.getPrice());
        emailService.sendBookingConfirmation(
                userEmail,
                event.getTitle(),
                event.getDate(),
                event.getLocation(),
                quantity,
                totalPrice,
                bookingDate,
                new EmailService.EmailCallback() {
                    @Override
                    public void onSuccess() {
                        LOGGER.info("Reservation confirmation email sent.");
                    }

                    @Override
                    public void onFailure(String error) {
                        LOGGER.warning("Reservation confirmation email failed: " + error);
                    }
                }
        );
    }
}
