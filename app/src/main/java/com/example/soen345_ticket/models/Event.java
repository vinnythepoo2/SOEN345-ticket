package com.example.soen345_ticket.models;

import java.io.Serializable;

public class Event implements Serializable {
    private String eventId;
    private String title;
    private String description;
    private String category;
    private String location;
    private String date;
    private int totalSeats;
    private int availableSeats;
    private double price;
    private boolean isCancelled;

    public Event() {
        // Required empty constructor for Firestore
    }

    public Event(String eventId, String title, String description, String category, String location, String date, int totalSeats, int availableSeats, double price, boolean isCancelled) {
        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.location = location;
        this.date = date;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
        this.price = price;
        this.isCancelled = isCancelled;
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }

    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public boolean isCancelled() { return isCancelled; }
    public void setCancelled(boolean cancelled) { isCancelled = cancelled; }
}
