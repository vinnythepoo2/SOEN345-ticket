package com.example.soen345_ticket.models;

import org.junit.Test;
import static org.junit.Assert.*;

public class EventTest {

    @Test
    public void testEventConstructorAndGetters() {
        Event event = new Event("e1", "Concert", "Music event", "Music", "Montreal", "2023-12-01", 100, 50, 25.0, false);
        
        assertEquals("e1", event.getEventId());
        assertEquals("Concert", event.getTitle());
        assertEquals("Music event", event.getDescription());
        assertEquals("Music", event.getCategory());
        assertEquals("Montreal", event.getLocation());
        assertEquals("2023-12-01", event.getDate());
        assertEquals(100, event.getTotalSeats());
        assertEquals(50, event.getAvailableSeats());
        assertEquals(25.0, event.getPrice(), 0.001);
        assertFalse(event.isCancelled());
    }

    @Test
    public void testEventSetters() {
        Event event = new Event();
        event.setEventId("e2");
        event.setTitle("Theater");
        event.setDescription("Play");
        event.setCategory("Arts");
        event.setLocation("Laval");
        event.setDate("2023-12-05");
        event.setTotalSeats(200);
        event.setAvailableSeats(180);
        event.setPrice(45.5);
        event.setCancelled(true);

        assertEquals("e2", event.getEventId());
        assertEquals("Theater", event.getTitle());
        assertEquals("Play", event.getDescription());
        assertEquals("Arts", event.getCategory());
        assertEquals("Laval", event.getLocation());
        assertEquals("2023-12-05", event.getDate());
        assertEquals(200, event.getTotalSeats());
        assertEquals(180, event.getAvailableSeats());
        assertEquals(45.5, event.getPrice(), 0.001);
        assertTrue(event.isCancelled());
    }
}
