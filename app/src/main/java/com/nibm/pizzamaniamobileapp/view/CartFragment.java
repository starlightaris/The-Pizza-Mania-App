package com.nibm.pizzamaniamobileapp.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nibm.pizzamaniamobileapp.R;
import com.nibm.pizzamaniamobileapp.adapter.CartAdapter;
import com.nibm.pizzamaniamobileapp.model.CartItem;
import com.nibm.pizzamaniamobileapp.model.Order;
import com.nibm.pizzamaniamobileapp.model.OrderItem;
import com.nibm.pizzamaniamobileapp.viewmodel.CartViewModel;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    private RecyclerView cartRecycler;
    private CartAdapter adapter;
    private TextView totalPriceText;
    private TextView tvEmptyMessage;
    private Button btnCheckout;
    private CartViewModel cartViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cart, container, false);

        cartRecycler = root.findViewById(R.id.cart_items_recycler);
        totalPriceText = root.findViewById(R.id.cart_total_price);
        btnCheckout = root.findViewById(R.id.cart_checkout_btn);

        // Initialize adapter
        adapter = new CartAdapter(new ArrayList<>());
        cartRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        cartRecycler.setAdapter(adapter);

        // Empty cart message
        tvEmptyMessage = root.findViewById(R.id.tv_empty_cart);
        if (tvEmptyMessage == null) {
            tvEmptyMessage = new TextView(getActivity());
            tvEmptyMessage.setText("Your cart is empty");
            tvEmptyMessage.setTextSize(16);
            tvEmptyMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            ((ViewGroup) cartRecycler.getParent()).addView(tvEmptyMessage);
        }
        tvEmptyMessage.setVisibility(View.GONE);

        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);

        // Observe cart items
        cartViewModel.getCartItemsLiveData().observe(getViewLifecycleOwner(), cartItems -> {
            adapter.updateList(cartItems);
            totalPriceText.setText("Total: Rs." + String.format("%.2f", cartViewModel.getTotalPrice()));

            if (cartItems.isEmpty()) {
                tvEmptyMessage.setVisibility(View.VISIBLE);
                cartRecycler.setVisibility(View.GONE);
            } else {
                tvEmptyMessage.setVisibility(View.GONE);
                cartRecycler.setVisibility(View.VISIBLE);
            }
        });

        // Checkout click
        btnCheckout.setOnClickListener(v -> {
            CheckoutFragment checkoutFragment = new CheckoutFragment();

            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayout, checkoutFragment);
            transaction.addToBackStack(null); //for back btn
            transaction.commit();
        });


        return root;
    }

    private void checkout() {
        List<CartItem> cartItems = cartViewModel.getCartItemsLiveData().getValue();
        if (cartItems == null || cartItems.isEmpty()) {
            Toast.makeText(getActivity(), "Cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = currentUser.getUid();
        FirebaseFirestore.getInstance().collection("users").document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    String customerName = doc.getString("fullName");
                    String deliveryAddress = doc.getString("address");

                    String selectedBranchId = cartViewModel.getSelectedBranchId();
                    String userId = uid;

                    // Convert CartItem → OrderItem
                    List<OrderItem> orderItems = new ArrayList<>();
                    for (CartItem ci : cartItems) {
                        orderItems.add(new OrderItem(ci.getMenuId(), ci.getName(), ci.getImageUrl(),
                                ci.getPrice(), ci.getQuantity()));
                    }

                    // Create order
                    Order order = new Order();
                    order.setItems(orderItems);
                    order.setTotalPrice(cartViewModel.getTotalPrice());
                    order.setStatus("Pending");
                    order.setCreatedAt(Timestamp.now());
                    order.setBranchId(selectedBranchId);
                    order.setCustomerName(customerName != null ? customerName : "");
                    order.setDeliveryAddress(deliveryAddress != null ? deliveryAddress : "");
                    order.setUserId(userId);

                    // Generate document ID first, then set order with orderId included
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    var docRef = db.collection("orders").document();
                    order.setOrderId(docRef.getId());  // set orderId before saving
                    docRef.set(order)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getActivity(), "Order placed!", Toast.LENGTH_SHORT).show();
                                cartViewModel.clearCart();
                            })
                            .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to place order", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to fetch user info", Toast.LENGTH_SHORT).show());
    }
}
