package com.example.soen345_ticket.models;

import static org.junit.Assert.*;
import org.junit.Test;

public class ReservationTest {
    @Test
    public void testReservationCreation() {
        Reservation res = new Reservation("res1", "user1", "event1", "Concert", "2023-12-01", 2, "active");
        assertEquals("active", res.getStatus());
        assertEquals(2, res.getQuantity());
        assertEquals("Concert", res.getEventTitle());
    }
}
