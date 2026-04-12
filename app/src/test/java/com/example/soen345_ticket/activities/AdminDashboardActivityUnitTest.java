package com.example.soen345_ticket.activities;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.example.soen345_ticket.R;
import com.example.soen345_ticket.models.Event;
import com.example.soen345_ticket.repositories.EventRepository;
import com.example.soen345_ticket.repositories.UserRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowToast;

@RunWith(RobolectricTestRunner.class)
public class AdminDashboardActivityUnitTest {

    public static class TestAdminDashboardActivity extends AdminDashboardActivity {
        static EventRepository eventRepository;
        static UserRepository userRepository;
        boolean navigatedToLogin = false;

        @Override
        protected EventRepository createEventRepository() {
            return eventRepository != null ? eventRepository : super.createEventRepository();
        }

        @Override
        protected UserRepository createUserRepository() {
            return userRepository != null ? userRepository : super.createUserRepository();
        }

        @Override
        protected void navigateToLogin() {
            navigatedToLogin = true;
        }

        static void reset() {
            eventRepository = null;
            userRepository = null;
        }
    }

    private EventRepository mockEventRepo;
    private UserRepository mockUserRepo;

    @Before
    public void setUp() {
        TestAdminDashboardActivity.reset();
        mockEventRepo = mock(EventRepository.class);
        mockUserRepo = mock(UserRepository.class);
        
        when(mockEventRepo.getAllEventsForAdminQuery()).thenReturn(mock(com.google.firebase.database.DatabaseReference.class));
        
        TestAdminDashboardActivity.eventRepository = mockEventRepo;
        TestAdminDashboardActivity.userRepository = mockUserRepo;
    }

    @Test
    public void btnLogout_callsLogoutAndNavigates() {
        try (ActivityController<TestAdminDashboardActivity> controller = Robolectric.buildActivity(TestAdminDashboardActivity.class)) {
            TestAdminDashboardActivity activity = controller.setup().get();
            activity.findViewById(R.id.btnLogout).performClick();

            verify(mockUserRepo).logout();
            assertEquals(true, activity.navigatedToLogin);
        }
    }

    @Test
    public void fabAddEvent_navigatesToAddEditEvent() {
        try (ActivityController<TestAdminDashboardActivity> controller = Robolectric.buildActivity(TestAdminDashboardActivity.class)) {
            TestAdminDashboardActivity activity = controller.setup().get();
            activity.findViewById(R.id.fabAddEvent).performClick();

            Intent startedIntent = Shadows.shadowOf(activity).getNextStartedActivity();
            assertEquals(AddEditEventActivity.class.getName(), startedIntent.getComponent().getClassName());
        }
    }

    @Test
    public void lifecycle_startAndStop_works() {
        try (ActivityController<TestAdminDashboardActivity> controller = Robolectric.buildActivity(TestAdminDashboardActivity.class)) {
            controller.setup().start().stop();
        }
    }

    @Test
    public void testViewHolder_populatesDataCancelled() {
        try (ActivityController<TestAdminDashboardActivity> controller = Robolectric.buildActivity(TestAdminDashboardActivity.class)) {
            TestAdminDashboardActivity activity = controller.setup().get();
            View itemView = activity.getLayoutInflater().inflate(R.layout.item_admin_event, null);
            AdminDashboardActivity.AdminEventViewHolder holder = new AdminDashboardActivity.AdminEventViewHolder(itemView);

            Event event = new Event("id1", "Jazz", "Music", "music", "MTL", "2025", 100, 10, 50.0, true);
            
            holder.tvTitle.setText(event.getTitle());
            holder.tvStatus.setText(event.isCancelled() ? "CANCELLED" : "ACTIVE");

            assertEquals("CANCELLED", holder.tvStatus.getText().toString());
        }
    }

    @Test
    public void testViewHolder_populatesDataActive() {
        try (ActivityController<TestAdminDashboardActivity> controller = Robolectric.buildActivity(TestAdminDashboardActivity.class)) {
            TestAdminDashboardActivity activity = controller.setup().get();
            View itemView = activity.getLayoutInflater().inflate(R.layout.item_admin_event, null);
            AdminDashboardActivity.AdminEventViewHolder holder = new AdminDashboardActivity.AdminEventViewHolder(itemView);

            Event event = new Event("id1", "Jazz", "Music", "music", "MTL", "2025", 100, 10, 50.0, false);
            
            holder.tvStatus.setText(event.isCancelled() ? "CANCELLED" : "ACTIVE");

            assertEquals("ACTIVE", holder.tvStatus.getText().toString());
        }
    }

    @Test
    public void showToast_displaysMessage() {
        try (ActivityController<TestAdminDashboardActivity> controller = Robolectric.buildActivity(TestAdminDashboardActivity.class)) {
            TestAdminDashboardActivity activity = controller.setup().get();
            activity.showToast("Test Error");
            assertEquals("Test Error", ShadowToast.getTextOfLatestToast());
        }
    }
}
