package com.nibm.pizzamaniamobileapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nibm.pizzamaniamobileapp.model.PaymentMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PaymentViewModel extends ViewModel {
    private final MutableLiveData<List<PaymentMethod>> paymentListLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<PaymentMethod> selectedPaymentMethod = new MutableLiveData<>();

    public LiveData<List<PaymentMethod>> getPaymentListLiveData() { return paymentListLiveData; }
    public LiveData<PaymentMethod> getSelectedPaymentMethod() { return selectedPaymentMethod; }

    // Add new payment
    public void addPayment(PaymentMethod payment) {
        List<PaymentMethod> current = paymentListLiveData.getValue();
        if (current == null) current = new ArrayList<>();
        if (payment.getPaymentId() == null || payment.getPaymentId().isEmpty())
            payment.setPaymentId(UUID.randomUUID().toString());
        current.add(payment);
        paymentListLiveData.setValue(current);
    }

    // Update existing payment
    public void updatePayment(PaymentMethod updated) {
        List<PaymentMethod> current = paymentListLiveData.getValue();
        if (current == null) return;
        for (int i = 0; i < current.size(); i++) {
            if (current.get(i).getPaymentId().equals(updated.getPaymentId())) {
                current.set(i, updated);
                break;
            }
        }
        paymentListLiveData.setValue(current);
    }

    // Delete payment
    public void deletePayment(PaymentMethod toDelete) {
        List<PaymentMethod> current = paymentListLiveData.getValue();
        if (current == null) return;
        current.removeIf(p -> p.getPaymentId().equals(toDelete.getPaymentId()));
        paymentListLiveData.setValue(current);
    }

    // Set selected payment
    public void selectPayment(PaymentMethod payment) {
        selectedPaymentMethod.setValue(payment);
    }
}
