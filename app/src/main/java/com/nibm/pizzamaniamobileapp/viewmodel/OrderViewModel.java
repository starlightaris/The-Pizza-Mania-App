package com.nibm.pizzamaniamobileapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.nibm.pizzamaniamobileapp.model.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderViewModel extends ViewModel {

    private final MutableLiveData<List<Order>> ordersLiveData = new MutableLiveData<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public LiveData<List<Order>> getOrdersLiveData() {
        return ordersLiveData;
    }

    public void fetchOrders(String branchId, String customerQuery) {
        Query query = db.collection("orders");

        // Apply branch filter if not empty
        if (branchId != null && !branchId.isEmpty()) {
            query = query.whereEqualTo("branchId", branchId);
        }

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Order> list = new ArrayList<>();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    Order order = doc.toObject(Order.class);
                    order.setOrderId(doc.getId());
                    list.add(order);

                    // Customer name filtering in Java
                    boolean matchesCustomer = true;
                    if (customerQuery != null && !customerQuery.isEmpty()) {
                        String customerName = order.getCustomerName() != null ?
                                order.getCustomerName().toLowerCase().trim() : "";
                        matchesCustomer = customerName.contains(customerQuery.toLowerCase().trim());
                    }

                    if (matchesCustomer) {
                        list.add(order);
                    }
                }
                ordersLiveData.setValue(list);
            } else {
                ordersLiveData.setValue(new ArrayList<>());
            }
        });
    }
}
