package com.nibm.pizzamaniamobileapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nibm.pizzamaniamobileapp.R;
import com.nibm.pizzamaniamobileapp.model.CartItem;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems;

    public CartAdapter(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.name.setText(item.getName());
        holder.quantity.setText("Qty: " + item.getQuantity());
        holder.price.setText("Rs. " + String.format("%.2f", item.getPrice()));

        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.icon_pizza)
                    .into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.icon_pizza);
        }
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public void updateList(List<CartItem> newItems) {
        cartItems = newItems;
        notifyDataSetChanged();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, quantity, price;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.cart_item_image);
            name = itemView.findViewById(R.id.cart_item_name);
            quantity = itemView.findViewById(R.id.cart_item_quantity);
            price = itemView.findViewById(R.id.cart_item_price);
        }
    }
}
