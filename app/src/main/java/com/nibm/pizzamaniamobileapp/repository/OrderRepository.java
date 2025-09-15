package com.nibm.pizzamaniamobileapp.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.nibm.pizzamaniamobileapp.model.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderRepository {

    private final FirebaseFirestore db;

    public OrderRepository() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Fetch orders with optional branch filter and search.
     * - branchId: if non-null -> whereEqualTo("branchId", branchId).
     * - searchQuery: if non-empty -> client-side filter on userId, customerName, deliveryAddress (case-insensitive contains or exact)
     *
     * Results returned through ordersLiveData.
     */
    public void getOrders(String branchId, String searchQuery, MutableLiveData<List<Order>> ordersLiveData) {
        Query baseQuery = db.collection("orders").orderBy("createdAt", Query.Direction.DESCENDING);

        if (branchId != null && !branchId.isEmpty()) {
            baseQuery = db.collection("orders")
                    .whereEqualTo("branchId", branchId)
                    .orderBy("createdAt", Query.Direction.DESCENDING);
        }

        String finalSearch = (searchQuery == null) ? "" : searchQuery.trim().toLowerCase();

        baseQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<Order> list = new ArrayList<>();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    Order o = doc.toObject(Order.class);
                    o.setOrderId(doc.getId());
                    if (finalSearch.isEmpty()) {
                        list.add(o);
                    } else {
                        boolean matched = false;
                        // userId exact or contains
                        if (o.getUserId() != null && o.getUserId().toLowerCase().contains(finalSearch)) matched = true;
                        // deliveryAddress contains
                        if (!matched && o.getDeliveryAddress() != null && o.getDeliveryAddress().toLowerCase().contains(finalSearch)) matched = true;
                        // customerName if present
                        if (!matched && o.getCustomerName() != null && o.getCustomerName().toLowerCase().contains(finalSearch)) matched = true;

                        if (matched) list.add(o);
                    }
                }
                ordersLiveData.setValue(list);
            } else {
                Log.e("OrderRepo", "Failed to fetch orders", task.getException());
                ordersLiveData.setValue(new ArrayList<>());
            }
        }).addOnFailureListener(e -> {
            Log.e("OrderRepo", "Error fetching orders", e);
            ordersLiveData.setValue(new ArrayList<>());
        });
    }
}
