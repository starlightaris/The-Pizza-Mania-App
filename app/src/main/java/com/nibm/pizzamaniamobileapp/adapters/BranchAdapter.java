package com.nibm.pizzamaniamobileapp.adapters;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.GeoPoint;
import com.nibm.pizzamaniamobileapp.R;
import com.nibm.pizzamaniamobileapp.model.Branch;

import java.util.List;

public class BranchAdapter extends RecyclerView.Adapter<BranchAdapter.BranchViewHolder> {

    public interface OnBranchClickListener {
        void onClick(Branch branch);
    }
    public interface OnBranchLongClickListener {
        void onLongClick(Branch branch);
    }
    public interface OnBranchEditListener {
        void onEdit(Branch branch);
    }
    public interface OnBranchDeleteListener {
        void onDelete(Branch branch);
    }
    private List<Branch> branchList;
    private final OnBranchClickListener clickListener;
    private final OnBranchLongClickListener longClickListener;
    private final OnBranchEditListener editListener;
    private final OnBranchDeleteListener deleteListener;
    public BranchAdapter(List<Branch> branchList,
                         OnBranchClickListener clickListener,
                         OnBranchLongClickListener longClickListener,
                         OnBranchEditListener editListener,
                         OnBranchDeleteListener deleteListener) {
        this.branchList = branchList;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    public void updateData(List<Branch> newList) {
        this.branchList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BranchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_branch, parent, false);
        return new BranchViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BranchViewHolder holder, int position) {
        Branch branch = branchList.get(position);

        holder.txtName.setText(branch.getName());
        if (branch.getLocation() != null) {
            holder.txtLocation.setText(branch.getLocation().getLatitude() + ", " + branch.getLocation().getLongitude());
        } else {
            holder.txtLocation.setText("No location");
        }
        holder.txtContact.setText(branch.getContact());

        // Edit button
        holder.btnEdit.setOnClickListener(v -> editListener.onEdit(branch));

        // Delete button (with confirmation)
        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Delete Branch")
                    .setMessage("Are you sure you want to delete \"" + branch.getName() + "\"?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        deleteListener.onDelete(branch);
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }
    @Override
    public int getItemCount() {
        return branchList.size();
    }
    static class BranchViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtLocation, txtContact;
        ImageView imgLocation;
        Button btnEdit, btnDelete;
        public BranchViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtBranchName);
            txtLocation = itemView.findViewById(R.id.txtBranchLocation);
            txtContact = itemView.findViewById(R.id.txtBranchContact);
            imgLocation = itemView.findViewById(R.id.imgLocation);
            btnEdit = itemView.findViewById(R.id.btnEditBranch);
            btnDelete = itemView.findViewById(R.id.btnDeleteBranch);
        }
    }
}
