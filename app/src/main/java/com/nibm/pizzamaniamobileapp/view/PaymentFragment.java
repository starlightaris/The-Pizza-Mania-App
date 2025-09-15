package com.nibm.pizzamaniamobileapp.view;

import static androidx.compose.ui.semantics.SemanticsPropertiesKt.dismiss;

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

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.nibm.pizzamaniamobileapp.R;
import com.nibm.pizzamaniamobileapp.adapter.PaymentAdapter;
import com.nibm.pizzamaniamobileapp.model.PaymentMethod;
import com.nibm.pizzamaniamobileapp.viewmodel.PaymentViewModel;

import java.util.ArrayList;
import java.util.List;

public class PaymentFragment extends BottomSheetDialogFragment {

    private RecyclerView recyclerPayments;
    private Button btnAddPayment;
    private PaymentAdapter paymentAdapter;
    private List<PaymentMethod> paymentList = new ArrayList<>();
    private PaymentViewModel paymentsViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment, container, false);

        recyclerPayments = view.findViewById(R.id.recyclerPayments);
        btnAddPayment = view.findViewById(R.id.btnAddPayment);

        recyclerPayments.setLayoutManager(new LinearLayoutManager(getContext()));

        paymentsViewModel = new ViewModelProvider(requireActivity()).get(PaymentViewModel.class);

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

        return view;
    }
}
