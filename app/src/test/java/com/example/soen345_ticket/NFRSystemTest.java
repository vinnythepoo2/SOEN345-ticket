package com.example.soen345_ticket;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.os.Looper;

import com.example.soen345_ticket.models.Reservation;
import com.example.soen345_ticket.models.User;
import com.example.soen345_ticket.repositories.ReservationRepository;
import com.example.soen345_ticket.services.NetworkChecker;
import com.example.soen345_ticket.services.NotificationService;
import com.example.soen345_ticket.services.ReservationService;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;

@RunWith(RobolectricTestRunner.class)
public class NFRSystemTest {

    private ReservationRepository reservationRepository;
    private NotificationService notificationService;
    private NetworkChecker networkChecker;
    private ReservationService reservationService;

    @Before
    public void setUp() {
        reservationRepository = mock(ReservationRepository.class);
        notificationService = mock(NotificationService.class);
        networkChecker = mock(NetworkChecker.class);
        reservationService = new ReservationService(reservationRepository, notificationService, networkChecker);
    }

    @Test
    public void testPerformance_ReservationExecutionTime() {
        // Goal: Ensure the logic layer processes the request in under 100ms (excluding network latency)
        when(networkChecker.isAvailable()).thenReturn(true);
        when(reservationRepository.createReservation(any(), anyInt())).thenReturn(Tasks.forResult(null));
        
        User user = new User("u1", "Name", "e@e.com", "1", "customer");
        Reservation res = new Reservation(null, "u1", "e1", "Title", "now", 1, "active");

        long startTime = System.currentTimeMillis();
        reservationService.processReservation(res, 1, user);
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;
        assertTrue("Performance failure: Reservation logic took too long: " + duration + "ms", duration < 100);
    }

    @Test
    public void testAvailability_OfflineHandling() {
        // Goal: Verify system remains responsive and handles offline state gracefully
        when(networkChecker.isAvailable()).thenReturn(false); // Simulate Airplane Mode

        User user = new User("u1", "Name", "e@e.com", "1", "customer");
        Reservation res = new Reservation(null, "u1", "e1", "Title", "now", 1, "active");

        Task<Void> task = reservationService.processReservation(res, 1, user);
        
        if (task.isSuccessful()) {
            fail("Availability failure: Reservation should not succeed when offline");
        } else {
            // Task should immediately contain the network error without timing out
            assertTrue(task.getException().getMessage().contains("Network unavailable"));
        }
    }
}
