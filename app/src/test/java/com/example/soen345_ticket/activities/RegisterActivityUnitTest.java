package com.example.soen345_ticket.activities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.os.Looper;
import android.widget.EditText;
import com.example.soen345_ticket.repositories.UserRepository;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowToast;

@RunWith(RobolectricTestRunner.class)
public class RegisterActivityUnitTest {

    public static class TestRegisterActivity extends RegisterActivity {
        static UserRepository userRepository;
        static FirebaseAuth firebaseAuth;
        boolean navigatedToMain = false;

        @Override
        protected UserRepository createUserRepository() {
            return userRepository != null ? userRepository : super.createUserRepository();
        }

        @Override
        protected FirebaseAuth getFirebaseAuth() {
            return firebaseAuth != null ? firebaseAuth : super.getFirebaseAuth();
        }

        @Override
        protected void navigateToMain() {
            navigatedToMain = true;
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
        TestRegisterActivity.reset();
        mockAuth = mock(FirebaseAuth.class);
        mockRepo = mock(UserRepository.class);
        TestRegisterActivity.firebaseAuth = mockAuth;
        TestRegisterActivity.userRepository = mockRepo;
    }

    private void setText(TestRegisterActivity activity, int viewId, String text) {
        EditText editText = activity.findViewById(viewId);
        editText.setText(text);
    }

    @Test
    public void registerWithEmptyFields_showsToast() {
        try (ActivityController<TestRegisterActivity> controller = Robolectric.buildActivity(TestRegisterActivity.class)) {
            TestRegisterActivity activity = controller.setup().get();
            activity.findViewById(com.example.soen345_ticket.R.id.btnRegister).performClick();

            assertEquals("Please fill in all fields", ShadowToast.getTextOfLatestToast());
            verify(mockAuth, never()).createUserWithEmailAndPassword(anyString(), anyString());
        }
    }

    @Test
    public void registerWithShortPassword_showsToast() {
        try (ActivityController<TestRegisterActivity> controller = Robolectric.buildActivity(TestRegisterActivity.class)) {
            TestRegisterActivity activity = controller.setup().get();
            
            setText(activity, com.example.soen345_ticket.R.id.etFullName, "John Doe");
            setText(activity, com.example.soen345_ticket.R.id.etEmail, "john@example.com");
            setText(activity, com.example.soen345_ticket.R.id.etPhone, "5141234567");
            setText(activity, com.example.soen345_ticket.R.id.etPassword, "123");

            activity.findViewById(com.example.soen345_ticket.R.id.btnRegister).performClick();

            assertEquals("Password must be at least 6 characters", ShadowToast.getTextOfLatestToast());
            verify(mockAuth, never()).createUserWithEmailAndPassword(anyString(), anyString());
        }
    }

    @Test
    public void registerWithExistingEmail_showsCollisionError() {
        when(mockAuth.createUserWithEmailAndPassword("exists@example.com", "password123"))
                .thenReturn(Tasks.forException(new FirebaseAuthUserCollisionException("error", "The email address is already in use by another account.")));

        try (ActivityController<TestRegisterActivity> controller = Robolectric.buildActivity(TestRegisterActivity.class)) {
            TestRegisterActivity activity = controller.setup().get();
            setText(activity, com.example.soen345_ticket.R.id.etFullName, "John Doe");
            setText(activity, com.example.soen345_ticket.R.id.etEmail, "exists@example.com");
            setText(activity, com.example.soen345_ticket.R.id.etPhone, "5141234567");
            setText(activity, com.example.soen345_ticket.R.id.etPassword, "password123");

            activity.findViewById(com.example.soen345_ticket.R.id.btnRegister).performClick();
            Shadows.shadowOf(Looper.getMainLooper()).idle();

            assertEquals("Registration failed: The email address is already in use by another account.", ShadowToast.getTextOfLatestToast());
        }
    }

    @Test
    public void registerWithInvalidEmail_showsInvalidEmailError() {
        when(mockAuth.createUserWithEmailAndPassword("invalid-email", "password123"))
                .thenReturn(Tasks.forException(new Exception("The email address is badly formatted.")));

        try (ActivityController<TestRegisterActivity> controller = Robolectric.buildActivity(TestRegisterActivity.class)) {
            TestRegisterActivity activity = controller.setup().get();
            setText(activity, com.example.soen345_ticket.R.id.etFullName, "John Doe");
            setText(activity, com.example.soen345_ticket.R.id.etEmail, "invalid-email");
            setText(activity, com.example.soen345_ticket.R.id.etPhone, "5141234567");
            setText(activity, com.example.soen345_ticket.R.id.etPassword, "password123");

            activity.findViewById(com.example.soen345_ticket.R.id.btnRegister).performClick();
            Shadows.shadowOf(Looper.getMainLooper()).idle();

            assertEquals("Registration failed: The email address is badly formatted.", ShadowToast.getTextOfLatestToast());
        }
    }

    @Test
    public void registerWithDatabaseFailure_showsSaveError() {
        AuthResult mockResult = mock(AuthResult.class);
        when(mockAuth.createUserWithEmailAndPassword("new@example.com", "password123"))
                .thenReturn(Tasks.forResult(mockResult));
        when(mockAuth.getUid()).thenReturn("new-uid");
        when(mockRepo.saveUser(any())).thenReturn(Tasks.forException(new Exception("Database disconnected")));

        try (ActivityController<TestRegisterActivity> controller = Robolectric.buildActivity(TestRegisterActivity.class)) {
            TestRegisterActivity activity = controller.setup().get();
            setText(activity, com.example.soen345_ticket.R.id.etFullName, "John Doe");
            setText(activity, com.example.soen345_ticket.R.id.etEmail, "new@example.com");
            setText(activity, com.example.soen345_ticket.R.id.etPhone, "5141234567");
            setText(activity, com.example.soen345_ticket.R.id.etPassword, "password123");

            activity.findViewById(com.example.soen345_ticket.R.id.btnRegister).performClick();
            Shadows.shadowOf(Looper.getMainLooper()).idle();

            assertEquals("Failed to save user info: Database disconnected", ShadowToast.getTextOfLatestToast());
        }
    }

    @Test
    public void registerSuccess_navigatesToMain() {
        AuthResult mockResult = mock(AuthResult.class);
        when(mockAuth.createUserWithEmailAndPassword("new@example.com", "password123"))
                .thenReturn(Tasks.forResult(mockResult));
        when(mockAuth.getUid()).thenReturn("new-uid");
        when(mockRepo.saveUser(any())).thenReturn(Tasks.forResult(null));

        try (ActivityController<TestRegisterActivity> controller = Robolectric.buildActivity(TestRegisterActivity.class)) {
            TestRegisterActivity activity = controller.setup().get();
            setText(activity, com.example.soen345_ticket.R.id.etFullName, "John Doe");
            setText(activity, com.example.soen345_ticket.R.id.etEmail, "new@example.com");
            setText(activity, com.example.soen345_ticket.R.id.etPhone, "5141234567");
            setText(activity, com.example.soen345_ticket.R.id.etPassword, "password123");

            activity.findViewById(com.example.soen345_ticket.R.id.btnRegister).performClick();
            Shadows.shadowOf(Looper.getMainLooper()).idle();

            assertEquals("Registration successful", ShadowToast.getTextOfLatestToast());
            assertTrue(activity.navigatedToMain);
        }
    }

    @Test
    public void tvLogin_finishesActivity() {
        try (ActivityController<TestRegisterActivity> controller = Robolectric.buildActivity(TestRegisterActivity.class)) {
            TestRegisterActivity activity = controller.setup().get();
            activity.findViewById(com.example.soen345_ticket.R.id.tvLogin).performClick();
            assertTrue(activity.isFinishing());
        }
    }
}
