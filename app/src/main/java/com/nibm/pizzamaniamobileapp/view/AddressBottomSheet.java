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
import com.nibm.pizzamaniamobileapp.viewmodel.ProfileViewModel;

import java.util.ArrayList;
import java.util.List;

public class AddressBottomSheet extends BottomSheetDialogFragment {

    private RecyclerView recyclerAddresses;
    private Button btnAddNewAddress;
    private AddressAdapter addressAdapter;
    private List<Address> addressList = new ArrayList<>();
    private CartViewModel cartViewModel;
    private ProfileViewModel profileViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottomsheet_address, container, false);

        recyclerAddresses = view.findViewById(R.id.recyclerAddresses);
        btnAddNewAddress = view.findViewById(R.id.btnAddNewAddress);

        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

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
                // ✅ Tell ViewModel to remove it, not just local list
                profileViewModel.deleteAddress(address);
            }
        });
        recyclerAddresses.setAdapter(addressAdapter);

        profileViewModel.getAddressListLiveData().observe(getViewLifecycleOwner(), addresses -> {
            addressList.clear();
            addressList.addAll(addresses);
            addressAdapter.notifyDataSetChanged();
        });

        btnAddNewAddress.setOnClickListener(v -> {
            AddressManagementDialog dialog = new AddressManagementDialog();
            dialog.show(getChildFragmentManager(), "AddAddressDialog");
        });

        return view;
    }
}


