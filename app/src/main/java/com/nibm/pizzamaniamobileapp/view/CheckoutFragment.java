package com.nibm.pizzamaniamobileapp.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.nibm.pizzamaniamobileapp.R;
import com.nibm.pizzamaniamobileapp.adapter.CartAdapter;
import com.nibm.pizzamaniamobileapp.viewmodel.CartViewModel;
import com.nibm.pizzamaniamobileapp.viewmodel.PaymentViewModel;

public class CheckoutFragment extends Fragment {

    private RecyclerView recyclerCartItems;
    private TextView txtDeliveryAddress, txtTotalPrice, txtSelectedPayment;
    private Button btnChangeAddress, btnProceedPayment;
    private CartViewModel cartViewModel;
    private PaymentViewModel paymentViewModel;
    private CartAdapter cartAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checkout, container, false);

        recyclerCartItems = view.findViewById(R.id.recyclerCartItems);
        txtDeliveryAddress = view.findViewById(R.id.txtDeliveryAddress);
        txtTotalPrice = view.findViewById(R.id.txtTotalPrice);
        txtSelectedPayment = view.findViewById(R.id.txtSelectedPayment);
        btnChangeAddress = view.findViewById(R.id.btnChangeAddress);
        btnProceedPayment = view.findViewById(R.id.btnProceedPayment);

        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        paymentViewModel = new ViewModelProvider(requireActivity()).get(PaymentViewModel.class);

        recyclerCartItems.setLayoutManager(new LinearLayoutManager(getContext()));
        cartAdapter = new CartAdapter(cartViewModel.getCartItemsLiveData().getValue());
        recyclerCartItems.setAdapter(cartAdapter);

        // Observe cart items
        cartViewModel.getCartItemsLiveData().observe(getViewLifecycleOwner(), items -> {
            cartAdapter.updateList(items);
            txtTotalPrice.setText("Total: $" + cartViewModel.getTotalPrice());
        });

        // Observe selected payment
        paymentViewModel.getSelectedPaymentMethod().observe(getViewLifecycleOwner(), payment -> {
            if (payment != null) {
                cartViewModel.setSelectedPayment(payment);
                txtSelectedPayment.setText(payment.getMaskedCard());
            }
        });
        // Click to open PaymentsFragment or dialog
        txtSelectedPayment.setOnClickListener(v -> {
            PaymentFragment paymentFragment = new PaymentFragment();
            paymentFragment.show(getParentFragmentManager(), "SelectPayment");
        });


        // Delivery address
        cartViewModel.getSelectedAddress().observe(getViewLifecycleOwner(), address -> {
            if (address != null) {
                txtDeliveryAddress.setText(address.getFullAddress());
            } else {
                txtDeliveryAddress.setText("No address selected");
            }
        });


        btnChangeAddress.setOnClickListener(v -> {
            AddressBottomSheet bottomSheet = new AddressBottomSheet();
            bottomSheet.show(getChildFragmentManager(), "AddressBottomSheet");
        });

        btnProceedPayment.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.frameLayout, new PaymentFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
    }