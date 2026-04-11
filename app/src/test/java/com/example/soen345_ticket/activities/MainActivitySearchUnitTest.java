package com.example.soen345_ticket.activities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import com.example.soen345_ticket.R;
import com.example.soen345_ticket.repositories.EventRepository;
import com.example.soen345_ticket.repositories.UserRepository;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

@RunWith(RobolectricTestRunner.class)
public class MainActivitySearchUnitTest {

    public static class TestMainActivity extends MainActivity {
        static EventRepository eventRepository;
        static UserRepository userRepository;

        @Override
        protected EventRepository createEventRepository() {
            return eventRepository != null ? eventRepository : super.createEventRepository();
        }

        @Override
        protected UserRepository createUserRepository() {
            return userRepository != null ? userRepository : super.createUserRepository();
        }

        @Override
        protected void setupRecyclerView(Query query) {
            // No-op for unit testing search logic to avoid Firebase adapter initialization
        }

        @Override
        protected void updateRecyclerView(Query query) {
            lastQuery = query;
        }

        Query lastQuery;

        static void reset() {
            eventRepository = null;
            userRepository = null;
        }
    }

    private EventRepository mockEventRepo;
    private DatabaseReference mockDbRef;

    @Before
    public void setUp() {
        TestMainActivity.reset();
        mockEventRepo = mock(EventRepository.class);
        mockDbRef = mock(DatabaseReference.class);
        
        // Return a mock base query
        when(mockEventRepo.getEventsQuery()).thenReturn(mockDbRef);
        when(mockEventRepo.getAllEventsForAdminQuery()).thenReturn(mockDbRef);
        
        TestMainActivity.eventRepository = mockEventRepo;
        TestMainActivity.userRepository = mock(UserRepository.class);
    }

    @Test
    public void searchWithEmptyText_resetsToAllEvents() {
        try (ActivityController<TestMainActivity> controller = Robolectric.buildActivity(TestMainActivity.class)) {
            TestMainActivity activity = controller.setup().get();
            EditText searchBox = activity.findViewById(R.id.etSearch);
            searchBox.setText("");
            
            activity.findViewById(R.id.btnFilter).performClick();

            assertEquals(mockDbRef, activity.lastQuery);
        }
    }

    @Test
    public void searchWithValidText_appliesTitleFilter() {
        when(mockDbRef.orderByChild("title")).thenReturn(mockDbRef);
        when(mockDbRef.startAt("Jazz")).thenReturn(mockDbRef);
        when(mockDbRef.endAt("Jazz\uf8ff")).thenReturn(mockDbRef);

        try (ActivityController<TestMainActivity> controller = Robolectric.buildActivity(TestMainActivity.class)) {
            TestMainActivity activity = controller.setup().get();
            EditText searchBox = activity.findViewById(R.id.etSearch);
            searchBox.setText("Jazz");
            
            activity.findViewById(R.id.btnFilter).performClick();

            assertNotNull(activity.lastQuery);
        }
    }

    @Test
    public void filterWithCategory_appliesCategoryFilter() {
        when(mockDbRef.orderByChild("category")).thenReturn(mockDbRef);
        when(mockDbRef.startAt("Music")).thenReturn(mockDbRef);
        when(mockDbRef.endAt("Music\uf8ff")).thenReturn(mockDbRef);

        try (ActivityController<TestMainActivity> controller = Robolectric.buildActivity(TestMainActivity.class)) {
            TestMainActivity activity = controller.setup().get();
            
            // Switch to filter tab
            activity.findViewById(R.id.btnTabFilter).performClick();
            
            EditText filterBox = activity.findViewById(R.id.etFilterValue);
            Spinner typeSpinner = activity.findViewById(R.id.spinnerFilterType);
            
            typeSpinner.setSelection(1); // Category
            filterBox.setText("Music");
            
            activity.findViewById(R.id.btnApplyFilter).performClick();

            assertNotNull(activity.lastQuery);
        }
    }

    @Test
    public void switchingTabs_resetsResults() {
        try (ActivityController<TestMainActivity> controller = Robolectric.buildActivity(TestMainActivity.class)) {
            TestMainActivity activity = controller.setup().get();
            
            // Perform a search first
            activity.lastQuery = null;
            activity.findViewById(R.id.btnTabFilter).performClick();

            // Should reset to default query
            assertEquals(mockDbRef, activity.lastQuery);
        }
    }

    @Test
    public void searchWithWhitespaceOnly_resetsToAllEvents() {
        try (ActivityController<TestMainActivity> controller = Robolectric.buildActivity(TestMainActivity.class)) {
            TestMainActivity activity = controller.setup().get();
            EditText searchBox = activity.findViewById(R.id.etSearch);
            searchBox.setText("   ");
            
            activity.findViewById(R.id.btnFilter).performClick();

            assertEquals(mockDbRef, activity.lastQuery);
        }
    }
}
