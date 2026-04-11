package com.example.soen345_ticket.activities;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Intent;
import android.os.Looper;
import android.widget.EditText;
import com.example.soen345_ticket.R;
import com.example.soen345_ticket.models.Event;
import com.example.soen345_ticket.repositories.EventRepository;
import com.google.android.gms.tasks.Tasks;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowToast;

@RunWith(RobolectricTestRunner.class)
public class AddEditEventActivityUnitTest {

    public static class TestAddEditEventActivity extends AddEditEventActivity {
        static EventRepository eventRepository;

        @Override
        protected EventRepository createEventRepository() {
            return eventRepository != null ? eventRepository : super.createEventRepository();
        }

        static void reset() {
            eventRepository = null;
        }
    }

    private EventRepository mockRepo;

    @Before
    public void setUp() {
        TestAddEditEventActivity.reset();
        mockRepo = mock(EventRepository.class);
        TestAddEditEventActivity.eventRepository = mockRepo;
    }

    private void fillForm(TestAddEditEventActivity activity, String title, String seats, String price) {
        ((EditText) activity.findViewById(R.id.etTitle)).setText(title);
        ((EditText) activity.findViewById(R.id.etTotalSeats)).setText(seats);
        ((EditText) activity.findViewById(R.id.etPrice)).setText(price);
    }

    @Test
    public void saveEvent_emptyFields_showsToast() {
        try (ActivityController<TestAddEditEventActivity> controller = Robolectric.buildActivity(TestAddEditEventActivity.class)) {
            TestAddEditEventActivity activity = controller.setup().get();
            fillForm(activity, "", "", "");
            activity.findViewById(R.id.btnSave).performClick();

            assertEquals("Please fill in all required fields", ShadowToast.getTextOfLatestToast());
            verify(mockRepo, never()).addEvent(any());
        }
    }

    @Test
    public void saveEvent_invalidNumber_showsToast() {
        try (ActivityController<TestAddEditEventActivity> controller = Robolectric.buildActivity(TestAddEditEventActivity.class)) {
            TestAddEditEventActivity activity = controller.setup().get();
            fillForm(activity, "Title", "abc", "10.0");
            activity.findViewById(R.id.btnSave).performClick();

            assertEquals("Invalid number format for seats or price", ShadowToast.getTextOfLatestToast());
        }
    }

    @Test
    public void saveEvent_negativeSeats_showsToast() {
        try (ActivityController<TestAddEditEventActivity> controller = Robolectric.buildActivity(TestAddEditEventActivity.class)) {
            TestAddEditEventActivity activity = controller.setup().get();
            fillForm(activity, "Title", "-5", "10.0");
            activity.findViewById(R.id.btnSave).performClick();

            assertEquals("Seats cannot be negative", ShadowToast.getTextOfLatestToast());
        }
    }

    @Test
    public void saveEvent_negativePrice_showsToast() {
        try (ActivityController<TestAddEditEventActivity> controller = Robolectric.buildActivity(TestAddEditEventActivity.class)) {
            TestAddEditEventActivity activity = controller.setup().get();
            fillForm(activity, "Title", "10", "-1.0");
            activity.findViewById(R.id.btnSave).performClick();

            assertEquals("Price cannot be negative", ShadowToast.getTextOfLatestToast());
        }
    }

    @Test
    public void saveEvent_addSuccess_showsToast() {
        when(mockRepo.addEvent(any())).thenReturn(Tasks.forResult(null));

        try (ActivityController<TestAddEditEventActivity> controller = Robolectric.buildActivity(TestAddEditEventActivity.class)) {
            TestAddEditEventActivity activity = controller.setup().get();
            fillForm(activity, "New Event", "100", "50.0");
            activity.findViewById(R.id.btnSave).performClick();
            Shadows.shadowOf(Looper.getMainLooper()).idle();

            assertEquals("Event added", ShadowToast.getTextOfLatestToast());
            verify(mockRepo).addEvent(any());
        }
    }

    @Test
    public void saveEvent_updateSuccess_updatesAvailableSeats() {
        Event existingEvent = new Event("id1", "Old", "Desc", "Cat", "Loc", "Date", 10, 5, 20.0, false);
        Intent intent = new Intent();
        intent.putExtra("event", existingEvent);

        when(mockRepo.updateEvent(any())).thenReturn(Tasks.forResult(null));

        try (ActivityController<TestAddEditEventActivity> controller = Robolectric.buildActivity(TestAddEditEventActivity.class, intent)) {
            TestAddEditEventActivity activity = controller.setup().get();
            
            // Increase total seats from 10 to 15 (diff +5)
            // Available seats should go from 5 to 10
            fillForm(activity, "Old", "15", "20.0");
            activity.findViewById(R.id.btnSave).performClick();
            Shadows.shadowOf(Looper.getMainLooper()).idle();

            assertEquals("Event updated", ShadowToast.getTextOfLatestToast());
            assertEquals(10, existingEvent.getAvailableSeats());
            verify(mockRepo).updateEvent(existingEvent);
        }
    }

    @Test
    public void deleteEvent_success_showsToast() {
        Event existingEvent = new Event("id1", "Old", "Desc", "Cat", "Loc", "Date", 10, 5, 20.0, false);
        Intent intent = new Intent();
        intent.putExtra("event", existingEvent);

        when(mockRepo.deleteEvent("id1")).thenReturn(Tasks.forResult(null));

        try (ActivityController<TestAddEditEventActivity> controller = Robolectric.buildActivity(TestAddEditEventActivity.class, intent)) {
            TestAddEditEventActivity activity = controller.setup().get();
            activity.findViewById(R.id.btnDelete).performClick();
            Shadows.shadowOf(Looper.getMainLooper()).idle();

            assertEquals("Event deleted", ShadowToast.getTextOfLatestToast());
            verify(mockRepo).deleteEvent("id1");
        }
    }

    @Test
    public void saveEvent_addFailure_showsToast() {
        when(mockRepo.addEvent(any())).thenReturn(Tasks.forException(new Exception("Fail")));

        try (ActivityController<TestAddEditEventActivity> controller = Robolectric.buildActivity(TestAddEditEventActivity.class)) {
            TestAddEditEventActivity activity = controller.setup().get();
            fillForm(activity, "New Event", "100", "50.0");
            activity.findViewById(R.id.btnSave).performClick();
            Shadows.shadowOf(Looper.getMainLooper()).idle();

            assertEquals("Failed to add event", ShadowToast.getTextOfLatestToast());
        }
    }
}
