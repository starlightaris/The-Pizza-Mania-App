package com.nibm.pizzamaniamobileapp.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nibm.pizzamaniamobileapp.model.Address;

import java.util.ArrayList;
import java.util.List;

public class ProfileViewModel extends ViewModel {

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private MutableLiveData<String> userName = new MutableLiveData<>();
    private MutableLiveData<String> email = new MutableLiveData<>();
    private MutableLiveData<String> phone = new MutableLiveData<>();
    private final MutableLiveData<List<Address>> addressListLiveData = new MutableLiveData<>(new ArrayList<>());

    public ProfileViewModel() {
        loadUserData();
    }

    public LiveData<String> getUserName() { return userName; }
    public LiveData<String> getEmail() { return email; }
    public LiveData<String> getPhone() { return phone; }
    public LiveData<List<Address>> getAddressListLiveData() { return addressListLiveData; }

    // 🔹 Load user info from Firebase
    private void loadUserData() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            email.setValue(user.getEmail());

            db.collection("users").document(user.getUid())
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        if (snapshot.exists()) {
                            userName.setValue(snapshot.getString("fullName"));
                            phone.setValue(snapshot.getString("phone"));
                        }
                    })
                    .addOnFailureListener(e -> Log.e("ProfileVM", "Failed to load user", e));
        }
    }

    // 🔹 Update profile data
    public void updateUser(String name, String emailAddress, String phoneNum) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            db.collection("users").document(user.getUid())
                    .update("fullName", name, "phone", phoneNum, "email", emailAddress)
                    .addOnSuccessListener(aVoid -> {
                        userName.setValue(name);
                        email.setValue(emailAddress);
                        phone.setValue(phoneNum);
                    })
                    .addOnFailureListener(e -> Log.e("ProfileVM", "Update failed", e));
        }
    }

    // 🔹 Address CRUD
    public void addAddress(Address address) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        db.collection("users").document(user.getUid())
                .collection("addresses")
                .add(address)
                .addOnSuccessListener(doc -> loadAddresses())
                .addOnFailureListener(e -> Log.e("ProfileVM", "Add address failed", e));
    }

    public void updateAddress(Address updated) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        db.collection("users").document(user.getUid())
                .collection("addresses")
                .document(updated.getAddressId())
                .set(updated)
                .addOnSuccessListener(aVoid -> loadAddresses())
                .addOnFailureListener(e -> Log.e("ProfileVM", "Update address failed", e));
    }

    public void deleteAddress(Address toDelete) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        db.collection("users").document(user.getUid())
                .collection("addresses")
                .document(toDelete.getAddressId())
                .delete()
                .addOnSuccessListener(aVoid -> loadAddresses())
                .addOnFailureListener(e -> Log.e("ProfileVM", "Delete address failed", e));
    }

    private void loadAddresses() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        db.collection("users").document(user.getUid())
                .collection("addresses")
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Address> list = new ArrayList<>();
                    snapshot.forEach(doc -> {
                        Address addr = doc.toObject(Address.class);
                        addr.setAddressId(doc.getId());
                        list.add(addr);
                    });
                    addressListLiveData.setValue(list);
                })
                .addOnFailureListener(e -> Log.e("ProfileVM", "Load addresses failed", e));
    }

    // 🔹 Logout
    public void logout() {
        auth.signOut();
    }

    // 🔹 Delete account
    public void deleteAccount(DeleteAccountCallback callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            callback.onComplete(false);
            return;
        }

        String uid = user.getUid();

        // 1. Delete Firestore user document
        db.collection("users").document(uid).delete()
                .addOnSuccessListener(aVoid -> {
                    // 2. Delete Firebase Auth account
                    user.delete()
                            .addOnSuccessListener(aVoid2 -> callback.onComplete(true))
                            .addOnFailureListener(e -> {
                                Log.e("ProfileVM", "Auth delete failed", e);
                                callback.onComplete(false);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("ProfileVM", "Firestore delete failed", e);
                    callback.onComplete(false);
                });
    }

    // Callback interface
    public interface DeleteAccountCallback {
        void onComplete(boolean success);
    }
}
