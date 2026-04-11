package com.example.soen345_ticket.services;

import com.example.soen345_ticket.models.Event;
import com.example.soen345_ticket.repositories.EventRepository;
import com.google.android.gms.tasks.Task;

public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Task<Void> createEvent(Event event) {
        return eventRepository.addEvent(event);
    }
}
