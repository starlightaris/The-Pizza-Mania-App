package com.nibm.pizzamaniamobileapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.nibm.pizzamaniamobileapp.R;
import com.nibm.pizzamaniamobileapp.viewmodel.UserViewModel;

public class RegistrationActivity extends AppCompatActivity {
    private EditText nameInput, emailInput, phoneInput, passwordInput;
    private Button registerBtn;
    private TextView loginLink;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        nameInput = findViewById(R.id.txtName);
        emailInput = findViewById(R.id.txtEmail);
        phoneInput = findViewById(R.id.txtPhone);
        passwordInput = findViewById(R.id.txtPass);
        registerBtn = findViewById(R.id.btnRegister);
        loginLink = findViewById(R.id.btnLogin);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        registerBtn.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String phone = phoneInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            userViewModel.register(name, email, phone, password);
        });
        loginLink.setOnClickListener(v -> {
            startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
        });
        userViewModel.getLoginSuccess().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Registered successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });
        userViewModel.getErrorMessage().observe(this, msg ->
                Toast.makeText(this, "Error: " + msg, Toast.LENGTH_SHORT).show());
    }
}