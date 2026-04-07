package com.example.soen345_ticket.utils;

/** Pure booking calculations, kept Android-free for unit testing. */
public class BookingHelper {

    /** Returns the total charge for a booking. */
    public static double calculateTotal(int quantity, double pricePerTicket) {
        return quantity * pricePerTicket;
    }

    private BookingHelper() {}
}
