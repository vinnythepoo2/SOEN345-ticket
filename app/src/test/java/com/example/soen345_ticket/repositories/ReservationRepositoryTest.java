package com.example.soen345_ticket.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.soen345_ticket.models.Reservation;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

public class ReservationRepositoryTest {

    @Mock
    private FirebaseDatabase mockDatabase;
    @Mock
    private DatabaseReference mockRootRef;
    @Mock
    private DatabaseReference mockReservationsRef;
    @Mock
    private DatabaseReference mockEventsRef;
    @Mock
    private DatabaseReference mockSingleEventRef;
    @Mock
    private DatabaseReference mockPushRef;

    private MockedStatic<FirebaseDatabase> mockedDbStatic;
    private ReservationRepository reservationRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockedDbStatic = mockStatic(FirebaseDatabase.class);
        when(FirebaseDatabase.getInstance()).thenReturn(mockDatabase);
        when(mockDatabase.getReference()).thenReturn(mockRootRef);

        reservationRepository = new ReservationRepository();
    }

    @After
    public void tearDown() {
        mockedDbStatic.close();
    }

    @Test
    public void testGetReservationsQueryByUser() {
        when(mockRootRef.child("reservations")).thenReturn(mockReservationsRef);
        Query mockQuery = mock(Query.class);
        when(mockReservationsRef.orderByChild("userId")).thenReturn(mockQuery);
        when(mockQuery.equalTo("user123")).thenReturn(mockQuery);

        Query result = reservationRepository.getReservationsQueryByUser("user123");

        verify(mockRootRef).child("reservations");
        verify(mockReservationsRef).orderByChild("userId");
        verify(mockQuery).equalTo("user123");
        assertEquals(mockQuery, result);
    }

    @Test
    public void testCreateReservation_Pathing() {
        // Setup paths
        when(mockRootRef.child("events")).thenReturn(mockEventsRef);
        when(mockEventsRef.child("evt-1")).thenReturn(mockSingleEventRef);
        when(mockRootRef.child("reservations")).thenReturn(mockReservationsRef);
        when(mockReservationsRef.push()).thenReturn(mockPushRef);
        when(mockPushRef.getKey()).thenReturn("res-new");

        Reservation res = new Reservation();
        res.setEventId("evt-1");

        // Act
        reservationRepository.createReservation(res, 2);

        // Verify that it reached the correct database nodes
        verify(mockRootRef).child("events");
        verify(mockEventsRef).child("evt-1");
        verify(mockRootRef).child("reservations");
        verify(mockReservationsRef).push();
        verify(mockSingleEventRef).runTransaction(any(Transaction.Handler.class));
    }

    @Test
    public void testCancelReservation_Pathing() {
        // Setup paths
        when(mockRootRef.child("events")).thenReturn(mockEventsRef);
        when(mockEventsRef.child("evt-1")).thenReturn(mockSingleEventRef);
        when(mockRootRef.child("reservations")).thenReturn(mockReservationsRef);
        when(mockReservationsRef.child("res-1")).thenReturn(mockPushRef);

        Reservation res = new Reservation();
        res.setEventId("evt-1");
        res.setReservationId("res-1");

        // Act
        reservationRepository.cancelReservation(res);

        // Verify that it reached the correct database nodes
        verify(mockRootRef).child("events");
        verify(mockEventsRef).child("evt-1");
        verify(mockRootRef).child("reservations");
        verify(mockReservationsRef).child("res-1");
        verify(mockSingleEventRef).runTransaction(any(Transaction.Handler.class));
    }
}
