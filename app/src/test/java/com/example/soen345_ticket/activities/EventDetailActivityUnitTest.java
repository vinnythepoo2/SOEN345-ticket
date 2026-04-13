package com.example.soen345_ticket.activities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.soen345_ticket.R;
import com.example.soen345_ticket.models.Event;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowToast;

@RunWith(RobolectricTestRunner.class)
public class EventDetailActivityUnitTest {

    @Test
    public void onCreate_withEvent_displaysDetails() {
        Event event = new Event("id1", "Title", "Desc", "Cat", "Loc", "Date", 100, 10, 50.0, false);
        Intent intent = new Intent();
        intent.putExtra("event", event);

        try (ActivityController<EventDetailActivity> controller = Robolectric.buildActivity(EventDetailActivity.class, intent)) {
            EventDetailActivity activity = controller.setup().get();

            assertEquals("Title", ((TextView) activity.findViewById(R.id.tvTitle)).getText().toString());
            assertEquals("Loc", ((TextView) activity.findViewById(R.id.tvLocation)).getText().toString());
            assertTrue(((Button) activity.findViewById(R.id.btnBookNow)).isEnabled());
        }
    }

    @Test
    public void onCreate_withSoldOutEvent_disablesBooking() {
        Event event = new Event("id1", "Title", "Desc", "Cat", "Loc", "Date", 100, 0, 50.0, false);
        Intent intent = new Intent();
        intent.putExtra("event", event);

        try (ActivityController<EventDetailActivity> controller = Robolectric.buildActivity(EventDetailActivity.class, intent)) {
            EventDetailActivity activity = controller.setup().get();

            Button btnBook = activity.findViewById(R.id.btnBookNow);
            assertFalse(btnBook.isEnabled());
            assertEquals("Sold Out", btnBook.getText().toString());
        }
    }

    @Test
    public void onCreate_withoutEvent_finishesWithToast() {
        try (ActivityController<EventDetailActivity> controller = Robolectric.buildActivity(EventDetailActivity.class)) {
            EventDetailActivity activity = controller.setup().get();

            assertEquals("Event not found", ShadowToast.getTextOfLatestToast());
            assertTrue(activity.isFinishing());
        }
    }

    @Test
    public void btnBookNow_navigatesToReservation() {
        Event event = new Event("id1", "Title", "Desc", "Cat", "Loc", "Date", 100, 10, 50.0, false);
        Intent intent = new Intent();
        intent.putExtra("event", event);

        try (ActivityController<EventDetailActivity> controller = Robolectric.buildActivity(EventDetailActivity.class, intent)) {
            EventDetailActivity activity = controller.setup().get();
            activity.findViewById(R.id.btnBookNow).performClick();

            Intent startedIntent = Shadows.shadowOf(activity).getNextStartedActivity();
            assertNotNull(startedIntent);
            assertEquals(ReservationActivity.class.getName(), startedIntent.getComponent().getClassName());
        }
    }
}
