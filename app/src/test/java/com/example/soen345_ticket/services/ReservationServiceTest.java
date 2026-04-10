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
public class ReservationServiceTest {

    private ReservationRepository repository;
    private NotificationService notificationService;
    private NetworkChecker networkChecker;
    private ReservationService service;

    @Before
    public void setUp() {
        repository = mock(ReservationRepository.class);
        notificationService = mock(NotificationService.class);
        networkChecker = mock(NetworkChecker.class);
        service = new ReservationService(repository, notificationService, networkChecker);
    }

    @Test
    public void processReservation_fails_whenNetworkUnavailable() {
        when(networkChecker.isAvailable()).thenReturn(false);
        Reservation res = new Reservation();
        User user = new User();

        Task<Void> task = service.processReservation(res, 1, user);

        assertTrue(task.isComplete());
        assertFalse(task.isSuccessful());
        assertTrue(task.getException().getMessage().contains("Network unavailable"));
        verify(repository, never()).createReservation(any(), anyInt());
    }

    @Test
    public void processReservation_succeeds_whenRepositorySucceeds() {
        when(networkChecker.isAvailable()).thenReturn(true);
        Reservation res = new Reservation();
        res.setEventTitle("Rock Concert");
        User user = new User();
        user.setEmail("test@test.com");

        when(repository.createReservation(any(), eq(1))).thenReturn(Tasks.forResult(null));

        Task<Void> task = service.processReservation(res, 1, user);
        
        // Essential: force task listeners to execute
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        assertTrue(task.isSuccessful());
        verify(notificationService).sendReservationConfirmation(eq("test@test.com"), eq(res));
    }

    @Test
    public void processReservation_fails_whenRepositoryFails() {
        when(networkChecker.isAvailable()).thenReturn(true);
        Reservation res = new Reservation();
        User user = new User();

        Exception error = new RuntimeException("Database error");
        when(repository.createReservation(any(), anyInt())).thenReturn(Tasks.forException(error));

        Task<Void> task = service.processReservation(res, 1, user);
        
        // force task listeners to execute
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        assertFalse(task.isSuccessful());
        assertEquals(error, task.getException());
        verify(notificationService, never()).sendReservationConfirmation(any(), any());
    }

    @Test
    public void processReservation_proceeds_whenNetworkCheckerIsNull() {
        service = new ReservationService(repository, notificationService, null);
        Reservation res = new Reservation();
        User user = new User();
        user.setEmail("test@test.com");

        when(repository.createReservation(any(), anyInt())).thenReturn(Tasks.forResult(null));

        Task<Void> task = service.processReservation(res, 1, user);
        
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        assertTrue(task.isSuccessful());
        verify(repository).createReservation(any(), anyInt());
    }
}
