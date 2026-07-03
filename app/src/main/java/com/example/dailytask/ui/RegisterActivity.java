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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilName, tilEmail, tilPassword, tilConfirmPassword;
    private TextInputEditText etName, etEmail, etPassword, etConfirmPassword;
    private MaterialButton btnRegister;
    private ProgressBar progressBar;
    private TextView txtLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tilName = findViewById(R.id.tilName);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);
        txtLogin = findViewById(R.id.txtLogin);

        btnRegister.setOnClickListener(v -> attemptRegister());
        txtLogin.setOnClickListener(v -> finish()); // kembali ke LoginActivity
    }

    private void attemptRegister() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
        String confirm = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim() : "";

        tilName.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);

        boolean valid = true;

        // ===== Validasi Input =====
        if (TextUtils.isEmpty(name)) {
            tilName.setError("Nama tidak boleh kosong");
            valid = false;
        }

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

        if (TextUtils.isEmpty(confirm)) {
            tilConfirmPassword.setError("Konfirmasi password tidak boleh kosong");
            valid = false;
        } else if (!confirm.equals(password)) {
            tilConfirmPassword.setError("Konfirmasi password tidak sama");
            valid = false;
        }

        if (!valid) return;

        setLoading(true);

        ApiClient.getApiService().register(name, email, password).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<User> body = response.body();
                    if (body.isSuccess()) {
                        Toast.makeText(RegisterActivity.this, "Registrasi berhasil, silakan login", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, body.getMessage() != null ? body.getMessage() : "Registrasi gagal", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Registrasi gagal, coba lagi", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                setLoading(false);
                Toast.makeText(RegisterActivity.this, "Tidak dapat terhubung ke server: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!loading);
    }
}
