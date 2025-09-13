package com.nibm.pizzamaniamobileapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.nibm.pizzamaniamobileapp.R;
import com.nibm.pizzamaniamobileapp.adapters.BranchAdapter;
import com.nibm.pizzamaniamobileapp.model.Branch;
import com.nibm.pizzamaniamobileapp.repository.UserRepository;
import com.nibm.pizzamaniamobileapp.viewmodel.BranchViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class BranchManagementActivity extends AppCompatActivity {

    private BranchViewModel branchViewModel;
    private RecyclerView recyclerBranches;
    private BranchAdapter branchAdapter;
    private FloatingActionButton btnAddBranch;
    private UserRepository userRepository;
    private FrameLayout loadingOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch_management);

        recyclerBranches = findViewById(R.id.recyclerBranches);
        btnAddBranch = findViewById(R.id.btnAddBranch);
        loadingOverlay = findViewById(R.id.loadingOverlay);

        branchViewModel = new ViewModelProvider(this).get(BranchViewModel.class);
        userRepository = new UserRepository();

        showLoading(true);
        checkAdminRole();
    }

    private void checkAdminRole() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        MutableLiveData<String> roleLiveData = new MutableLiveData<>();
        userRepository.fetchUserRole(uid, roleLiveData);

        roleLiveData.observe(this, role -> {
            if (role == null) return;

            if (!role.equals("admin")) {
                Log.d("BranchActivity", "Role is not admin: " + role);
                Toast.makeText(this, "Access denied", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, WelcomeActivity.class));
                finish();
            } else {
                Log.d("BranchActivity", "Admin verified");
                showLoading(false);
                setupUI();
                observeBranches();
            }
        });
    }

    private void showLoading(boolean visible) {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(visible ? FrameLayout.VISIBLE : FrameLayout.GONE);
        }
    }

    private void setupUI() {
        recyclerBranches.setLayoutManager(new LinearLayoutManager(this));

        branchAdapter = new BranchAdapter(
                new ArrayList<>(),
                branch -> showEditDialog(branch), // click
                branch -> {}, // long click (optional, keep empty)
                branch -> showEditDialog(branch), // edit listener
                branch -> { // delete listener
                    new AlertDialog.Builder(this)
                            .setTitle("Delete Branch")
                            .setMessage("Are you sure you want to delete \"" + branch.getName() + "\"?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                branchViewModel.deleteBranch(branch.getBranchId());
                                Toast.makeText(this, "Branch deleted", Toast.LENGTH_SHORT).show();
                            })
                            .setNegativeButton("No", null)
                            .show();
                }
        );

        recyclerBranches.setAdapter(branchAdapter);

        btnAddBranch.setOnClickListener(v -> showAddDialog());
    }

    private void observeBranches() {
        branchViewModel.getBranches().observe(this, snapshot -> {
            List<Branch> branches = new ArrayList<>();
            for (QueryDocumentSnapshot doc : snapshot) {
                Branch b = doc.toObject(Branch.class);
                branches.add(b);
            }
            branchAdapter.updateData(branches);
        });
    }

    private void showAddDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_branch, null);

        EditText inputName = dialogView.findViewById(R.id.inputBranchName);
        EditText inputLat = dialogView.findViewById(R.id.inputBranchLat);
        EditText inputLng = dialogView.findViewById(R.id.inputBranchLng);
        EditText inputContact = dialogView.findViewById(R.id.inputBranchContact);

        new AlertDialog.Builder(this)
                .setTitle("Add Branch")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String name = inputName.getText().toString().trim();
                    double lat = Double.parseDouble(inputLat.getText().toString().trim());
                    double lng = Double.parseDouble(inputLng.getText().toString().trim());
                    String contact = inputContact.getText().toString().trim();

                    Branch newBranch = new Branch(null,
                            name,
                            new com.google.firebase.firestore.GeoPoint(lat, lng),
                            contact);
                    branchViewModel.addBranch(newBranch);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEditDialog(Branch branch) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_branch, null);

        EditText inputName = dialogView.findViewById(R.id.inputBranchName);
        EditText inputLat = dialogView.findViewById(R.id.inputBranchLat);
        EditText inputLng = dialogView.findViewById(R.id.inputBranchLng);
        EditText inputContact = dialogView.findViewById(R.id.inputBranchContact);

        inputName.setText(branch.getName());
        if (branch.getLocation() != null) {
            inputLat.setText(String.valueOf(branch.getLocation().getLatitude()));
            inputLng.setText(String.valueOf(branch.getLocation().getLongitude()));
        }
        inputContact.setText(branch.getContact());

        new AlertDialog.Builder(this)
                .setTitle("Edit Branch")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = inputName.getText().toString().trim();
                    double lat = Double.parseDouble(inputLat.getText().toString().trim());
                    double lng = Double.parseDouble(inputLng.getText().toString().trim());
                    String contact = inputContact.getText().toString().trim();

                    branch.setName(name);
                    branch.setLocation(new com.google.firebase.firestore.GeoPoint(lat, lng));
                    branch.setContact(contact);

                    branchViewModel.updateBranch(branch);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}