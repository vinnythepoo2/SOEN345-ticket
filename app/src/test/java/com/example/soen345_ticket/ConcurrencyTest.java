package com.example.soen345_ticket;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.os.Looper;

import com.example.soen345_ticket.models.Reservation;
import com.example.soen345_ticket.models.User;
import com.example.soen345_ticket.repositories.ReservationRepository;
import com.example.soen345_ticket.services.NetworkChecker;
import com.example.soen345_ticket.services.NotificationService;
import com.example.soen345_ticket.services.ReservationService;
import com.google.android.gms.tasks.Tasks;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(RobolectricTestRunner.class)
public class ConcurrencyTest {

    private ReservationRepository reservationRepository;
    private NotificationService notificationService;
    private NetworkChecker networkChecker;
    private ReservationService reservationService;

    @Before
    public void setUp() {
        reservationRepository = mock(ReservationRepository.class);
        notificationService = mock(NotificationService.class);
        networkChecker = mock(NetworkChecker.class);
        
        when(networkChecker.isAvailable()).thenReturn(true);
        
        reservationService = new ReservationService(reservationRepository, notificationService, networkChecker);
    }

    @Test
    public void testSimultaneousReservations() throws InterruptedException {
        int numberOfThreads = 10;
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);

        when(reservationRepository.createReservation(any(), anyInt()))
                .thenReturn(Tasks.forResult(null));

        User user = new User("user-1", "Test User", "test@example.com", "123", "customer");
        Reservation res = new Reservation(null, "user-1", "evt-1", "Concert", "today", 1, "active");

        for (int i = 0; i < numberOfThreads; i++) {
            service.submit(() -> {
                try {
                    reservationService.processReservation(res, 1, user);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        boolean completed = latch.await(5, TimeUnit.SECONDS);
        
        // Idle looper to process the task completions
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        
        assertEquals("Threads did not complete in time", true, completed);
        assertEquals(numberOfThreads, successCount.get());
        verify(notificationService, atLeastOnce()).sendReservationConfirmation(any(), any());
    }
}
