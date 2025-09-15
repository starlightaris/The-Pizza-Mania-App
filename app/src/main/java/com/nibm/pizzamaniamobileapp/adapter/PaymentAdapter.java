package com.nibm.pizzamaniamobileapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nibm.pizzamaniamobileapp.R;
import com.nibm.pizzamaniamobileapp.model.PaymentMethod;

import java.util.List;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder> {

    private final List<PaymentMethod> paymentList;
    private final OnPaymentClickListener listener;

    public PaymentAdapter(List<PaymentMethod> paymentList, OnPaymentClickListener listener) {
        this.paymentList = paymentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_payment, parent, false);
        return new PaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentViewHolder holder, int position) {
        PaymentMethod payment = paymentList.get(position);
        holder.bind(payment);
    }

    @Override
    public int getItemCount() {
        return paymentList.size();
    }

    class PaymentViewHolder extends RecyclerView.ViewHolder {
        TextView txtCardHolder, txtCardNumber, txtExpiry;

        public PaymentViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCardHolder = itemView.findViewById(R.id.txtCardHolder);
            txtCardNumber = itemView.findViewById(R.id.txtCardNumber);
            txtExpiry = itemView.findViewById(R.id.txtExpiry);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onSelectPayment(paymentList.get(pos));
                }
            });
        }

        void bind(PaymentMethod payment) {
            txtCardHolder.setText(payment.getCardHolderName());
            txtCardNumber.setText(payment.getMaskedCard());
            txtExpiry.setText(payment.getExpiryDate());
        }
    }

    public interface OnPaymentClickListener {
        void onSelectPayment(PaymentMethod payment);
        void onEditPayment(PaymentMethod payment);
        void onDeletePayment(PaymentMethod payment);
    }
}
