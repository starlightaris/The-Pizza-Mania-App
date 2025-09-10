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

public class LoginActivity extends AppCompatActivity {
    private EditText emailInput, passwordInput;
    private Button loginBtn;
    private TextView signUpLink;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        emailInput = findViewById(R.id.txtEmail);
        passwordInput = findViewById(R.id.txtPass);
        loginBtn = findViewById(R.id.btnLogin);
        signUpLink = findViewById(R.id.btnRegister);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        loginBtn.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            userViewModel.login(email, password);
        });
        signUpLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
        });
        userViewModel.getLoginSuccess().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });
        userViewModel.getErrorMessage().observe(this, msg ->
                Toast.makeText(this, "Error: " + msg, Toast.LENGTH_SHORT).show());
    }
}