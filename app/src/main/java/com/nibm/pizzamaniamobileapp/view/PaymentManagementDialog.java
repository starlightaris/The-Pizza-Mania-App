package com.nibm.pizzamaniamobileapp.view;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.nibm.pizzamaniamobileapp.R;
import com.nibm.pizzamaniamobileapp.model.PaymentMethod;
import com.nibm.pizzamaniamobileapp.viewmodel.PaymentViewModel;

import java.util.UUID;

public class PaymentManagementDialog extends DialogFragment {

    private EditText edtCardHolder, edtCardNumber, edtExpiry, edtCVV;
    private Button btnSave;
    private PaymentViewModel paymentsViewModel;
    private PaymentMethod editingPayment; // optional

    public static PaymentManagementDialog newInstance(PaymentMethod payment) {
        PaymentManagementDialog dialog = new PaymentManagementDialog();
        dialog.editingPayment = payment;
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_payment_management, null);

        edtCardHolder = view.findViewById(R.id.edtCardHolder);
        edtCardNumber = view.findViewById(R.id.edtCardNumber);
        edtExpiry = view.findViewById(R.id.edtExpiryDate);
        edtCVV = view.findViewById(R.id.edtCVV);
        btnSave = view.findViewById(R.id.btnSavePayment);

        paymentsViewModel = new ViewModelProvider(requireActivity()).get(PaymentsViewModel.class);

        if (editingPayment != null) {
            edtCardHolder.setText(editingPayment.getCardHolderName());
            edtCardNumber.setText(editingPayment.getCardNumber());
            edtExpiry.setText(editingPayment.getExpiryDate());
        }

        btnSave.setOnClickListener(v -> {
            String name = edtCardHolder.getText().toString().trim();
            String number = edtCardNumber.getText().toString().trim();
            String expiry = edtExpiry.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(number) || TextUtils.isEmpty(expiry)) return;

            if (editingPayment != null) {
                editingPayment.setCardHolderName(name);
                editingPayment.setCardNumber(number);
                editingPayment.setExpiryDate(expiry);
                paymentsViewModel.updatePayment(editingPayment);
            } else {
                PaymentMethod newPayment = new PaymentMethod(UUID.randomUUID().toString(), name, number, expiry, false);
                paymentsViewModel.addPayment(newPayment);
            }
            dismiss();
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(view);
        return builder.create();
    }
}
