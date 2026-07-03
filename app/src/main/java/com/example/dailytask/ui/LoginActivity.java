package com.example.dailytask.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dailytask.R;
import com.example.dailytask.model.ApiResponse;
import com.example.dailytask.model.User;
import com.example.dailytask.network.ApiClient;
import com.example.dailytask.network.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private ProgressBar progressBar;
    private TextView txtRegister;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);

        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
        txtRegister = findViewById(R.id.txtRegister);

        btnLogin.setOnClickListener(v -> attemptLogin());

        txtRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void attemptLogin() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        tilEmail.setError(null);
        tilPassword.setError(null);

        boolean valid = true;

        // ===== Validasi Input =====
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email tidak boleh kosong");
            valid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Format email tidak valid");
            valid = false;
        }

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Password tidak boleh kosong");
            valid = false;
        } else if (password.length() < 6) {
            tilPassword.setError("Password minimal 6 karakter");
            valid = false;
        }

        if (!valid) return;

        setLoading(true);

        ApiClient.getApiService().login(email, password).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<User> body = response.body();
                    if (body.isSuccess() && body.getData() != null) {
                        sessionManager.saveSession(body.getData());
                        Toast.makeText(LoginActivity.this, "Login berhasil", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, body.getMessage() != null ? body.getMessage() : "Email atau password salah", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Login gagal, coba lagi", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                setLoading(false);
                Toast.makeText(LoginActivity.this, "Tidak dapat terhubung ke server: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!loading);
    }
}
