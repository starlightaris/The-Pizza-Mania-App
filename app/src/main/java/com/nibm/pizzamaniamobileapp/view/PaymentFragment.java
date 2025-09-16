package com.nibm.pizzamaniamobileapp.view;

import static androidx.compose.ui.semantics.SemanticsPropertiesKt.dismiss;

import android.content.Intent;
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
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nibm.pizzamaniamobileapp.R;
import com.nibm.pizzamaniamobileapp.adapter.PaymentAdapter;
import com.nibm.pizzamaniamobileapp.model.CartItem;
import com.nibm.pizzamaniamobileapp.model.Order;
import com.nibm.pizzamaniamobileapp.model.OrderItem;
import com.nibm.pizzamaniamobileapp.model.PaymentMethod;
import com.nibm.pizzamaniamobileapp.viewmodel.CartViewModel;
import com.nibm.pizzamaniamobileapp.viewmodel.PaymentViewModel;

import java.util.ArrayList;
import java.util.List;

public class PaymentFragment extends BottomSheetDialogFragment {

    private RecyclerView recyclerPayments;
    private Button btnAddPayment;
    private Button btnConfirmOrder;
    private PaymentAdapter paymentAdapter;
    private List<PaymentMethod> paymentList = new ArrayList<>();
    private PaymentViewModel paymentsViewModel;
    private CartViewModel cartViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment, container, false);

        recyclerPayments = view.findViewById(R.id.recyclerPayments);
        btnAddPayment = view.findViewById(R.id.btnAddPayment);
        btnConfirmOrder = view.findViewById(R.id.btnConfirmOrder);

        recyclerPayments.setLayoutManager(new LinearLayoutManager(getContext()));

        paymentsViewModel = new ViewModelProvider(requireActivity()).get(PaymentViewModel.class);
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);

        paymentAdapter = new PaymentAdapter(paymentList, new PaymentAdapter.OnPaymentClickListener() {
            @Override
            public void onSelectPayment(PaymentMethod payment) {
                paymentsViewModel.selectPayment(payment);
                dismiss();
            }

            @Override
            public void onEditPayment(PaymentMethod payment) {
                PaymentManagementDialog.newInstance(payment).show(getChildFragmentManager(), "EditPaymentDialog");
            }

            @Override
            public void onDeletePayment(PaymentMethod payment) {
                paymentsViewModel.deletePayment(payment);
            }
        });

        recyclerPayments.setAdapter(paymentAdapter);

        paymentsViewModel.getPaymentListLiveData().observe(getViewLifecycleOwner(), payments -> {
            paymentList.clear();
            paymentList.addAll(payments);
            paymentAdapter.notifyDataSetChanged();
        });

        btnAddPayment.setOnClickListener(v -> {
            PaymentManagementDialog dialog = new PaymentManagementDialog();
            dialog.show(getChildFragmentManager(), "AddPaymentDialog");
        });

        btnConfirmOrder.setOnClickListener(v -> {
            processOrder();
        });

        return view;
    }

    private void processOrder() {
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
                                dismiss(); // Close the payment dialog

                                if (getActivity() != null) {
                                    // Go to MainActivity → open Delivery tab with orderId
                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    intent.putExtra("openDelivery", true);
                                    intent.putExtra("orderId", order.getOrderId());
                                    startActivity(intent);

                                    // Optional: finish current activity if you don’t want users to navigate back here
                                    getActivity().finish();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getActivity(), "Failed to place order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                });
    }
}