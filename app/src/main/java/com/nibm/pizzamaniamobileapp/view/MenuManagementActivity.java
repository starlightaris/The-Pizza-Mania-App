package com.nibm.pizzamaniamobileapp.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nibm.pizzamaniamobileapp.R;
import com.nibm.pizzamaniamobileapp.adapter.MenuAdapter;
import com.nibm.pizzamaniamobileapp.model.MenuItem;
import com.nibm.pizzamaniamobileapp.repository.BranchRepository;
import com.nibm.pizzamaniamobileapp.viewmodel.MenuViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MenuManagementActivity extends AppCompatActivity {

    private MenuViewModel menuViewModel;
    private RecyclerView recyclerView;
    private MenuAdapter adapter;
    private Spinner branchSpinner;
    private Button btnAddMenu;

    private final MutableLiveData<List<Map<String, String>>> branchesLiveData = new MutableLiveData<>();
    private final BranchRepository branchRepository = new BranchRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_management);

        branchSpinner = findViewById(R.id.branchSpinner);
        recyclerView = findViewById(R.id.menuRecyclerView);
        btnAddMenu = findViewById(R.id.btnAddMenu);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MenuAdapter(this);
        recyclerView.setAdapter(adapter);

        menuViewModel = new ViewModelProvider(this).get(MenuViewModel.class);

        // Observe menu items
        menuViewModel.getMenuItemsLiveData().observe(this, items -> adapter.setMenuItems(items));

        // Adapter buttons
        adapter.setOnItemActionListener(new MenuAdapter.OnItemActionListener() {
            @Override
            public void onEdit(MenuItem item) {
                showMenuDialog(item); // Open edit dialog
            }

            @Override
            public void onDelete(MenuItem item) {
                menuViewModel.deleteMenuItem(item.getMenuId());
            }
        });

        // Fetch branches for dropdown
        branchRepository.getAllBranchesForDropdown(branchesLiveData);

        branchesLiveData.observe(this, branches -> {
            List<String> branchNames = new ArrayList<>();
            for (Map<String, String> branch : branches) branchNames.add(branch.get("name"));

            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, branchNames);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            branchSpinner.setAdapter(spinnerAdapter);

            branchSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                    String branchId = branches.get(position).get("branchId");
                    menuViewModel.setBranchId(branchId);
                    menuViewModel.fetchMenuItems();
                }

                @Override
                public void onNothingSelected(android.widget.AdapterView<?> parent) {}
            });
        });

        // Add Menu Item button
        btnAddMenu.setOnClickListener(v -> showMenuDialog(null));
    }

    private void showMenuDialog(MenuItem existingItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_menu_item, null);
        builder.setView(dialogView);

        EditText etName = dialogView.findViewById(R.id.etMenuName);
        EditText etPrice = dialogView.findViewById(R.id.etMenuPrice);
        EditText etCategory = dialogView.findViewById(R.id.etMenuCategory);
        EditText etImageUrl = dialogView.findViewById(R.id.etMenuImageUrl);
        EditText etDescription = dialogView.findViewById(R.id.etMenuDescription);
        Button btnSave = dialogView.findViewById(R.id.btnSaveMenu);
        Button btnDelete = dialogView.findViewById(R.id.btnDeleteMenu);

        if (existingItem != null) {
            etName.setText(existingItem.getName());
            etPrice.setText(String.valueOf(existingItem.getPrice()));
            etCategory.setText(existingItem.getCategory());
            etImageUrl.setText(existingItem.getImageUrl());
            etDescription.setText(existingItem.getDescription());
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            btnDelete.setVisibility(View.GONE);
        }

        AlertDialog dialog = builder.create();

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String category = etCategory.getText().toString().trim();
            String imageUrl = etImageUrl.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            double price;
            try {
                price = Double.parseDouble(etPrice.getText().toString().trim());
            } catch (NumberFormatException e) {
                price = 0.0;
            }

            if (existingItem != null) {
                // Update existing
                existingItem.setName(name);
                existingItem.setDescription(description);
                existingItem.setPrice(price);
                existingItem.setCategory(category);
                existingItem.setImageUrl(imageUrl);
                menuViewModel.updateMenuItem(existingItem);
            } else {
                // Add new
                MenuItem newItem = new MenuItem();
                newItem.setName(name);
                newItem.setDescription(description);
                newItem.setPrice(price);
                newItem.setCategory(category);
                newItem.setImageUrl(imageUrl);
                newItem.setAvailable(true);
                menuViewModel.addMenuItem(newItem);
            }
            dialog.dismiss();
        });

        btnDelete.setOnClickListener(v -> {
            if (existingItem != null) {
                menuViewModel.deleteMenuItem(existingItem.getMenuId());
            }
            dialog.dismiss();
        });

        dialog.show();
    }
}
