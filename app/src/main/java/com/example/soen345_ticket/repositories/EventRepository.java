package com.example.soen345_ticket.repositories;

import com.example.soen345_ticket.models.Event;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class EventRepository {
    private final DatabaseReference db;
    private static final String EVENTS_PATH = "events";

    public EventRepository() {
        db = FirebaseDatabase.getInstance().getReference(EVENTS_PATH);
    }

    public Task<Void> addEvent(Event event) {
        DatabaseReference newRef = db.push();
        event.setEventId(newRef.getKey());
        return newRef.setValue(event);
    }

    public Task<Void> updateEvent(Event event) {
        return db.child(event.getEventId()).setValue(event);
    }

    public Task<Void> deleteEvent(String eventId) {
        return db.child(eventId).removeValue();
    }

    public Query getAllEventsForAdminQuery() {
        return db;
    }

    public Query getEventsQuery() {
        return db.orderByChild("cancelled").equalTo(false);
    }
}
