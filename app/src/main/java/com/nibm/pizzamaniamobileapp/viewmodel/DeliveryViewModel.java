package com.nibm.pizzamaniamobileapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.nibm.pizzamaniamobileapp.model.Order;

import java.util.HashMap;
import java.util.Map;

public class DeliveryViewModel extends ViewModel {

    private MutableLiveData<Order> orderLiveData = new MutableLiveData<>();
    private MutableLiveData<LatLng> userLocationLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    private FirebaseFirestore db;
    private ListenerRegistration orderListener;
    private String currentOrderId;

    public DeliveryViewModel() {
        db = FirebaseFirestore.getInstance();
    }

    public void startListeningToOrder(String orderId) {
        if (orderId == null) return;
        currentOrderId = orderId;

        orderListener = db.collection("orders").document(orderId)
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null) {
                        errorLiveData.setValue("Error listening to order updates: " + e.getMessage());
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        Order order = documentSnapshot.toObject(Order.class);
                        if (order != null) {
                            order.setOrderId(documentSnapshot.getId());
                            orderLiveData.setValue(order);
                        }
                    }
                });
    }

    public void updateUserLocation(LatLng location) {
        userLocationLiveData.setValue(location);

        if (currentOrderId != null) {
            Map<String, Object> updateData = new HashMap<>();
            updateData.put("deliveryCurrentLat", location.latitude);
            updateData.put("deliveryCurrentLng", location.longitude);
            updateData.put("locationUpdatedAt", FieldValue.serverTimestamp());

            db.collection("orders").document(currentOrderId)
                    .update(updateData)
                    .addOnFailureListener(e ->
                            errorLiveData.setValue("Failed to update location: " + e.getMessage()));
        }
    }

    public LiveData<Order> getOrderLiveData() {
        return orderLiveData;
    }

    public LiveData<LatLng> getUserLocationLiveData() {
        return userLocationLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (orderListener != null) {
            orderListener.remove();
        }
    }
}