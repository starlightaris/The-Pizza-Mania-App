package com.nibm.pizzamaniamobileapp.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.nibm.pizzamaniamobileapp.R;

public class AddressManagementDialog extends DialogFragment {
    private EditText editFullName, editPhone, editStreet, editCity, editPostal;
    private Button btnSave;

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

        btnSave.setOnClickListener(v -> {
            // Later: build an Address object & save to Firebase
            dismiss();
        });

        return view;
    }
}
