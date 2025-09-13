package com.nibm.pizzamaniamobileapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.nibm.pizzamaniamobileapp.R;

public class DashboardActivity extends AppCompatActivity {

    private Button btnManageBranches, btnManageMenu, btnViewOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        btnManageBranches = findViewById(R.id.btnManageBranches);
        btnManageMenu = findViewById(R.id.btnManageMenu);
        btnViewOrders = findViewById(R.id.btnViewOrders);

        btnManageBranches.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, BranchManagementActivity.class));
        });

        btnManageMenu.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, MenuManagementActivity.class));
        });

        btnViewOrders.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, OrderManagementActivity.class));
        });
    }
}
