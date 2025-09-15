package com.nibm.pizzamaniamobileapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nibm.pizzamaniamobileapp.R;
import com.nibm.pizzamaniamobileapp.model.HomeHoriModel;

import java.util.List;

public class HomeHoriAdapter extends RecyclerView.Adapter<HomeHoriAdapter.ViewHolder> {

    Context context;
    List<HomeHoriModel> list;

    public HomeHoriAdapter(Context context, List<HomeHoriModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.home_horizontal_category, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.imageView.setImageResource(list.get(position).getImage());
        holder.name.setText(list.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.hori_pizza_img);
            name = itemView.findViewById(R.id.hori_pizza_txt);
        }
    }
}
