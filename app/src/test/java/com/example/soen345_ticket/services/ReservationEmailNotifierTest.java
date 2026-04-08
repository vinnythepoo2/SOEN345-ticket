package com.example.soen345_ticket.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.example.soen345_ticket.models.Event;
import java.util.concurrent.Executor;
import org.junit.Test;

public class ReservationEmailNotifierTest {

    private static class CapturingEmailService extends EmailService {
        String toEmail;
        String eventTitle;
        String eventDate;
        String eventLocation;
        int quantity;
        double totalPrice;
        String bookingDate;
        EmailCallback callback;
        boolean called;

        CapturingEmailService() {
            super(new Executor() {
                @Override
                public void execute(Runnable command) {
                    command.run();
                }
            }, jsonBody -> 200);
        }

        @Override
        public void sendBookingConfirmation(
                String toEmail,
                String eventTitle,
                String eventDate,
                String eventLocation,
                int quantity,
                double totalPrice,
                String bookingDate,
                EmailCallback callback
        ) {
            this.toEmail = toEmail;
            this.eventTitle = eventTitle;
            this.eventDate = eventDate;
            this.eventLocation = eventLocation;
            this.quantity = quantity;
            this.totalPrice = totalPrice;
            this.bookingDate = bookingDate;
            this.callback = callback;
            this.called = true;
        }
    }

    @Test
    public void sendConfirmationEmail_forwardsValuesAndCalculatesTotal() {
        ReservationEmailNotifier notifier = new ReservationEmailNotifier();
        CapturingEmailService emailService = new CapturingEmailService();
        Event event = new Event(
                "evt-1",
                "Jazz Night",
                "desc",
                "music",
                "Montreal",
                "2024-06-15",
                100,
                90,
                49.99,
                false
        );

        notifier.sendConfirmationEmail(emailService, event, "user@example.com", 2, "2024-01-10 09:00");

        assertTrue(emailService.called);
        assertEquals("user@example.com", emailService.toEmail);
        assertEquals("Jazz Night", emailService.eventTitle);
        assertEquals("2024-06-15", emailService.eventDate);
        assertEquals("Montreal", emailService.eventLocation);
        assertEquals(2, emailService.quantity);
        assertEquals(99.98, emailService.totalPrice, 0.0001);
        assertEquals("2024-01-10 09:00", emailService.bookingDate);
        assertNotNull(emailService.callback);
    }

    @Test
    public void sendConfirmationEmail_withNullInputs_doesNothing() {
        ReservationEmailNotifier notifier = new ReservationEmailNotifier();
        CapturingEmailService emailService = new CapturingEmailService();
        Event event = new Event();

        notifier.sendConfirmationEmail(null, event, "user@example.com", 1, "date");
        assertNull(emailService.callback);

        notifier.sendConfirmationEmail(emailService, null, "user@example.com", 1, "date");
        assertNull(emailService.callback);

        notifier.sendConfirmationEmail(emailService, event, null, 1, "date");
        assertNull(emailService.callback);
    }

    @Test
    public void sendConfirmationEmail_callbackHandlesBothOutcomes() {
        ReservationEmailNotifier notifier = new ReservationEmailNotifier();
        CapturingEmailService emailService = new CapturingEmailService();
        Event event = new Event(
                "evt-1",
                "Jazz Night",
                "desc",
                "music",
                "Montreal",
                "2024-06-15",
                100,
                90,
                50.0,
                false
        );

        notifier.sendConfirmationEmail(emailService, event, "user@example.com", 1, "2024-01-10 09:00");

        assertNotNull(emailService.callback);
        emailService.callback.onSuccess();
        emailService.callback.onFailure("HTTP 500");
    }
}
