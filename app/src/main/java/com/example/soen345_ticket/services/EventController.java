package com.example.soen345_ticket.services;

import com.example.soen345_ticket.models.Event;
import com.example.soen345_ticket.repositories.EventRepository;
import com.example.soen345_ticket.utils.FilterHelper;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.Query;

public class EventController {
    private final EventRepository eventRepository;

    public EventController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Task<Void> addEvent(Event event) {
        return eventRepository.addEvent(event);
    }

    public Query searchEventsByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return eventRepository.getEventsQuery();
        }
        return eventRepository.getAllEventsForAdminQuery()
                .orderByChild("title")
                .startAt(title)
                .endAt(FilterHelper.buildRangeEnd(title));
    }
}
