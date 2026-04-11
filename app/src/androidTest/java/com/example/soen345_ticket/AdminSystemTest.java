package com.example.soen345_ticket;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import android.view.View;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.soen345_ticket.activities.SplashActivity;
import com.google.firebase.auth.FirebaseAuth;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AdminSystemTest {

    @Rule
    public ActivityScenarioRule<SplashActivity> activityRule = 
            new ActivityScenarioRule<>(SplashActivity.class);

    @Before
    public void setUp() {
        FirebaseAuth.getInstance().signOut();
    }

    @Test
    public void testAdminFullJourney() {
        // 1. Login as Admin
        waitUntilVisible(withId(R.id.etEmail), 15000);
        onView(withId(R.id.etEmail)).perform(replaceText("admin1@example.com"), closeSoftKeyboard());
        onView(withId(R.id.etPassword)).perform(replaceText("123456"), closeSoftKeyboard());
        onView(withId(R.id.btnLogin)).perform(click());

        // 2. Navigate to Add Event
        waitUntilVisible(withId(R.id.fabAddEvent), 15000);
        onView(withId(R.id.fabAddEvent)).perform(click());

        // 3. Add Event
        waitUntilVisible(withId(R.id.etTitle), 5000);
        String eventTitle = "Admin System Test " + System.currentTimeMillis();
        onView(withId(R.id.etTitle)).perform(typeText(eventTitle), closeSoftKeyboard());
        onView(withId(R.id.etTotalSeats)).perform(typeText("50"), closeSoftKeyboard());
        onView(withId(R.id.etPrice)).perform(typeText("100"), closeSoftKeyboard());
        onView(withId(R.id.btnSave)).perform(click());

        // 4. Edit Event - Scroll to and click the item
        waitUntilVisible(withId(R.id.rvEvents), 15000);
        
        // Wait a bit for Firebase to sync the new item
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

        onView(withId(R.id.rvEvents))
                .perform(RecyclerViewActions.scrollTo(hasDescendant(withText(eventTitle))))
                .perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(eventTitle)), click()));
        
        // 5. Modification
        waitUntilVisible(withId(R.id.etPrice), 10000);
        onView(withId(R.id.etPrice)).perform(replaceText("120"), closeSoftKeyboard());
        onView(withId(R.id.btnSave)).perform(click());

        // 6. Cancel Event
        waitUntilVisible(withId(R.id.rvEvents), 15000);
        onView(withId(R.id.rvEvents))
                .perform(RecyclerViewActions.scrollTo(hasDescendant(withText(eventTitle))))
                .perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(eventTitle)), click()));

        waitUntilVisible(withId(R.id.cbIsCancelled), 10000);
        onView(withId(R.id.cbIsCancelled)).perform(click());
        onView(withId(R.id.btnSave)).perform(click());

        // Verification - back on Dashboard
        waitUntilVisible(withId(R.id.fabAddEvent), 15000);
        onView(withId(R.id.fabAddEvent)).check(matches(isDisplayed()));
    }

    private void waitUntilVisible(Matcher<View> matcher, long timeoutMs) {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            try {
                onView(matcher).check(matches(isDisplayed()));
                return;
            } catch (NoMatchingViewException | AssertionError e) {
                try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            }
        }
        onView(matcher).check(matches(isDisplayed()));
    }
}
