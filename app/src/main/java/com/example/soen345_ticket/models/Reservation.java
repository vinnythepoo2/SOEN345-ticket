package com.example.soen345_ticket.models;

import java.io.Serializable;

public class Reservation implements Serializable {
    private String reservationId;
    private String userId;
    private String eventId;
    private String eventTitle;
    private String reservationDate;
    private int quantity;
    private String status; // "active", "cancelled"

    public Reservation() {
        // Required empty constructor for Firestore
    }

    public Reservation(String reservationId, String userId, String eventId, String eventTitle, String reservationDate, int quantity, String status) {
        this.reservationId = reservationId;
        this.userId = userId;
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.reservationDate = reservationDate;
        this.quantity = quantity;
        this.status = status;
    }

    public String getReservationId() { return reservationId; }
    public void setReservationId(String reservationId) { this.reservationId = reservationId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getEventTitle() { return eventTitle; }
    public void setEventTitle(String eventTitle) { this.eventTitle = eventTitle; }

    public String getReservationDate() { return reservationDate; }
    public void setReservationDate(String reservationDate) { this.reservationDate = reservationDate; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
