package com.example.soen345_ticket.models;

import static org.junit.Assert.*;
import org.junit.Test;

public class UserTest {

    @Test
    public void testUserCreation() {
        User user = new User("user123", "John Doe", "john@example.com", "1234567890", "customer");
        assertEquals("user123", user.getUserId());
        assertEquals("John Doe", user.getFullName());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("1234567890", user.getPhone());
        assertEquals("customer", user.getRole());
    }

    @Test
    public void testUserSetters() {
        User user = new User();
        user.setUserId("user456");
        user.setFullName("Jane Smith");
        user.setEmail("jane@example.com");
        user.setPhone("0987654321");
        user.setRole("admin");

        assertEquals("user456", user.getUserId());
        assertEquals("Jane Smith", user.getFullName());
        assertEquals("jane@example.com", user.getEmail());
        assertEquals("0987654321", user.getPhone());
        assertEquals("admin", user.getRole());
    }
}
