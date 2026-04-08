package com.example.soen345_ticket.repositories;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.soen345_ticket.models.Event;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

public class EventRepositoryTest {

    @Mock
    private FirebaseDatabase mockDatabase;
    @Mock
    private DatabaseReference mockEventsRef;

    private MockedStatic<FirebaseDatabase> mockedDbStatic;
    private EventRepository eventRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockedDbStatic = mockStatic(FirebaseDatabase.class);
        when(FirebaseDatabase.getInstance()).thenReturn(mockDatabase);
        when(mockDatabase.getReference("events")).thenReturn(mockEventsRef);

        eventRepository = new EventRepository();
    }

    @After
    public void tearDown() {
        mockedDbStatic.close();
    }

    @Test
    public void testAddEvent() {
        Event event = new Event();
        DatabaseReference mockPushRef = mock(DatabaseReference.class);
        when(mockEventsRef.push()).thenReturn(mockPushRef);
        when(mockPushRef.getKey()).thenReturn("new-event-id");
        Task<Void> mockTask = mock(Task.class);
        when(mockPushRef.setValue(event)).thenReturn(mockTask);

        Task<Void> result = eventRepository.addEvent(event);

        verify(mockEventsRef).push();
        verify(mockPushRef).setValue(event);
        assertEquals("new-event-id", event.getEventId());
        assertEquals(mockTask, result);
    }

    @Test
    public void testUpdateEvent() {
        Event event = new Event();
        event.setEventId("e1");
        DatabaseReference mockChildRef = mock(DatabaseReference.class);
        when(mockEventsRef.child("e1")).thenReturn(mockChildRef);
        Task<Void> mockTask = mock(Task.class);
        when(mockChildRef.setValue(event)).thenReturn(mockTask);

        Task<Void> result = eventRepository.updateEvent(event);

        verify(mockEventsRef).child("e1");
        verify(mockChildRef).setValue(event);
        assertEquals(mockTask, result);
    }

    @Test
    public void testDeleteEvent() {
        DatabaseReference mockChildRef = mock(DatabaseReference.class);
        when(mockEventsRef.child("e1")).thenReturn(mockChildRef);
        Task<Void> mockTask = mock(Task.class);
        when(mockChildRef.removeValue()).thenReturn(mockTask);

        Task<Void> result = eventRepository.deleteEvent("e1");

        verify(mockEventsRef).child("e1");
        verify(mockChildRef).removeValue();
        assertEquals(mockTask, result);
    }

    @Test
    public void testGetAllEventsForAdminQuery() {
        Query result = eventRepository.getAllEventsForAdminQuery();
        assertEquals(mockEventsRef, result);
    }

    @Test
    public void testGetEventsQuery() {
        Query mockQuery = mock(Query.class);
        when(mockEventsRef.orderByChild("cancelled")).thenReturn(mockQuery);
        when(mockQuery.equalTo(false)).thenReturn(mockQuery);

        Query result = eventRepository.getEventsQuery();

        verify(mockEventsRef).orderByChild("cancelled");
        verify(mockQuery).equalTo(false);
        assertEquals(mockQuery, result);
    }
}
