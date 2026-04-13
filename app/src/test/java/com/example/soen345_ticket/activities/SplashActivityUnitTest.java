package com.example.soen345_ticket.activities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Intent;
import android.os.Looper;

import com.example.soen345_ticket.repositories.UserRepository;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;

@RunWith(RobolectricTestRunner.class)
public class SplashActivityUnitTest {

    public static class TestSplashActivity extends SplashActivity {
        static UserRepository userRepository;
        static FirebaseAuth firebaseAuth;

        @Override
        protected UserRepository createUserRepository() {
            return userRepository != null ? userRepository : super.createUserRepository();
        }

        @Override
        protected FirebaseAuth getFirebaseAuth() {
            return firebaseAuth != null ? firebaseAuth : super.getFirebaseAuth();
        }

        @Override
        protected long getDelayMillis() {
            return 0; // No delay for testing
        }

        static void reset() {
            userRepository = null;
            firebaseAuth = null;
        }
    }

    private FirebaseAuth mockAuth;
    private UserRepository mockRepo;

    @Before
    public void setUp() {
        TestSplashActivity.reset();
        mockAuth = mock(FirebaseAuth.class);
        mockRepo = mock(UserRepository.class);
        TestSplashActivity.firebaseAuth = mockAuth;
        TestSplashActivity.userRepository = mockRepo;
    }

    @Test
    public void splash_noUser_navigatesToLogin() {
        when(mockAuth.getCurrentUser()).thenReturn(null);

        try (ActivityController<TestSplashActivity> controller = Robolectric.buildActivity(TestSplashActivity.class)) {
            TestSplashActivity activity = controller.setup().get();
            Shadows.shadowOf(Looper.getMainLooper()).idle();

            Intent startedIntent = Shadows.shadowOf(activity).getNextStartedActivity();
            assertEquals(LoginActivity.class.getName(), startedIntent.getComponent().getClassName());
        }
    }

    @Test
    public void splash_withAdmin_navigatesToAdminDashboard() {
        FirebaseUser mockUser = mock(FirebaseUser.class);
        when(mockAuth.getCurrentUser()).thenReturn(mockUser);
        
        DataSnapshot mockSnapshot = mock(DataSnapshot.class);
        DataSnapshot mockRoleSnapshot = mock(DataSnapshot.class);
        when(mockSnapshot.exists()).thenReturn(true);
        when(mockSnapshot.child("role")).thenReturn(mockRoleSnapshot);
        when(mockRoleSnapshot.getValue(String.class)).thenReturn("admin");
        when(mockRepo.getCurrentUser()).thenReturn(Tasks.forResult(mockSnapshot));

        try (ActivityController<TestSplashActivity> controller = Robolectric.buildActivity(TestSplashActivity.class)) {
            TestSplashActivity activity = controller.setup().get();
            Shadows.shadowOf(Looper.getMainLooper()).idle();

            Intent startedIntent = Shadows.shadowOf(activity).getNextStartedActivity();
            assertEquals(AdminDashboardActivity.class.getName(), startedIntent.getComponent().getClassName());
        }
    }

    @Test
    public void splash_withCustomer_navigatesToMain() {
        FirebaseUser mockUser = mock(FirebaseUser.class);
        when(mockAuth.getCurrentUser()).thenReturn(mockUser);
        
        DataSnapshot mockSnapshot = mock(DataSnapshot.class);
        DataSnapshot mockRoleSnapshot = mock(DataSnapshot.class);
        when(mockSnapshot.exists()).thenReturn(true);
        when(mockSnapshot.child("role")).thenReturn(mockRoleSnapshot);
        when(mockRoleSnapshot.getValue(String.class)).thenReturn("customer");
        when(mockRepo.getCurrentUser()).thenReturn(Tasks.forResult(mockSnapshot));

        try (ActivityController<TestSplashActivity> controller = Robolectric.buildActivity(TestSplashActivity.class)) {
            TestSplashActivity activity = controller.setup().get();
            Shadows.shadowOf(Looper.getMainLooper()).idle();

            Intent startedIntent = Shadows.shadowOf(activity).getNextStartedActivity();
            assertEquals(MainActivity.class.getName(), startedIntent.getComponent().getClassName());
        }
    }

    @Test
    public void splash_userNotInDatabase_signsOutAndNavigatesToLogin() {
        FirebaseUser mockUser = mock(FirebaseUser.class);
        when(mockAuth.getCurrentUser()).thenReturn(mockUser);
        
        DataSnapshot mockSnapshot = mock(DataSnapshot.class);
        when(mockSnapshot.exists()).thenReturn(false);
        when(mockRepo.getCurrentUser()).thenReturn(Tasks.forResult(mockSnapshot));

        try (ActivityController<TestSplashActivity> controller = Robolectric.buildActivity(TestSplashActivity.class)) {
            TestSplashActivity activity = controller.setup().get();
            Shadows.shadowOf(Looper.getMainLooper()).idle();

            verify(mockAuth).signOut();
            Intent startedIntent = Shadows.shadowOf(activity).getNextStartedActivity();
            assertEquals(LoginActivity.class.getName(), startedIntent.getComponent().getClassName());
        }
    }

    @Test
    public void splash_databaseFailure_signsOutAndNavigatesToLogin() {
        FirebaseUser mockUser = mock(FirebaseUser.class);
        when(mockAuth.getCurrentUser()).thenReturn(mockUser);
        
        when(mockRepo.getCurrentUser()).thenReturn(Tasks.forException(new Exception("DB fail")));

        try (ActivityController<TestSplashActivity> controller = Robolectric.buildActivity(TestSplashActivity.class)) {
            TestSplashActivity activity = controller.setup().get();
            Shadows.shadowOf(Looper.getMainLooper()).idle();

            verify(mockAuth).signOut();
            Intent startedIntent = Shadows.shadowOf(activity).getNextStartedActivity();
            assertEquals(LoginActivity.class.getName(), startedIntent.getComponent().getClassName());
        }
    }
}
