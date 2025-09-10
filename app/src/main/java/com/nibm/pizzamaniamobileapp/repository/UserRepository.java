package com.nibm.pizzamaniamobileapp.repository;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nibm.pizzamaniamobileapp.model.User;

public class UserRepository {
    private final FirebaseAuth auth;
    private final FirebaseFirestore db;
    public UserRepository() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }
    public Task<AuthResult> register(String name, String email, String phone, String password) {
        return auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    if (auth.getCurrentUser() != null) {
                        String uid = auth.getCurrentUser().getUid();
                        User user = new User(
                                uid,
                                name,
                                email,
                                phone,
                                "customer", // default role
                                com.google.firebase.Timestamp.now()
                        );
                        db.collection("users").document(uid).set(user);
                    }
                });
    }
    public Task<AuthResult> login(String email, String password) {
        return auth.signInWithEmailAndPassword(email, password);
    }
    public FirebaseAuth getAuth() {
        return auth;
    }
}
