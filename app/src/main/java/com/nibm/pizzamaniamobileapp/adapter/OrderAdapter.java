package com.nibm.pizzamaniamobileapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nibm.pizzamaniamobileapp.R;
import com.nibm.pizzamaniamobileapp.model.Order;
import com.nibm.pizzamaniamobileapp.model.OrderItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orders;

    public OrderAdapter(List<Order> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_card, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);

        holder.orderId.setText("Order ID: " + order.getOrderId());
        holder.userId.setText("Customer: " + order.getCustomerName());
        holder.deliveryAddress.setText("Delivery: " + order.getDeliveryAddress());
        holder.status.setText("Status: " + order.getStatus());
        holder.totalPrice.setText("Total: Rs." + String.format("%.2f", order.getTotalPrice()));

        if (order.getCreatedAt() != null) {
            Date date = order.getCreatedAt().toDate(); // Firestore Timestamp → Date
            String formattedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(date);
            holder.createdAt.setText("Created: " + formattedDate);
        }

        // Nested RecyclerView for order items
        List<OrderItem> items = order.getItems();
        OrderItemAdapter itemAdapter = new OrderItemAdapter(items);
        holder.itemsRecycler.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.itemsRecycler.setAdapter(itemAdapter);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public void updateList(List<Order> newOrders) {
        this.orders = newOrders;
        notifyDataSetChanged();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, userId, deliveryAddress, status, totalPrice, createdAt;
        RecyclerView itemsRecycler;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.order_id);
            userId = itemView.findViewById(R.id.order_user);
            deliveryAddress = itemView.findViewById(R.id.order_address);
            status = itemView.findViewById(R.id.order_status);
            totalPrice = itemView.findViewById(R.id.order_total);
            createdAt = itemView.findViewById(R.id.order_date);
            itemsRecycler = itemView.findViewById(R.id.order_items_recycler);
        }
    }
}
