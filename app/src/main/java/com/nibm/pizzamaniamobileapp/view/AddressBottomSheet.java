package com.nibm.pizzamaniamobileapp.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.nibm.pizzamaniamobileapp.R;
import com.nibm.pizzamaniamobileapp.adapter.AddressAdapter;
import com.nibm.pizzamaniamobileapp.model.Address;
import com.nibm.pizzamaniamobileapp.viewmodel.CartViewModel;

import java.util.ArrayList;
import java.util.List;

public class AddressBottomSheet extends BottomSheetDialogFragment {

    private RecyclerView recyclerAddresses;
    private Button btnAddNewAddress;
    private AddressAdapter addressAdapter;
    private List<Address> addressList = new ArrayList<>();
    private CartViewModel cartViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottomsheet_address, container, false);

        recyclerAddresses = view.findViewById(R.id.recyclerAddresses);
        btnAddNewAddress = view.findViewById(R.id.btnAddNewAddress);

        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);

        // TODO: Load addresses from Firebase or ProfileViewModel
        // For now, using local list for testing
        addressList = getSavedAddresses();

        recyclerAddresses.setLayoutManager(new LinearLayoutManager(getContext()));
        addressAdapter = new AddressAdapter(addressList, new AddressAdapter.OnAddressClickListener() {
            @Override
            public void onAddressSelected(Address address) {
                cartViewModel.setSelectedAddress(address);
                dismiss(); // close bottom sheet
            }

            @Override
            public void onEditAddress(Address address) {
                AddressManagementDialog.newInstance(address)
                        .show(getChildFragmentManager(), "EditAddressDialog");
            }

            @Override
            public void onDeleteAddress(Address address) {
                // remove from list / Firebase
                addressList.remove(address);
                addressAdapter.notifyDataSetChanged();
            }
        });
        recyclerAddresses.setAdapter(addressAdapter);

        btnAddNewAddress.setOnClickListener(v -> {
            AddressManagementDialog dialog = new AddressManagementDialog();
            dialog.show(getChildFragmentManager(), "AddAddressDialog");
        });

        return view;
    }

    private List<Address> getSavedAddresses() {
        // Placeholder: in real app, load from Firebase
        return new ArrayList<>();
    }
}

