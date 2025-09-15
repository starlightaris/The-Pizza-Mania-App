package com.nibm.pizzamaniamobileapp.view;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nibm.pizzamaniamobileapp.R;
import com.nibm.pizzamaniamobileapp.viewmodel.CartViewModel;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigatinView;
    private FrameLayout frameLayout;
    private CartViewModel cartViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigatinView = findViewById(R.id.bottomNavView);
        frameLayout = findViewById(R.id.frameLayout);

        // --- Get CartViewModel ---
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);

        // --- Fetch logged-in user info from Firebase Auth ---
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            cartViewModel.setUserId(uid);

            // No need to store name/address in ViewModel now
            FirebaseFirestore.getInstance().collection("users").document(uid)
                    .get();
        }

        // Example: set default branch (replace if branch selection exists in HomeFragment)
        cartViewModel.setSelectedBranchId("branch_001");

        bottomNavigatinView.setOnNavigationItemSelectedListener(menuItem -> {
            int itemId = menuItem.getItemId();

            if (itemId == R.id.navHome) {
                loadFragment(new HomeFragment(), false);
            } else if (itemId == R.id.navCart) {
                loadFragment(new CartFragment(), false);
            } else if (itemId == R.id.navActivity) {
                loadFragment(new DeliveryFragment(), false);
            } else { // navProfile
                loadFragment(new ProfileFragment(), false);
            }

            return true;
        });

        loadFragment(new HomeFragment(), true);
    }

    private void loadFragment(Fragment fragment, boolean isAppInitialized) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (isAppInitialized) {
            fragmentTransaction.add(R.id.frameLayout, fragment);
        } else {
            fragmentTransaction.replace(R.id.frameLayout, fragment);
        }
        fragmentTransaction.commit();
    }
}
