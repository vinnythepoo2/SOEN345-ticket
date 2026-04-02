package com.example.soen345_ticket.models;

import static org.junit.Assert.*;
import org.junit.Test;

public class EventTest {

    @Test
    public void testEventCreation() {
        Event event = new Event("1", "Concert", "Music event", "Music", "Montreal", "2023-12-01", 100, 100, 50.0, false);
        assertEquals("Concert", event.getTitle());
        assertEquals(100, event.getAvailableSeats());
        assertEquals(50.0, event.getPrice(), 0.0);
    }

    @Test
    public void testSetAvailableSeats() {
        Event event = new Event();
        event.setAvailableSeats(50);
        assertEquals(50, event.getAvailableSeats());
    }
}
