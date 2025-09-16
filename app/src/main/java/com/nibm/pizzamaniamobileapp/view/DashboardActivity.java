package com.nibm.pizzamaniamobileapp.view;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.nibm.pizzamaniamobileapp.R;
import com.nibm.pizzamaniamobileapp.utils.SessionManager;
import com.nibm.pizzamaniamobileapp.viewmodel.ProfileViewModel;

public class DashboardActivity extends AppCompatActivity {

    private Button btnManageBranches, btnManageMenu, btnViewOrders, btnLogout;
    private ProfileViewModel profileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        btnManageBranches = findViewById(R.id.btnManageBranches);
        btnManageMenu = findViewById(R.id.btnManageMenu);
        btnViewOrders = findViewById(R.id.btnViewOrders);
        btnLogout = findViewById(R.id.btnLogout);

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        btnManageBranches.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, BranchManagementActivity.class));
        });

        btnManageMenu.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, MenuManagementActivity.class));
        });

        btnViewOrders.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, OrderManagementActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            profileViewModel.logout(); // Firebase sign out
            SessionManager sessionManager = new SessionManager(DashboardActivity.this);
            sessionManager.logout(); // Clear local session

            // Go to login screen
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
