package com.example.nutrimeal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nutrimeal.R;
import com.example.nutrimeal.database.UserDao;
import com.example.nutrimeal.model.User;
import com.example.nutrimeal.utils.SessionManager;
import com.example.nutrimeal.utils.ThemeUtils;
import com.google.android.material.button.MaterialButton;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etUsername, etPassword;
    private MaterialButton btnRegister;
    private TextView tvLogin;

    private UserDao userDao;
    private SessionManager sessionManager;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userDao = new UserDao(this);
        sessionManager = new SessionManager(this);

        etName = findViewById(R.id.et_name);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnRegister = findViewById(R.id.btn_register);
        tvLogin = findViewById(R.id.tv_login);

        btnRegister.setOnClickListener(v -> attemptRegister());
        tvLogin.setOnClickListener(v -> finish());
    }

    private void attemptRegister() {
        String name = etName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("Name is required");
            return;
        }
        if (username.isEmpty()) {
            etUsername.setError("Username is required");
            return;
        }
        if (username.length() < 3) {
            etUsername.setError("Username must be at least 3 characters");
            return;
        }
        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            return;
        }
        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            return;
        }

        btnRegister.setEnabled(false);
        btnRegister.setText("Creating account...");

        executor.execute(() -> {
            boolean isTaken = userDao.isUsernameTaken(username);
            if (isTaken) {
                mainHandler.post(() -> {
                    btnRegister.setEnabled(true);
                    btnRegister.setText("Create Account");
                    etUsername.setError("Username already taken");
                });
                return;
            }

            User user = new User(name, username, password);
            boolean success = userDao.register(user);

            mainHandler.post(() -> {
                btnRegister.setEnabled(true);
                btnRegister.setText("Create Account");

                if (success) {
                    Toast.makeText(this,
                            "Account created! Please login",
                            Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this,
                            "Registration failed, try again",
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}