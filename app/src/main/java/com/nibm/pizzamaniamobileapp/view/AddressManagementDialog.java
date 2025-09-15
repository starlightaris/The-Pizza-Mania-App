package com.nibm.pizzamaniamobileapp.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.nibm.pizzamaniamobileapp.R;
import com.nibm.pizzamaniamobileapp.model.Address;
import com.nibm.pizzamaniamobileapp.viewmodel.CartViewModel;

import java.io.Serializable;

public class AddressManagementDialog extends DialogFragment {
    private EditText editFullName, editPhone, editStreet, editCity, editPostal;
    private Button btnSave;
    private Address addressToEdit;
    private CartViewModel cartViewModel;

    public static AddressManagementDialog newInstance(Address address) {
        AddressManagementDialog dialog = new AddressManagementDialog();
        Bundle args = new Bundle();
        args.putSerializable("address", (Serializable) address);
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_address, container, false);

        editFullName = view.findViewById(R.id.editFullName);
        editPhone = view.findViewById(R.id.editPhone);
        editStreet = view.findViewById(R.id.editStreet);
        editCity = view.findViewById(R.id.editCity);
        editPostal = view.findViewById(R.id.editPostal);
        btnSave = view.findViewById(R.id.btnSaveAddress);

        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);

        // If editing existing address, pre-fill fields
        if (getArguments() != null && getArguments().containsKey("address")) {
            addressToEdit = (Address) getArguments().getSerializable("address");
            editFullName.setText(addressToEdit.getFullName());
            editPhone.setText(addressToEdit.getPhone());
            editStreet.setText(addressToEdit.getStreet());
            editCity.setText(addressToEdit.getCity());
            editPostal.setText(addressToEdit.getPostalCode());
        }

        btnSave.setOnClickListener(v -> {
            String fullName = editFullName.getText().toString().trim();
            String phone = editPhone.getText().toString().trim();
            String street = editStreet.getText().toString().trim();
            String city = editCity.getText().toString().trim();
            String postalCode = editPostal.getText().toString().trim();

            if (fullName.isEmpty() || phone.isEmpty() || street.isEmpty() || city.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Address address = addressToEdit != null ? addressToEdit : new Address();
            address.setFullName(fullName);
            address.setPhone(phone);
            address.setStreet(street);
            address.setCity(city);
            address.setPostalCode(postalCode);
            address.setDefault(true);

            // Update ViewModel
            cartViewModel.setSelectedAddress(address);

            dismiss();
            Toast.makeText(getContext(), "Address saved!", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
