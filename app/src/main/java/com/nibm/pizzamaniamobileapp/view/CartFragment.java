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
import com.nibm.pizzamaniamobileapp.utils.SessionManager;
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
    private SessionManager sessionManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cart, container, false);

        cartRecycler = root.findViewById(R.id.cart_items_recycler);
        totalPriceText = root.findViewById(R.id.cart_total_price);
        btnCheckout = root.findViewById(R.id.cart_checkout_btn);

        sessionManager = new SessionManager(requireActivity());

        // Initialize adapter
        adapter = new CartAdapter(new ArrayList<>());
        cartRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        cartRecycler.setAdapter(adapter);

        // Set up item removal listener
        adapter.setOnItemRemoveListener(new CartAdapter.OnItemRemoveListener() {
            @Override
            public void onItemRemoved(String menuId) {
                cartViewModel.removeItem(menuId);
            }
        });

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

        // Load cart from Firestore if user is logged in
        if (sessionManager.isLoggedIn()) {
            String userId = sessionManager.getUserId();
            cartViewModel.setUserId(userId);
            cartViewModel.loadCartFromFirestore(userId, new CartViewModel.OnCartLoadedListener() {
                @Override
                public void onCartLoaded(boolean success) {
                    if (!success) {
                        System.out.println("DEBUG: Cart loading completed with failure status");
                    } else {
                        System.out.println("DEBUG: Cart loading completed successfully");
                    }
                }
            });
        } else {
            // User not logged in, show empty cart
            cartViewModel.clearCart();
        }

        // Observe cart items
        cartViewModel.getCartItemsLiveData().observe(getViewLifecycleOwner(), cartItems -> {
            adapter.updateList(cartItems);
            totalPriceText.setText("Total: Rs." + String.format("%.2f", cartViewModel.getTotalPrice()));

            if (cartItems.isEmpty()) {
                tvEmptyMessage.setVisibility(View.VISIBLE);
                cartRecycler.setVisibility(View.GONE);
                btnCheckout.setEnabled(false);
            } else {
                tvEmptyMessage.setVisibility(View.GONE);
                cartRecycler.setVisibility(View.VISIBLE);
                btnCheckout.setEnabled(true);
            }
        });

        // Checkout click - Navigate to CheckoutFragment instead of processing order
        btnCheckout.setOnClickListener(v -> {
            navigateToCheckout();
        });

        return root;
    }

    private void navigateToCheckout() {
        List<CartItem> cartItems = cartViewModel.getCartItemsLiveData().getValue();
        if (cartItems == null || cartItems.isEmpty()) {
            Toast.makeText(getActivity(), "Cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        // Navigate to CheckoutFragment
        CheckoutFragment checkoutFragment = new CheckoutFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, checkoutFragment);
        transaction.addToBackStack(null); // Add to back stack so user can go back
        transaction.commit();
    }
}