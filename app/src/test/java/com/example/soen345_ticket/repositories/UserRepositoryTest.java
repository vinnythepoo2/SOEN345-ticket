package com.example.soen345_ticket.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.soen345_ticket.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

public class UserRepositoryTest {

    @Mock
    private FirebaseAuth mockAuth;
    @Mock
    private FirebaseDatabase mockDatabase;
    @Mock
    private DatabaseReference mockDbRef;
    @Mock
    private DatabaseReference mockUsersRef;

    private MockedStatic<FirebaseAuth> mockedAuthStatic;
    private MockedStatic<FirebaseDatabase> mockedDbStatic;

    private UserRepository userRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        mockedAuthStatic = mockStatic(FirebaseAuth.class);
        mockedDbStatic = mockStatic(FirebaseDatabase.class);

        when(FirebaseAuth.getInstance()).thenReturn(mockAuth);
        when(FirebaseDatabase.getInstance()).thenReturn(mockDatabase);
        when(mockDatabase.getReference("users")).thenReturn(mockUsersRef);

        userRepository = new UserRepository();
    }

    @After
    public void tearDown() {
        mockedAuthStatic.close();
        mockedDbStatic.close();
    }

    @Test
    public void testSaveUser() {
        User user = new User("u1", "Name", "email@test.com", "123", "customer");
        DatabaseReference mockUserChildRef = mock(DatabaseReference.class);
        when(mockUsersRef.child("u1")).thenReturn(mockUserChildRef);
        Task<Void> mockTask = mock(Task.class);
        when(mockUserChildRef.setValue(user)).thenReturn(mockTask);

        Task<Void> result = userRepository.saveUser(user);

        verify(mockUsersRef).child("u1");
        verify(mockUserChildRef).setValue(user);
        assertEquals(mockTask, result);
    }

    @Test
    public void testGetCurrentUserId() {
        when(mockAuth.getUid()).thenReturn("test-uid");
        String uid = userRepository.getCurrentUserId();
        assertEquals("test-uid", uid);
    }

    @Test
    public void testLogout() {
        userRepository.logout();
        verify(mockAuth).signOut();
    }

    @Test
    public void testGetCurrentUser_NullUid() {
        when(mockAuth.getUid()).thenReturn(null);
        Task<DataSnapshot> result = userRepository.getCurrentUser();
        assertNull(result);
    }
}
