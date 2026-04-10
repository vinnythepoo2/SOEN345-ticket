package com.example.soen345_ticket;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.view.View;

import androidx.test.espresso.NoMatchingViewException;
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
public class SystemTest {

    @Rule
    public ActivityScenarioRule<SplashActivity> activityRule = 
            new ActivityScenarioRule<>(SplashActivity.class);

    @Before
    public void setUp() {
        // Ensure signed-out state
        FirebaseAuth.getInstance().signOut();
    }

    @Test
    public void testFullCustomerJourney() {
        // 1. Wait for Login and navigate to Register
        waitUntilVisible(withId(R.id.tvRegister), 10000);
        onView(withId(R.id.tvRegister)).perform(click());

        // 2. Registration
        waitUntilVisible(withId(R.id.btnRegister), 5000);
        String uniqueEmail = "system_test_" + System.currentTimeMillis() + "@example.com";
        onView(withId(R.id.etFullName)).perform(typeText("System Test User"), closeSoftKeyboard());
        onView(withId(R.id.etEmail)).perform(typeText(uniqueEmail), closeSoftKeyboard());
        onView(withId(R.id.etPhone)).perform(typeText("5140000000"), closeSoftKeyboard());
        onView(withId(R.id.etPassword)).perform(typeText("password123"), closeSoftKeyboard());
        onView(withId(R.id.btnRegister)).perform(click());

        // 3. Wait for Dashboard (transition after successful registration)
        waitUntilVisible(withId(R.id.etSearch), 15000); 
        
        // Search
        onView(withId(R.id.etSearch)).perform(click(), typeText("paddle"), closeSoftKeyboard());
        onView(withId(R.id.btnFilter)).perform(click());
        
        // 4. Reservation Flow
        waitUntilVisible(withText("paddle"), 5000);
        onView(withText("paddle")).perform(click());
        
        waitUntilVisible(withId(R.id.btnBookNow), 5000);
        onView(withId(R.id.btnBookNow)).perform(click());
        
        waitUntilVisible(withId(R.id.btnConfirm), 5000);
        onView(withId(R.id.etQuantity)).perform(replaceText("1"), closeSoftKeyboard());
        onView(withId(R.id.btnConfirm)).perform(click());

        // 5. Verification
        waitUntilVisible(withText("Events"), 10000);
        onView(withText("Events")).check(matches(isDisplayed()));
    }

    /**
     * Helper to wait for a view to be visible before interacting.
     * Essential for asynchronous Firebase operations.
     */
    private void waitUntilVisible(Matcher<View> matcher, long timeoutMs) {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            try {
                onView(matcher).check(matches(isDisplayed()));
                return; // View is visible
            } catch (NoMatchingViewException | AssertionError e) {
                try { Thread.sleep(500); } catch (InterruptedException ignored) {}
            }
        }
        // Final attempt, will throw exception if fails
        onView(matcher).check(matches(isDisplayed()));
    }
}
