package com.nibm.pizzamaniamobileapp.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.nibm.pizzamaniamobileapp.R;
import com.nibm.pizzamaniamobileapp.model.Address;
import com.nibm.pizzamaniamobileapp.viewmodel.CartViewModel;

import java.io.Serializable;

public class AddressManagementDialog extends DialogFragment {
    private EditText editStreet, editCity, editPostal;
    private Button btnSave;
    private Address addressToEdit;
    private CartViewModel cartViewModel;
    private FusedLocationProviderClient fusedLocationClient;

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

        editStreet = view.findViewById(R.id.editStreet);
        editCity = view.findViewById(R.id.editCity);
        editPostal = view.findViewById(R.id.editPostal);
        btnSave = view.findViewById(R.id.btnSaveAddress);

        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        // If editing existing address, pre-fill fields
        if (getArguments() != null && getArguments().containsKey("address")) {
            addressToEdit = (Address) getArguments().getSerializable("address");
            editStreet.setText(addressToEdit.getStreet());
            editCity.setText(addressToEdit.getCity());
            editPostal.setText(addressToEdit.getPostalCode());
        }

        btnSave.setOnClickListener(v -> requestLocationAndSave());

        return view;
    }

    private void requestLocationAndSave() {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
        } else {
            saveWithLocation();
        }
    }

    private void saveWithLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());

                Address address = addressToEdit != null ? addressToEdit : new Address();
                address.setStreet(editStreet.getText().toString().trim());
                address.setCity(editCity.getText().toString().trim());
                address.setPostalCode(editPostal.getText().toString().trim());
                address.setGeoPoint(geoPoint);
                address.setDefault(true);

                // ✅ Save locally in ViewModel
                cartViewModel.setSelectedAddress(address);

                // ✅ ALSO save to Firestore
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(userId)
                        .collection("addresses")
                        .add(address)
                        .addOnSuccessListener(docRef -> {
                            Toast.makeText(getContext(), "Address saved to Firestore!", Toast.LENGTH_SHORT).show();
                            dismiss();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Failed to save: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });

            } else {
                Toast.makeText(getContext(), "Could not get location", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001 && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            saveWithLocation();
        } else {
            Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
        }
    }
}
