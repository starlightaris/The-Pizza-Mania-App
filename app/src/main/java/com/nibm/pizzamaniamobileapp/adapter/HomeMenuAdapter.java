package com.nibm.pizzamaniamobileapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nibm.pizzamaniamobileapp.R;
import com.nibm.pizzamaniamobileapp.model.MenuItem;

import java.util.List;
import java.util.Locale;

public class HomeMenuAdapter extends RecyclerView.Adapter<HomeMenuAdapter.MenuViewHolder> {

    private final Context context;
    private final List<MenuItem> menuItems;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(MenuItem item);
    }

    public HomeMenuAdapter(Context context, List<MenuItem> menuItems, OnItemClickListener listener) {
        this.context = context;
        this.menuItems = menuItems;
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

        // Format price as Rs.XXXX.00
        String formattedPrice = String.format(Locale.getDefault(), "Rs.%.2f", item.getPrice());
        holder.menuPrice.setText(formattedPrice);

        // Load image
        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.icon_pizza)
                    .into(holder.menuImage);
        } else {
            holder.menuImage.setImageResource(R.drawable.icon_pizza);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
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
        TextView menuName, menuDescription, menuPrice;
        ImageView menuImage;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            menuName = itemView.findViewById(R.id.menu_name);
            menuDescription = itemView.findViewById(R.id.menu_description);
            menuPrice = itemView.findViewById(R.id.menu_price);
            menuImage = itemView.findViewById(R.id.menu_image);
        }
    }
}
