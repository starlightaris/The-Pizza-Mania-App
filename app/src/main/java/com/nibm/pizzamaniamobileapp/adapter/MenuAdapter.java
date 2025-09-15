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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private final Context context;
    private List<MenuItem> menuItems = new ArrayList<>();
    private OnItemActionListener onItemActionListener;
    public MenuAdapter(Context context) {
        this.context = context;
    }
    public void setMenuItems(List<MenuItem> items) {
        this.menuItems = items;
        notifyDataSetChanged();
    }
    public void setOnItemActionListener(OnItemActionListener listener) {
        this.onItemActionListener = listener;
    }
    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_menu, parent, false);
        return new MenuViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        MenuItem item = menuItems.get(position);
        holder.name.setText(item.getName());
        holder.price.setText("Rs." + item.getPrice());
        holder.category.setText(item.getCategory());
        holder.availability.setText(item.isAvailable() ? "Available" : "Not Available");
        holder.description.setText(item.getDescription());

        Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.drawable.image_placeholder)
                .into(holder.image);

        // Handle edit
        holder.btnEditMenu.setOnClickListener(v -> {
            if (onItemActionListener != null) onItemActionListener.onEdit(item);
        });

        // Handle delete
        holder.btnDeleteMenu.setOnClickListener(v -> {
            if (onItemActionListener != null) onItemActionListener.onDelete(item);
        });
    }
    @Override
    public int getItemCount() {
        return menuItems.size();
    }
    static class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, category, availability, description;
        ImageView image;
        Button btnEditMenu, btnDeleteMenu;
        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.menuName);
            price = itemView.findViewById(R.id.menuPrice);
            category = itemView.findViewById(R.id.menuCategory);
            availability = itemView.findViewById(R.id.menuAvailability);
            description = itemView.findViewById(R.id.menuDescription);
            image = itemView.findViewById(R.id.menuImage);
            btnEditMenu = itemView.findViewById(R.id.btnEditMenu);
            btnDeleteMenu = itemView.findViewById(R.id.btnDeleteMenu);
        }
    }
    public interface OnItemActionListener {
        void onEdit(MenuItem item);
        void onDelete(MenuItem item);
    }
}
