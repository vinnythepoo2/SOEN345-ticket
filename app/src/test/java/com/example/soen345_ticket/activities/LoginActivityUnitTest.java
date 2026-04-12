package com.example.soen345_ticket.activities;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.os.Looper;
import android.widget.EditText;

import com.example.soen345_ticket.R;
import com.example.soen345_ticket.repositories.UserRepository;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowToast;

@RunWith(RobolectricTestRunner.class)
public class LoginActivityUnitTest {

    public static class TestLoginActivity extends LoginActivity {
        static UserRepository userRepository;
        static FirebaseAuth firebaseAuth;
        boolean navigatedToAdmin = false;
        boolean navigatedToMain = false;
        boolean navigatedToRegister = false;

        @Override
        protected UserRepository createUserRepository() {
            return userRepository != null ? userRepository : super.createUserRepository();
        }

        @Override
        protected FirebaseAuth getFirebaseAuth() {
            return firebaseAuth != null ? firebaseAuth : super.getFirebaseAuth();
        }

        @Override
        protected void navigateToAdminDashboard() {
            navigatedToAdmin = true;
        }

        @Override
        protected void navigateToMain() {
            navigatedToMain = true;
        }

        @Override
        protected void navigateToRegister() {
            navigatedToRegister = true;
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
        TestLoginActivity.reset();
        mockAuth = mock(FirebaseAuth.class);
        mockRepo = mock(UserRepository.class);
        TestLoginActivity.firebaseAuth = mockAuth;
        TestLoginActivity.userRepository = mockRepo;
    }

    @Test
    public void login_emptyFields_showsToast() {
        try (ActivityController<TestLoginActivity> controller = Robolectric.buildActivity(TestLoginActivity.class)) {
            TestLoginActivity activity = controller.setup().get();
            activity.findViewById(R.id.btnLogin).performClick();

            assertEquals("Please fill in all fields", ShadowToast.getTextOfLatestToast());
        }
    }

    @Test
    public void login_successAdmin_navigatesToAdmin() {
        when(mockAuth.signInWithEmailAndPassword("admin@test.com", "password"))
                .thenReturn(Tasks.forResult(mock(AuthResult.class)));
        
        DataSnapshot mockSnapshot = mock(DataSnapshot.class);
        DataSnapshot mockRoleSnapshot = mock(DataSnapshot.class);
        when(mockSnapshot.exists()).thenReturn(true);
        when(mockSnapshot.child("role")).thenReturn(mockRoleSnapshot);
        when(mockRoleSnapshot.getValue(String.class)).thenReturn("admin");
        when(mockRepo.getCurrentUser()).thenReturn(Tasks.forResult(mockSnapshot));

        try (ActivityController<TestLoginActivity> controller = Robolectric.buildActivity(TestLoginActivity.class)) {
            TestLoginActivity activity = controller.setup().get();
            
            ((EditText) activity.findViewById(R.id.etEmail)).setText("admin@test.com");
            ((EditText) activity.findViewById(R.id.etPassword)).setText("password");
            activity.findViewById(R.id.btnLogin).performClick();
            
            Shadows.shadowOf(Looper.getMainLooper()).idle();

            assertEquals(true, activity.navigatedToAdmin);
        }
    }

    @Test
    public void login_successCustomer_navigatesToMain() {
        when(mockAuth.signInWithEmailAndPassword("user@test.com", "password"))
                .thenReturn(Tasks.forResult(mock(AuthResult.class)));
        
        DataSnapshot mockSnapshot = mock(DataSnapshot.class);
        DataSnapshot mockRoleSnapshot = mock(DataSnapshot.class);
        when(mockSnapshot.exists()).thenReturn(true);
        when(mockSnapshot.child("role")).thenReturn(mockRoleSnapshot);
        when(mockRoleSnapshot.getValue(String.class)).thenReturn("customer");
        when(mockRepo.getCurrentUser()).thenReturn(Tasks.forResult(mockSnapshot));

        try (ActivityController<TestLoginActivity> controller = Robolectric.buildActivity(TestLoginActivity.class)) {
            TestLoginActivity activity = controller.setup().get();
            
            ((EditText) activity.findViewById(R.id.etEmail)).setText("user@test.com");
            ((EditText) activity.findViewById(R.id.etPassword)).setText("password");
            activity.findViewById(R.id.btnLogin).performClick();
            
            Shadows.shadowOf(Looper.getMainLooper()).idle();

            assertEquals(true, activity.navigatedToMain);
        }
    }

    @Test
    public void login_repoFailure_showsToast() {
        when(mockAuth.signInWithEmailAndPassword("user@test.com", "password"))
                .thenReturn(Tasks.forResult(mock(AuthResult.class)));
        
        when(mockRepo.getCurrentUser()).thenReturn(Tasks.forException(new Exception("DB fail")));

        try (ActivityController<TestLoginActivity> controller = Robolectric.buildActivity(TestLoginActivity.class)) {
            TestLoginActivity activity = controller.setup().get();
            
            ((EditText) activity.findViewById(R.id.etEmail)).setText("user@test.com");
            ((EditText) activity.findViewById(R.id.etPassword)).setText("password");
            activity.findViewById(R.id.btnLogin).performClick();
            
            Shadows.shadowOf(Looper.getMainLooper()).idle();

            assertEquals("Error getting user info", ShadowToast.getTextOfLatestToast());
        }
    }

    @Test
    public void login_failure_showsToast() {
        when(mockAuth.signInWithEmailAndPassword(anyString(), anyString()))
                .thenReturn(Tasks.forException(new Exception("Auth failed")));

        try (ActivityController<TestLoginActivity> controller = Robolectric.buildActivity(TestLoginActivity.class)) {
            TestLoginActivity activity = controller.setup().get();
            
            ((EditText) activity.findViewById(R.id.etEmail)).setText("test@test.com");
            ((EditText) activity.findViewById(R.id.etPassword)).setText("password");
            activity.findViewById(R.id.btnLogin).performClick();
            
            Shadows.shadowOf(Looper.getMainLooper()).idle();

            assertEquals("Authentication failed", ShadowToast.getTextOfLatestToast());
        }
    }

    @Test
    public void tvRegister_navigatesToRegister() {
        try (ActivityController<TestLoginActivity> controller = Robolectric.buildActivity(TestLoginActivity.class)) {
            TestLoginActivity activity = controller.setup().get();
            activity.findViewById(R.id.tvRegister).performClick();

            assertEquals(true, activity.navigatedToRegister);
        }
    }
}
