// MyApplication.java
package com.nibm.pizzamaniamobileapp;

import android.app.Application;
import androidx.lifecycle.ViewModelProvider;
import com.nibm.pizzamaniamobileapp.utils.SessionManager;
import com.nibm.pizzamaniamobileapp.viewmodel.CartViewModel;

public class MyApplication extends Application {

    @Override
    public void onTerminate() {
        // Save cart when app is closing
        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            CartViewModel cartViewModel = new ViewModelProvider.AndroidViewModelFactory(this)
                    .create(CartViewModel.class);
            cartViewModel.saveCartToFirestore(sessionManager.getUserId());
        }
        super.onTerminate();
    }
}