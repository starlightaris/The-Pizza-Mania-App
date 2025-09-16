package com.nibm.pizzamaniamobileapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.nibm.pizzamaniamobileapp.R;
import com.nibm.pizzamaniamobileapp.model.Address;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {
    private List<Address> addressList;
    private OnAddressClickListener listener;

    public AddressAdapter(List<Address> addressList, OnAddressClickListener listener) {
        this.addressList = addressList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        Address address = addressList.get(position);
        holder.bind(address);
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public class AddressViewHolder extends RecyclerView.ViewHolder {
        TextView txtStreet, txtCity, txtPostal;
        ImageButton btnEdit, btnDelete;
        CardView cardView;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            txtStreet = itemView.findViewById(R.id.txtStreet);
            txtCity = itemView.findViewById(R.id.txtCity);
            txtPostal = itemView.findViewById(R.id.txtPostal);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            cardView = itemView.findViewById(R.id.cardAddress);

            // Click events
            cardView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onAddressSelected(addressList.get(pos));
                }
            });

            btnEdit.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onEditAddress(addressList.get(pos));
                }
            });

            btnDelete.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onDeleteAddress(addressList.get(pos));
                }
            });
        }

        public void bind(Address address) {
            txtStreet.setText(address.getStreet());
            txtCity.setText(address.getCity());
            txtPostal.setText(address.getPostalCode());
        }
    }

    // Listener interface
    public interface OnAddressClickListener {
        void onAddressSelected(Address address);
        void onEditAddress(Address address);
        void onDeleteAddress(Address address);
    }

    public void updateList(List<Address> newList) {
        addressList.clear();
        addressList.addAll(newList);
        notifyDataSetChanged();
    }
}
