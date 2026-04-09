package com.example.soen345_ticket;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.os.Looper;

import com.example.soen345_ticket.models.Event;
import com.example.soen345_ticket.models.Reservation;
import com.example.soen345_ticket.models.User;
import com.example.soen345_ticket.repositories.EventRepository;
import com.example.soen345_ticket.repositories.ReservationRepository;
import com.example.soen345_ticket.services.EventController;
import com.example.soen345_ticket.services.NotificationService;
import com.example.soen345_ticket.services.ReservationService;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.Query;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;

@RunWith(RobolectricTestRunner.class)
public class EndToEndIntegrationTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private Query mockQuery;

    private EventController eventController;
    private ReservationService reservationService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        eventController = new EventController(eventRepository);
        reservationService = new ReservationService(reservationRepository, notificationService);
    }

    @Test
    public void testAdminToCustomerFullFlow() {
        // --- 1. Admin adds event ---
        Event newEvent = new Event("evt-123", "Tech Conference", "Latest in AI", "Technology", "Montreal", "2025-11-20", 200, 200, 75.0, false);
        when(eventRepository.addEvent(any(Event.class))).thenReturn(Tasks.forResult(null));
        
        eventController.addEvent(newEvent);
        verify(eventRepository).addEvent(newEvent);

        // --- 2. Customer searches for the event ---
        // Setup the repository to return a query object
        when(eventRepository.getAllEventsForAdminQuery()).thenReturn(mockQuery);
        when(mockQuery.orderByChild("title")).thenReturn(mockQuery);
        when(mockQuery.startAt("Tech")).thenReturn(mockQuery);
        when(mockQuery.endAt(any(String.class))).thenReturn(mockQuery);

        Query searchQuery = eventController.searchEventsByTitle("Tech");
        assertNotNull(searchQuery);

        // --- 3. Customer reserves a ticket ---
        User customer = new User("user-999", "John Doe", "john@example.com", "514-000-0000", "customer");
        Reservation reservation = new Reservation(null, customer.getUserId(), newEvent.getEventId(), newEvent.getTitle(), "2025-01-15", 3, "active");
        
        when(reservationRepository.createReservation(any(Reservation.class), anyInt()))
                .thenReturn(Tasks.forResult(null));

        // Act
        reservationService.processReservation(reservation, 3, customer);
        
        // Idle the main looper to ensure addOnCompleteListener is executed
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        // --- 4. Verify interaction and notification ---
        // Verify database was hit for reservation
        verify(reservationRepository).createReservation(reservation, 3);
        
        // Verify notification service was triggered (Integration point)
        verify(notificationService).sendReservationConfirmation(eq("john@example.com"), eq(reservation));
    }
}
