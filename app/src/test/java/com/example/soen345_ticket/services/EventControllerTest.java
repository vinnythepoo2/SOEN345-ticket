package com.example.soen345_ticket.services;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.soen345_ticket.models.Event;
import com.example.soen345_ticket.repositories.EventRepository;
import com.google.firebase.database.Query;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class EventControllerTest {

    private EventRepository repository;
    private EventController controller;
    private EventService service;

    @Before
    public void setUp() {
        repository = mock(EventRepository.class);
        controller = new EventController(repository);
        service = new EventService(repository);
    }

    @Test
    public void addEvent_callsRepository() {
        Event event = new Event();
        controller.addEvent(event);
        verify(repository).addEvent(event);
    }

    @Test
    public void service_createEvent_callsRepository() {
        Event event = new Event();
        service.createEvent(event);
        verify(repository).addEvent(event);
    }

    @Test
    public void searchEvents_withNull_returnsAllQuery() {
        when(repository.getEventsQuery()).thenReturn(mock(Query.class));
        assertNotNull(controller.searchEventsByTitle(null));
        verify(repository).getEventsQuery();
    }

    @Test
    public void searchEvents_withEmpty_returnsAllQuery() {
        when(repository.getEventsQuery()).thenReturn(mock(Query.class));
        assertNotNull(controller.searchEventsByTitle("  "));
        verify(repository).getEventsQuery();
    }

    @Test
    public void searchEvents_withText_returnsFilteredQuery() {
        Query mockQuery = mock(Query.class);
        when(repository.getAllEventsForAdminQuery()).thenReturn(mockQuery);
        when(mockQuery.orderByChild(any())).thenReturn(mockQuery);
        when(mockQuery.startAt(any(String.class))).thenReturn(mockQuery);
        when(mockQuery.endAt(any(String.class))).thenReturn(mockQuery);

        assertNotNull(controller.searchEventsByTitle("Rock"));
        verify(repository).getAllEventsForAdminQuery();
    }
}
