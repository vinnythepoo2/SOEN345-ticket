package com.example.soen345_ticket.models;

import org.junit.Test;
import static org.junit.Assert.*;

public class UserTest {

    @Test
    public void testUserConstructorAndGetters() {
        User user = new User("uid123", "John Doe", "john@example.com", "1234567890", "customer");
        
        assertEquals("uid123", user.getUserId());
        assertEquals("John Doe", user.getFullName());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("1234567890", user.getPhone());
        assertEquals("customer", user.getRole());
    }

    @Test
    public void testUserSetters() {
        User user = new User();
        user.setUserId("uid456");
        user.setFullName("Jane Smith");
        user.setEmail("jane@example.com");
        user.setPhone("0987654321");
        user.setRole("admin");

        assertEquals("uid456", user.getUserId());
        assertEquals("Jane Smith", user.getFullName());
        assertEquals("jane@example.com", user.getEmail());
        assertEquals("0987654321", user.getPhone());
        assertEquals("admin", user.getRole());
    }
}
