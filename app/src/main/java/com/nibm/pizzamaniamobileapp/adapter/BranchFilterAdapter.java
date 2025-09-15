package com.nibm.pizzamaniamobileapp.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.nibm.pizzamaniamobileapp.R;

import java.util.List;

public class BranchFilterAdapter extends RecyclerView.Adapter<BranchFilterAdapter.BranchViewHolder> {

    private final List<String> branchList;
    private final BranchClickListener listener;
    private int selectedPosition = RecyclerView.NO_POSITION; // To highlight selection

    // Callback interface
    public interface BranchClickListener {
        void onBranchClick(String branchName);
    }

    public BranchFilterAdapter(List<String> branchList, BranchClickListener listener) {
        this.branchList = branchList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BranchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_branch_filter, parent, false);
        return new BranchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BranchViewHolder holder, int position) {
        String branchName = branchList.get(position);
        holder.branchName.setText(branchName);

        // Highlight selected branch
        if (selectedPosition == position) {
            holder.cardView.setCardBackgroundColor(holder.itemView.getContext().getColor(R.color.purple_500));
            holder.branchName.setTextColor(Color.WHITE);
        } else {
            holder.cardView.setCardBackgroundColor(holder.itemView.getContext().getColor(R.color.grey));
            holder.branchName.setTextColor(Color.BLACK);
        }

        // Click listener
        holder.itemView.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();

            // Update UI
            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);

            // Callback
            listener.onBranchClick(branchName);
        });
    }

    @Override
    public int getItemCount() {
        return branchList.size();
    }

    static class BranchViewHolder extends RecyclerView.ViewHolder {
        TextView branchName;
        CardView cardView;

        public BranchViewHolder(@NonNull View itemView) {
            super(itemView);
            branchName = itemView.findViewById(R.id.branchNameText);
            cardView = itemView.findViewById(R.id.branchCardView);
        }
    }
}
