package com.example.soen345_ticket.models;

import org.junit.Test;
import static org.junit.Assert.*;

public class ReservationTest {

    @Test
    public void testReservationConstructorAndGetters() {
        Reservation res = new Reservation("r1", "u1", "e1", "Concert", "2023-11-20", 2, "active");
        
        assertEquals("r1", res.getReservationId());
        assertEquals("u1", res.getUserId());
        assertEquals("e1", res.getEventId());
        assertEquals("Concert", res.getEventTitle());
        assertEquals("2023-11-20", res.getReservationDate());
        assertEquals(2, res.getQuantity());
        assertEquals("active", res.getStatus());
    }

    @Test
    public void testReservationSetters() {
        Reservation res = new Reservation();
        res.setReservationId("r2");
        res.setUserId("u2");
        res.setEventId("e2");
        res.setEventTitle("Theater");
        res.setReservationDate("2023-11-25");
        res.setQuantity(4);
        res.setStatus("cancelled");

        assertEquals("r2", res.getReservationId());
        assertEquals("u2", res.getUserId());
        assertEquals("e2", res.getEventId());
        assertEquals("Theater", res.getEventTitle());
        assertEquals("2023-11-25", res.getReservationDate());
        assertEquals(4, res.getQuantity());
        assertEquals("cancelled", res.getStatus());
    }
}
