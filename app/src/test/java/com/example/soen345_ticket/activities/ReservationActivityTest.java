package com.example.soen345_ticket.activities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Intent;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.soen345_ticket.R;
import com.example.soen345_ticket.models.Event;
import com.example.soen345_ticket.models.Reservation;
import com.example.soen345_ticket.repositories.ReservationRepository;
import com.example.soen345_ticket.repositories.UserRepository;
import com.example.soen345_ticket.services.EmailService;
import com.example.soen345_ticket.services.ReservationEmailNotifier;
import com.example.soen345_ticket.services.ReservationService;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseUser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowToast;

@RunWith(RobolectricTestRunner.class)
public class ReservationActivityTest {

    public static class TestReservationActivity extends ReservationActivity {
        static ReservationRepository reservationRepository;
        static UserRepository userRepository;
        static EmailService emailService;
        static ReservationEmailNotifier reservationEmailNotifier;
        static ReservationService reservationService;
        static FirebaseUser currentFirebaseUser;
        boolean backPressed;

        @Override
        protected ReservationRepository createReservationRepository() {
            return reservationRepository != null ? reservationRepository : super.createReservationRepository();
        }

        @Override
        protected UserRepository createUserRepository() {
            return userRepository != null ? userRepository : super.createUserRepository();
        }

        @Override
        protected EmailService createEmailService() {
            return emailService != null ? emailService : super.createEmailService();
        }

        @Override
        protected ReservationEmailNotifier createReservationEmailNotifier() {
            return reservationEmailNotifier != null ? reservationEmailNotifier : super.createReservationEmailNotifier();
        }

        @Override
        protected ReservationService createReservationService() {
            return reservationService != null ? reservationService : super.createReservationService();
        }

        @Override
        protected FirebaseUser getCurrentFirebaseUser() {
            return currentFirebaseUser;
        }

        @Override
        public void onBackPressed() {
            backPressed = true;
            super.onBackPressed();
        }

        @Override
        public String getCurrentTimestamp() {
            return "2025-01-01 12:00";
        }

        static void reset() {
            reservationRepository = null;
            userRepository = null;
            emailService = null;
            reservationEmailNotifier = null;
            reservationService = null;
            currentFirebaseUser = null;
        }
    }

    private Event testEvent;

    @Before
    public void setUp() {
        TestReservationActivity.reset();
        testEvent = new Event(
                "evt-1",
                "Jazz Night",
                "Music event",
                "music",
                "Montreal",
                "2026-05-15",
                100,
                10,
                50.0,
                false
        );
        
        // Setup default mocks to avoid Firebase initialization errors
        TestReservationActivity.reservationRepository = mock(ReservationRepository.class);
        TestReservationActivity.userRepository = mock(UserRepository.class);
        TestReservationActivity.emailService = mock(EmailService.class);
        TestReservationActivity.reservationEmailNotifier = mock(ReservationEmailNotifier.class);
        TestReservationActivity.reservationService = mock(ReservationService.class);
    }

    @After
    public void tearDown() {
        TestReservationActivity.reset();
    }

    private TestReservationActivity buildActivity() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.putExtra("event", testEvent);
        ActivityController<TestReservationActivity> controller =
                Robolectric.buildActivity(TestReservationActivity.class, intent).setup();
        return controller.get();
    }

    @Test
    public void onCreate_populatesEventAndInitialTotal() {
        TestReservationActivity activity = buildActivity();

        TextView title = activity.findViewById(R.id.tvEventTitle);
        TextView price = activity.findViewById(R.id.tvEventPrice);
        TextView total = activity.findViewById(R.id.tvTotalPrice);

        assertEquals("Jazz Night", title.getText().toString());
        assertEquals("Price per ticket: $50.0", price.getText().toString());
        assertEquals("Total: $50.00", total.getText().toString());
    }

    @Test
    public void quantityChange_updatesTotal() {
        TestReservationActivity activity = buildActivity();
        EditText quantity = activity.findViewById(R.id.etQuantity);
        TextView total = activity.findViewById(R.id.tvTotalPrice);

        quantity.setText("3");
        assertEquals("Total: $150.00", total.getText().toString());

        quantity.setText("");
        assertEquals("Total: $0.00", total.getText().toString());
    }

    @Test
    public void confirmWithEmptyQuantity_showsToast() {
        TestReservationActivity activity = buildActivity();
        EditText quantity = activity.findViewById(R.id.etQuantity);
        Button confirm = activity.findViewById(R.id.btnConfirm);

        quantity.setText("");
        confirm.performClick();

        assertEquals("Enter quantity", ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void confirmWithInvalidQuantity_showsToast() {
        TestReservationActivity activity = buildActivity();
        EditText quantity = activity.findViewById(R.id.etQuantity);
        Button confirm = activity.findViewById(R.id.btnConfirm);

        quantity.setText("0");
        confirm.performClick();

        assertEquals("Quantity must be at least 1", ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void confirmWithTooManySeats_showsToast() {
        TestReservationActivity activity = buildActivity();
        EditText quantity = activity.findViewById(R.id.etQuantity);
        Button confirm = activity.findViewById(R.id.btnConfirm);

        quantity.setText("11");
        confirm.performClick();

        assertEquals("Not enough seats available", ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void confirmWithValidQuantity_createsReservation() {
        ReservationService reservationService = mock(ReservationService.class);
        FirebaseUser firebaseUser = mock(FirebaseUser.class);

        when(firebaseUser.getUid()).thenReturn("user-1");
        when(firebaseUser.getEmail()).thenReturn("user@example.com");
        when(reservationService.processReservation(any(), eq(2), any())).thenReturn(Tasks.forResult(null));

        TestReservationActivity.reservationService = reservationService;
        TestReservationActivity.currentFirebaseUser = firebaseUser;

        TestReservationActivity activity = buildActivity();
        EditText quantity = activity.findViewById(R.id.etQuantity);
        Button confirm = activity.findViewById(R.id.btnConfirm);

        quantity.setText("2");
        confirm.performClick();
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        assertEquals("Reservation confirmed!", ShadowToast.getTextOfLatestToast());
        verify(reservationService).processReservation(any(Reservation.class), eq(2), any());
        assertNotNull(Shadows.shadowOf(activity).getNextStartedActivity());
    }

    @Test
    public void confirmWithNullUser_doesNothing() {
        ReservationService reservationService = mock(ReservationService.class);
        TestReservationActivity.reservationService = reservationService;
        TestReservationActivity.currentFirebaseUser = null;

        TestReservationActivity activity = buildActivity();
        EditText quantity = activity.findViewById(R.id.etQuantity);
        Button confirm = activity.findViewById(R.id.btnConfirm);

        quantity.setText("2");
        confirm.performClick();

        verify(reservationService, org.mockito.Mockito.never()).processReservation(any(), anyInt(), any());
    }

    @Test
    public void confirmWithServiceFailure_showsFailureToast() {
        ReservationService reservationService = mock(ReservationService.class);
        FirebaseUser firebaseUser = mock(FirebaseUser.class);

        when(firebaseUser.getUid()).thenReturn("user-1");
        when(firebaseUser.getEmail()).thenReturn("user@example.com");
        when(reservationService.processReservation(any(), eq(2), any()))
                .thenReturn(Tasks.forException(new RuntimeException("boom")));

        TestReservationActivity.reservationService = reservationService;
        TestReservationActivity.currentFirebaseUser = firebaseUser;

        TestReservationActivity activity = buildActivity();
        EditText quantity = activity.findViewById(R.id.etQuantity);
        Button confirm = activity.findViewById(R.id.btnConfirm);

        quantity.setText("2");
        confirm.performClick();
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        assertEquals("Failed: boom", ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void backButton_triggersOnBackPressed() {
        TestReservationActivity activity = buildActivity();
        Button back = activity.findViewById(R.id.btnBack);

        back.performClick();

        assertTrue(activity.backPressed);
    }
}
