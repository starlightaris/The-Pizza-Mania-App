package com.nibm.pizzamaniamobileapp.view;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.nibm.pizzamaniamobileapp.R;
import com.nibm.pizzamaniamobileapp.viewmodel.ProfileViewModel;

public class ProfileManagementDialog extends DialogFragment {

    private EditText edtFullName, edtEmail, edtPhone;
    private Button btnSave;
    private ProfileViewModel profileViewModel;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_edit_profile, null);

        edtFullName = view.findViewById(R.id.edtFullName);
        edtEmail = view.findViewById(R.id.edtEmail);
        edtPhone = view.findViewById(R.id.edtPhone);
        btnSave = view.findViewById(R.id.btnSaveProfile);

        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        // Pre-fill with existing data
        profileViewModel.getUserName().observe(this, edtFullName::setText);
        profileViewModel.getEmail().observe(this, edtEmail::setText);
        profileViewModel.getPhone().observe(this, edtPhone::setText);

        btnSave.setOnClickListener(v -> {
            String name = edtFullName.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();

            profileViewModel.updateUser(name, email, phone);
            dismiss();
        });

        return new AlertDialog.Builder(requireContext())
                .setView(view)
                .setTitle("Edit Profile")
                .create();
    }
}

