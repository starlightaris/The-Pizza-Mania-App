package com.nibm.pizzamaniamobileapp.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

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
    public Task<AuthResult> register(String name, String email, String phone, String password, String role) {
        return auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getUser() != null) {
                        String uid = task.getResult().getUser().getUid();
                        User user = new User(
                                uid,
                                name,
                                email,
                                phone,
                                role,
                                com.google.firebase.Timestamp.now()
                        );
                        db.collection("users").document(uid).set(user)
                                .addOnSuccessListener(unused ->
                                        Log.d("Firestore", "✅ User added to users collection"))
                                .addOnFailureListener(e ->
                                        Log.e("Firestore", "❌ Failed to add user", e));
                    } else {
                        if (task.getException() != null) {
                            Log.e("FirebaseAuth", "❌ Registration failed", task.getException());
                        }
                    }
                });
    }
    public Task<AuthResult> login(String email, String password) {
        return auth.signInWithEmailAndPassword(email, password);
    }
    public void fetchUserRole(String uid, MutableLiveData<String> roleLiveData) {
        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");
                        roleLiveData.setValue(role);
                    } else {
                        roleLiveData.setValue(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "❌ Failed to fetch role", e);
                    roleLiveData.setValue(null);
                });
    }
    public FirebaseAuth getAuth() {
        return auth;
    }
}
