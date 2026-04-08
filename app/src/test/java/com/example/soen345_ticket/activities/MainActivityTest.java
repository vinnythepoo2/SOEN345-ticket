package com.example.soen345_ticket.activities;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import android.content.Intent;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.soen345_ticket.R;
import com.example.soen345_ticket.repositories.EventRepository;
import com.example.soen345_ticket.repositories.UserRepository;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;

@RunWith(RobolectricTestRunner.class)
public class MainActivityTest {

    @SuppressWarnings("rawtypes")
    public static class TestMainActivity extends MainActivity {
        static EventRepository mockEventRepository;
        static UserRepository mockUserRepository;
        Query lastUpdatedQuery;
        boolean setupRecyclerViewCalled;

        @Override
        protected EventRepository createEventRepository() {
            return mockEventRepository;
        }

        @Override
        protected UserRepository createUserRepository() {
            return mockUserRepository;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void setupRecyclerView(Query query) {
            setupRecyclerViewCalled = true;
            adapter = mock(FirebaseRecyclerAdapter.class);
        }

        @Override
        protected void updateRecyclerView(Query query) {
            lastUpdatedQuery = query;
        }

        static void reset() {
            mockEventRepository = null;
            mockUserRepository = null;
        }
    }

    private EventRepository mockEventRepo;
    private UserRepository mockUserRepo;
    private Query mockQuery;

    @Before
    public void setUp() {
        mockEventRepo = mock(EventRepository.class);
        mockUserRepo = mock(UserRepository.class);
        mockQuery = mock(Query.class);

        when(mockEventRepo.getEventsQuery()).thenReturn(mockQuery);
        when(mockEventRepo.getAllEventsForAdminQuery()).thenReturn(mockQuery);
        when(mockQuery.orderByChild(anyString())).thenReturn(mockQuery);
        when(mockQuery.startAt(anyString())).thenReturn(mockQuery);
        when(mockQuery.endAt(anyString())).thenReturn(mockQuery);

        TestMainActivity.mockEventRepository = mockEventRepo;
        TestMainActivity.mockUserRepository = mockUserRepo;
    }

    @After
    public void tearDown() {
        TestMainActivity.reset();
    }

    private TestMainActivity buildActivity() {
        return Robolectric.buildActivity(TestMainActivity.class).setup().get();
    }

    // ── onCreate ──────────────────────────────────────────────────────────────

    @Test
    public void onCreate_setupRecyclerViewIsCalled() {
        TestMainActivity activity = buildActivity();
        assertTrue(activity.setupRecyclerViewCalled);
    }

    // ── setupTabToggle ───────────────────────────────────────────────────────

    @Test
    public void searchTab_click_showsSearchPanelAndHidesFilterPanel() {
        TestMainActivity activity = buildActivity();
        // Switch to filter first then back to search to exercise both branches
        activity.findViewById(R.id.btnTabFilter).performClick();
        activity.findViewById(R.id.btnTabSearch).performClick();

        assertEquals(View.VISIBLE, activity.findViewById(R.id.searchPanel).getVisibility());
        assertEquals(View.GONE, activity.findViewById(R.id.filterPanel).getVisibility());
    }

    @Test
    public void searchTab_click_resetsToAllEvents() {
        TestMainActivity activity = buildActivity();
        activity.lastUpdatedQuery = null;

        activity.findViewById(R.id.btnTabSearch).performClick();

        assertEquals(mockQuery, activity.lastUpdatedQuery);
    }

    @Test
    public void filterTab_click_showsFilterPanelAndHidesSearchPanel() {
        TestMainActivity activity = buildActivity();
        activity.findViewById(R.id.btnTabFilter).performClick();

        assertEquals(View.GONE, activity.findViewById(R.id.searchPanel).getVisibility());
        assertEquals(View.VISIBLE, activity.findViewById(R.id.filterPanel).getVisibility());
    }

    @Test
    public void filterTab_click_resetsToAllEvents() {
        TestMainActivity activity = buildActivity();
        activity.lastUpdatedQuery = null;

        activity.findViewById(R.id.btnTabFilter).performClick();

        assertEquals(mockQuery, activity.lastUpdatedQuery);
    }

    // ── setupFilterSpinner ───────────────────────────────────────────────────

    @Test
    public void spinner_selection_updatesHintToMatchPosition() {
        TestMainActivity activity = buildActivity();
        Spinner spinner = activity.findViewById(R.id.spinnerFilterType);
        EditText etFilterValue = activity.findViewById(R.id.etFilterValue);

        spinner.setSelection(1); // Category
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        assertEquals("e.g. Music, Sports, Tech", etFilterValue.getHint().toString());
    }

    @Test
    public void spinner_selection_clearsFilterInput() {
        TestMainActivity activity = buildActivity();
        EditText etFilterValue = activity.findViewById(R.id.etFilterValue);
        Spinner spinner = activity.findViewById(R.id.spinnerFilterType);

        etFilterValue.setText("old text");
        spinner.setSelection(2); // Location
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        assertEquals("", etFilterValue.getText().toString());
    }

    @Test
    public void spinner_locationPosition_showsLocationHint() {
        TestMainActivity activity = buildActivity();
        Spinner spinner = activity.findViewById(R.id.spinnerFilterType);
        EditText etFilterValue = activity.findViewById(R.id.etFilterValue);

        spinner.setSelection(2);
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        assertEquals("e.g. Montreal, Quebec", etFilterValue.getHint().toString());
    }

    // ── performTitleSearch ───────────────────────────────────────────────────

    @Test
    public void titleSearch_withValidInput_queriesAllEventsOrderedByTitle() {
        TestMainActivity activity = buildActivity();
        EditText etSearch = activity.findViewById(R.id.etSearch);

        etSearch.setText("Jazz");
        activity.findViewById(R.id.btnFilter).performClick();

        verify(mockEventRepo).getAllEventsForAdminQuery();
        verify(mockQuery).orderByChild("title");
        assertEquals(mockQuery, activity.lastUpdatedQuery);
    }

    @Test
    public void titleSearch_withEmptyInput_showsAllEvents() {
        TestMainActivity activity = buildActivity();
        EditText etSearch = activity.findViewById(R.id.etSearch);
        activity.lastUpdatedQuery = null;

        etSearch.setText("");
        activity.findViewById(R.id.btnFilter).performClick();

        verify(mockEventRepo, atLeastOnce()).getEventsQuery();
        assertEquals(mockQuery, activity.lastUpdatedQuery);
    }

    @Test
    public void titleSearch_withWhitespaceOnly_showsAllEvents() {
        TestMainActivity activity = buildActivity();
        EditText etSearch = activity.findViewById(R.id.etSearch);
        activity.lastUpdatedQuery = null;

        etSearch.setText("   ");
        activity.findViewById(R.id.btnFilter).performClick();

        verify(mockEventRepo, atLeastOnce()).getEventsQuery();
    }

    // ── performFieldFilter ───────────────────────────────────────────────────

    @Test
    public void fieldFilter_withValidInput_queriesAllEventsOrderedByField() {
        TestMainActivity activity = buildActivity();
        EditText etFilterValue = activity.findViewById(R.id.etFilterValue);

        etFilterValue.setText("Montreal");
        activity.findViewById(R.id.btnApplyFilter).performClick();

        verify(mockEventRepo).getAllEventsForAdminQuery();
        assertEquals(mockQuery, activity.lastUpdatedQuery);
    }

    @Test
    public void fieldFilter_withEmptyInput_showsAllEvents() {
        TestMainActivity activity = buildActivity();
        EditText etFilterValue = activity.findViewById(R.id.etFilterValue);
        activity.lastUpdatedQuery = null;

        etFilterValue.setText("");
        activity.findViewById(R.id.btnApplyFilter).performClick();

        verify(mockEventRepo, atLeastOnce()).getEventsQuery();
        assertEquals(mockQuery, activity.lastUpdatedQuery);
    }

    @Test
    public void fieldFilter_withWhitespaceOnly_showsAllEvents() {
        TestMainActivity activity = buildActivity();
        EditText etFilterValue = activity.findViewById(R.id.etFilterValue);
        activity.lastUpdatedQuery = null;

        etFilterValue.setText("   ");
        activity.findViewById(R.id.btnApplyFilter).performClick();

        verify(mockEventRepo, atLeastOnce()).getEventsQuery();
    }

    @Test
    public void fieldFilter_usesSpinnerSelectionAsFirebaseField() {
        TestMainActivity activity = buildActivity();
        Spinner spinner = activity.findViewById(R.id.spinnerFilterType);
        EditText etFilterValue = activity.findViewById(R.id.etFilterValue);

        spinner.setSelection(2); // Location
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        etFilterValue.setText("Montreal");
        activity.findViewById(R.id.btnApplyFilter).performClick();

        verify(mockQuery).orderByChild("location");
    }

    // ── Button navigation ────────────────────────────────────────────────────

    @Test
    public void myReservationsButton_startsMyReservationsActivity() {
        TestMainActivity activity = buildActivity();
        activity.findViewById(R.id.btnMyReservations).performClick();

        Intent started = Shadows.shadowOf(activity).getNextStartedActivity();
        assertNotNull(started);
        assertEquals(MyReservationsActivity.class.getName(),
                started.getComponent().getClassName());
    }

    @Test
    public void logoutButton_callsLogoutAndStartsLoginActivity() {
        TestMainActivity activity = buildActivity();
        activity.findViewById(R.id.btnLogout).performClick();

        verify(mockUserRepo).logout();
        Intent started = Shadows.shadowOf(activity).getNextStartedActivity();
        assertNotNull(started);
        assertEquals(LoginActivity.class.getName(), started.getComponent().getClassName());
    }

    // ── Lifecycle ────────────────────────────────────────────────────────────

    @Test
    public void onStart_startsAdapterListening() {
        ActivityController<TestMainActivity> controller =
                Robolectric.buildActivity(TestMainActivity.class).create().start();

        verify(controller.get().adapter).startListening();
    }

    @Test
    public void onStop_stopsAdapterListening() {
        ActivityController<TestMainActivity> controller =
                Robolectric.buildActivity(TestMainActivity.class).create().start().resume().stop();

        verify(controller.get().adapter).stopListening();
    }
}
