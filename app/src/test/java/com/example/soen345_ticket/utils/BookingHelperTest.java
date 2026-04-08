package com.example.soen345_ticket.utils;

import static org.junit.Assert.*;
import org.junit.Test;

public class BookingHelperTest {

    @Test
    public void calculateTotal_multipleTickets_returnsCorrectTotal() {
        assertEquals(150.0, BookingHelper.calculateTotal(3, 50.0), 0.001);
    }

    @Test
    public void calculateTotal_singleTicket_returnsPriceUnchanged() {
        assertEquals(75.0, BookingHelper.calculateTotal(1, 75.0), 0.001);
    }

    @Test
    public void calculateTotal_zeroQuantity_returnsZero() {
        assertEquals(0.0, BookingHelper.calculateTotal(0, 50.0), 0.001);
    }

    @Test
    public void calculateTotal_freeEvent_returnsZero() {
        assertEquals(0.0, BookingHelper.calculateTotal(5, 0.0), 0.001);
    }

    @Test
    public void calculateTotal_fractionalPrice_isAccurate() {
        assertEquals(29.97, BookingHelper.calculateTotal(3, 9.99), 0.001);
    }
}
