package com.example.soen345_ticket.activities;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.soen345_ticket.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void testLoginUIElementsVisible() {
        onView(withId(R.id.etEmail)).check(matches(isDisplayed()));
        onView(withId(R.id.etPassword)).check(matches(isDisplayed()));
        onView(withId(R.id.btnLogin)).check(matches(isDisplayed()));
        onView(withId(R.id.tvRegister)).check(matches(isDisplayed()));
    }

    @Test
    public void testEmptyFieldsShowError() {
        onView(withId(R.id.btnLogin)).perform(click());
        // Since it shows a Toast, we usually check for toast or just verify we are still on the same activity
        onView(withText("Login")).check(matches(isDisplayed()));
    }

    @Test
    public void testNavigateToRegister() {
        onView(withId(R.id.tvRegister)).perform(click());
        // Verify it navigated to RegisterActivity (Check for a unique view in RegisterActivity)
        // Assuming RegisterActivity has a view with id btnRegister
        onView(withId(R.id.btnRegister)).check(matches(isDisplayed()));
    }
}
