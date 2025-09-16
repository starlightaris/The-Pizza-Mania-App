package com.nibm.pizzamaniamobileapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nibm.pizzamaniamobileapp.R;
import com.nibm.pizzamaniamobileapp.model.MenuItem;
import com.nibm.pizzamaniamobileapp.viewmodel.CartViewModel;

import java.util.List;
import java.util.Locale;

public class HomeMenuAdapter extends RecyclerView.Adapter<HomeMenuAdapter.MenuViewHolder> {

    private final Context context;
    private final List<MenuItem> menuItems;
    private final CartViewModel cartViewModel;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(MenuItem item);
        void onAddClick(MenuItem item);
    }

    public HomeMenuAdapter(Context context, List<MenuItem> menuItems, CartViewModel cartViewModel, OnItemClickListener listener) {
        this.context = context;
        this.menuItems = menuItems;
        this.cartViewModel = cartViewModel;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_menu_item, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        MenuItem item = menuItems.get(position);

        holder.menuName.setText(item.getName());
        holder.menuDescription.setText(item.getDescription());
        holder.menuPrice.setText(String.format(Locale.getDefault(), "Rs.%.2f", item.getPrice()));

        // Load image
        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.icon_pizza)
                    .into(holder.menuImage);
        } else {
            holder.menuImage.setImageResource(R.drawable.icon_pizza);
        }

        // Set cart count visibility
        int count = cartViewModel.getItemCount(item.getMenuId());
        if (count > 0) {
            holder.tvCartCount.setText(String.valueOf(count));
            holder.tvCartCount.setVisibility(View.VISIBLE);
        } else {
            holder.tvCartCount.setVisibility(View.GONE);
        }

        // Click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });

        // In HomeMenuAdapter.java - update the add click listener:
        holder.btnAdd.setOnClickListener(v -> {
            String userId = cartViewModel.getUserId();
            System.out.println("DEBUG: Adding item with userId: " + userId);
            int updatedCount = cartViewModel.addItem(item);
            holder.tvCartCount.setText(String.valueOf(updatedCount));
            holder.tvCartCount.setVisibility(View.VISIBLE);
            if (listener != null) listener.onAddClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    public void updateList(List<MenuItem> newMenuItems) {
        menuItems.clear();
        menuItems.addAll(newMenuItems);
        notifyDataSetChanged();
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView menuName, menuDescription, menuPrice, tvCartCount;
        ImageView menuImage;
        Button btnAdd;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            menuName = itemView.findViewById(R.id.menu_name);
            menuDescription = itemView.findViewById(R.id.menu_description);
            menuPrice = itemView.findViewById(R.id.menu_price);
            menuImage = itemView.findViewById(R.id.menu_image);
            btnAdd = itemView.findViewById(R.id.btn_add_cart);
            tvCartCount = itemView.findViewById(R.id.tv_cart_count);
        }
    }
}
