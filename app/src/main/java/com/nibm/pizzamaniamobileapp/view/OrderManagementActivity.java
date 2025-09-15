package com.nibm.pizzamaniamobileapp.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.nibm.pizzamaniamobileapp.R;
import com.nibm.pizzamaniamobileapp.adapter.OrderAdapter;
import com.nibm.pizzamaniamobileapp.adapter.OrderItemAdapter;
import com.nibm.pizzamaniamobileapp.model.MenuItem;
import com.nibm.pizzamaniamobileapp.model.Order;
import com.nibm.pizzamaniamobileapp.model.OrderItem;
import com.nibm.pizzamaniamobileapp.viewmodel.OrderViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderManagementActivity extends AppCompatActivity {

    private RecyclerView ordersRecycler;
    private OrderAdapter adapter;
    private OrderViewModel viewModel;

    private Spinner branchSpinner;
    private EditText searchCustomer;

    private List<String> branchNames = new ArrayList<>();
    private List<String> branchIds = new ArrayList<>();
    private String selectedBranchId = "";
    private String customerQuery = "";

    // Cache menu items for image URLs
    private final Map<String, String> menuImageMap = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_management);

        ordersRecycler = findViewById(R.id.orders_recycler);
        ordersRecycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderAdapter(new ArrayList<>());
        ordersRecycler.setAdapter(adapter);

        branchSpinner = findViewById(R.id.branch_filter_spinner);
        searchCustomer = findViewById(R.id.search_customer);

        viewModel = new ViewModelProvider(this).get(OrderViewModel.class);

        viewModel.getOrdersLiveData().observe(this, orders -> {
            // Inject image URLs into order items from cached menu items
            for (Order order : orders) {
                for (OrderItem item : order.getItems()) {
                    if (menuImageMap.containsKey(item.getMenuId())) {
                        item.setImageUrl(menuImageMap.get(item.getMenuId()));
                    }
                }
            }
            adapter.updateList(orders);
        });

        fetchBranchesFromFirestore();
        fetchMenuImages(); // populate menuImageMap

        branchSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                selectedBranchId = position == 0 ? "" : branchIds.get(position - 1); // 0 = "All Branches"
                refreshOrders();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) { }
        });

        searchCustomer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                customerQuery = s.toString();
                refreshOrders();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void fetchBranchesFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("branches")
                .get()
                .addOnCompleteListener(task -> {
                    branchNames.clear();
                    branchIds.clear();
                    branchNames.add("All Branches"); // default option

                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            branchNames.add(doc.getString("name"));
                            branchIds.add(doc.getId());
                        }
                    }

                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                            android.R.layout.simple_spinner_item, branchNames);
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    branchSpinner.setAdapter(spinnerAdapter);
                });
    }

    private void fetchMenuImages() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("menu")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            String menuId = doc.getId();
                            String imageUrl = doc.getString("imageUrl");
                            if (imageUrl != null) {
                                menuImageMap.put(menuId, imageUrl);
                            }
                        }
                    }
                });
    }

    private void refreshOrders() {
        viewModel.fetchOrders(selectedBranchId, customerQuery);
    }
}
