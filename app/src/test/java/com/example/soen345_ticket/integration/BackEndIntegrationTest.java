package com.example.soen345_ticket.integration;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.soen345_ticket.models.Event;
import com.example.soen345_ticket.repositories.EventRepository;
import com.example.soen345_ticket.services.EventController;
import com.google.firebase.database.Query;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class BackEndIntegrationTest {

    private EventRepository mockRepo;
    private EventController controller;

    @Before
    public void setUp() {
        mockRepo = mock(EventRepository.class);
        controller = new EventController(mockRepo);
    }

    @Test
    public void testControllerRepositoryIntegration_AddEvent() {
        Event event = new Event();
        controller.addEvent(event);
        verify(mockRepo).addEvent(event);
    }

    @Test
    public void testControllerRepositoryIntegration_Search() {
        Query mockQuery = mock(Query.class);
        when(mockRepo.getAllEventsForAdminQuery()).thenReturn(mockQuery);
        // EventController.searchEventsByTitle calls orderByChild, startAt, endAt
        when(mockQuery.orderByChild(any())).thenReturn(mockQuery);
        when(mockQuery.startAt(any(String.class))).thenReturn(mockQuery);
        when(mockQuery.endAt(any(String.class))).thenReturn(mockQuery);

        assertNotNull(controller.searchEventsByTitle("Jazz"));
        verify(mockRepo).getAllEventsForAdminQuery();
    }
}
