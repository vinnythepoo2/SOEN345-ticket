package com.example.soen345_ticket.repositories;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.soen345_ticket.models.Reservation;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

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
    
    // Note: createReservation and cancelReservation involve complex Firebase Transactions 
    // which are typically tested via Integration Tests or complex ArgumentCaptors.
    // Basic logic for database pathing is verified here.
}
