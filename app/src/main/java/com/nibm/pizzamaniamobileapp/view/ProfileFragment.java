package com.nibm.pizzamaniamobileapp.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.nibm.pizzamaniamobileapp.R;
import com.nibm.pizzamaniamobileapp.adapter.AddressAdapter;
import com.nibm.pizzamaniamobileapp.model.Address;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
    private RecyclerView recyclerAddresses;
    private Button btnAddAddress;
    private AddressAdapter addressAdapter;
    private List<Address> addressList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        recyclerAddresses = view.findViewById(R.id.recyclerAddresses);
        btnAddAddress = view.findViewById(R.id.btnAddAddress);

        recyclerAddresses.setLayoutManager(new LinearLayoutManager(getContext()));
        addressAdapter = new AddressAdapter(addressList);
        recyclerAddresses.setAdapter(addressAdapter);

        btnAddAddress.setOnClickListener(v -> {
            AddAddressDialog dialog = new AddAddressDialog();
            dialog.show(getChildFragmentManager(), "AddAddressDialog");
        });

        // TODO: load addresses from Firebase later
        return view;
    }
}