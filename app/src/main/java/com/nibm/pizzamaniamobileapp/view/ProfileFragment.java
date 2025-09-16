package com.nibm.pizzamaniamobileapp.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.nibm.pizzamaniamobileapp.R;
import com.nibm.pizzamaniamobileapp.adapter.AddressAdapter;
import com.nibm.pizzamaniamobileapp.model.Address;
import com.nibm.pizzamaniamobileapp.viewmodel.ProfileViewModel;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
    private TextView txtUserName, txtEmail;
    private RecyclerView recyclerAddresses;
    private Button btnAddAddress;
    private Button btnEditProfile;
    private Button btnLogout, btnDeleteAccount;
    private AddressAdapter addressAdapter;
    private List<Address> addressList = new ArrayList<>();
    private ProfileViewModel profileViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //Bind views
        txtUserName = view.findViewById(R.id.txtUserName);
        txtEmail = view.findViewById(R.id.txtEmail);
        recyclerAddresses = view.findViewById(R.id.recyclerAddresses);
        btnAddAddress = view.findViewById(R.id.btnAddAddress);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);

        btnLogout = view.findViewById(R.id.btnLogout);
        btnDeleteAccount = view.findViewById(R.id.btnDeleteAccount);

        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        profileViewModel.getUserName().observe(getViewLifecycleOwner(), name -> txtUserName.setText(name));
        profileViewModel.getEmail().observe(getViewLifecycleOwner(), email -> txtEmail.setText(email));

        recyclerAddresses.setLayoutManager(new LinearLayoutManager(getContext()));
        addressAdapter = new AddressAdapter(addressList, new AddressAdapter.OnAddressClickListener() {
            @Override
            public void onAddressSelected(Address address) {
                // Optional: maybe set as default
            }
            @Override
            public void onEditAddress(Address address) {
                AddressManagementDialog.newInstance(address)
                        .show(getChildFragmentManager(), "EditAddressDialog");
            }
            @Override
            public void onDeleteAddress(Address address) {
                profileViewModel.deleteAddress(address);
            }
        });
        recyclerAddresses.setAdapter(addressAdapter);

        profileViewModel.getAddressListLiveData().observe(getViewLifecycleOwner(), addresses -> {
            addressList.clear();
            addressList.addAll(addresses);
            addressAdapter.notifyDataSetChanged();
        });

        btnAddAddress.setOnClickListener(v -> {
            AddressManagementDialog dialog = new AddressManagementDialog();
            dialog.show(getChildFragmentManager(), "AddAddressDialog");
        });

        btnEditProfile.setOnClickListener(v -> {
            ProfileManagementDialog dialog = new ProfileManagementDialog();
            dialog.show(getChildFragmentManager(), "ProfileManagementDialog");
        });

        btnLogout.setOnClickListener(v -> {
            profileViewModel.logout();
            requireActivity().finish(); // go back to login screen
        });

        btnDeleteAccount.setOnClickListener(v -> {
            profileViewModel.deleteAccount(success -> {
                if (success) {
                    requireActivity().finish();
                }
            });
        });
        return view;
    }
}