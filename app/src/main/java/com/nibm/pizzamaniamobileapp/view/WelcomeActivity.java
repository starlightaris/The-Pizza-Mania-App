package com.nibm.pizzamaniamobileapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.nibm.pizzamaniamobileapp.R;
import com.nibm.pizzamaniamobileapp.utils.SessionManager;
import com.nibm.pizzamaniamobileapp.viewmodel.CartViewModel;

public class WelcomeActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private CartViewModel cartViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sessionManager = new SessionManager(this);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);

        if (sessionManager.isLoggedIn()) {
            // Auto-login user, navigate to main screen
            String userId = sessionManager.getUserId();
            // Set user ID in your ViewModel
            cartViewModel.setUserId(userId);
            // Load cart from Firestore
            cartViewModel.loadCartFromFirestore(userId, new CartViewModel.OnCartLoadedListener() {
                @Override
                public void onCartLoaded(boolean success) {
                    // Cart loaded from Firestore, navigate to main activity
                    navigateToMainActivity();
                }
            });
        }
    }

    public void register(View view){
        startActivity(new Intent(WelcomeActivity.this, RegistrationActivity.class));
    }

    public void login(View view){
        startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
    }

    private void navigateToMainActivity() {
        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        finish();
    }
}