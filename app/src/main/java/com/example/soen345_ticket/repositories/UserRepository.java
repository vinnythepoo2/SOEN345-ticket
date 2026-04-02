package com.example.soen345_ticket.repositories;

import com.example.soen345_ticket.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserRepository {
    private final FirebaseAuth auth;
    private final DatabaseReference db;

    public UserRepository() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference("users");
    }

    public Task<Void> saveUser(User user) {
        return db.child(user.getUserId()).setValue(user);
    }

    public Task<DataSnapshot> getCurrentUser() {
        String uid = auth.getUid();
        if (uid == null) return null;
        return db.child(uid).get();
    }

    public String getCurrentUserId() {
        return auth.getUid();
    }

    public void logout() {
        auth.signOut();
    }
}
