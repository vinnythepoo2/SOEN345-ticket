package com.example.soen345_ticket.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.os.Looper;
import com.example.soen345_ticket.models.Reservation;
import com.example.soen345_ticket.models.User;
import com.example.soen345_ticket.repositories.ReservationRepository;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;

@RunWith(RobolectricTestRunner.class)
public class ReservationServiceEdgeCaseTest {

    private ReservationRepository mockRepo;
    private NotificationService mockNotification;
    private NetworkChecker mockNetwork;
    private ReservationService service;
    private Reservation testReservation;
    private User testUser;

    @Before
    public void setUp() {
        mockRepo = mock(ReservationRepository.class);
        mockNotification = mock(NotificationService.class);
        mockNetwork = mock(NetworkChecker.class);
        service = new ReservationService(mockRepo, mockNotification, mockNetwork);

        testUser = new User("uid-1", "John", "john@example.com", "123", "customer");
        testReservation = new Reservation("res-1", "uid-1", "evt-1", "Concert", "2024-12-12", 2, "active");
    }

    @Test
    public void processReservation_networkUnavailable_failsImmediately() {
        when(mockNetwork.isAvailable()).thenReturn(false);

        Task<Void> task = service.processReservation(testReservation, 2, testUser);

        assertTrue(task.isComplete());
        assertFalse(task.isSuccessful());
        assertEquals("Network unavailable. Please check your connection.", task.getException().getMessage());
        verify(mockRepo, never()).createReservation(any(), anyInt());
    }

    @Test
    public void processReservation_databaseError_returnsException() {
        when(mockNetwork.isAvailable()).thenReturn(true);
        when(mockRepo.createReservation(any(), anyInt()))
                .thenReturn(Tasks.forException(new Exception("DB Error")));

        Task<Void> task = service.processReservation(testReservation, 2, testUser);
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        assertTrue(task.isComplete());
        assertFalse(task.isSuccessful());
        assertEquals("DB Error", task.getException().getMessage());
        verify(mockNotification, never()).sendReservationConfirmation(any(), any());
    }

    @Test
    public void processReservation_notificationTriggeredOnSuccess() {
        when(mockNetwork.isAvailable()).thenReturn(true);
        when(mockRepo.createReservation(any(), anyInt())).thenReturn(Tasks.forResult(null));

        Task<Void> task = service.processReservation(testReservation, 2, testUser);
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        assertTrue(task.isSuccessful());
        verify(mockNotification).sendReservationConfirmation(eq("john@example.com"), eq(testReservation));
    }

    @Test
    public void processReservation_nullUserEmail_stillCallsRepo() {
        // Technically User might have null email if not loaded correctly
        User userWithNoEmail = new User("uid-1", "John", null, "123", "customer");
        when(mockNetwork.isAvailable()).thenReturn(true);
        when(mockRepo.createReservation(any(), anyInt())).thenReturn(Tasks.forResult(null));

        Task<Void> task = service.processReservation(testReservation, 2, userWithNoEmail);
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        assertTrue(task.isSuccessful());
        verify(mockNotification).sendReservationConfirmation(eq(null), eq(testReservation));
    }
}
