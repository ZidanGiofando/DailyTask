package com.example.dailytask.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dailytask.R;
import com.example.dailytask.model.ApiResponse;
import com.example.dailytask.model.Task;
import com.example.dailytask.network.ApiClient;
import com.example.dailytask.network.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private TextView txtName, txtEmail, txtTotal, txtDone, txtPending;
    private MaterialCardView cardEditProfile, cardAbout;
    private MaterialButton btnLogout;

    private SessionManager sessionManager;

    public ProfileFragment() {
        super(R.layout.fragment_profile);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());

        txtName = view.findViewById(R.id.txtName);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtTotal = view.findViewById(R.id.txtTotal);
        txtDone = view.findViewById(R.id.txtDone);
        txtPending = view.findViewById(R.id.txtPending);
        cardEditProfile = view.findViewById(R.id.cardEditProfile);
        cardAbout = view.findViewById(R.id.cardAbout);
        btnLogout = view.findViewById(R.id.btnLogout);

        txtName.setText(sessionManager.getName());
        txtEmail.setText(sessionManager.getEmail());

        loadStats();

        cardEditProfile.setOnClickListener(v ->
                Toast.makeText(requireContext(), "Fitur edit profile: menyusul", Toast.LENGTH_SHORT).show());

        cardAbout.setOnClickListener(v -> new AlertDialog.Builder(requireContext())
                .setTitle("Tentang Aplikasi")
                .setMessage("DailyTask - Aplikasi pengingat & pengelola tugas harian.\nDibuat untuk UAS Mobile Programming.")
                .setPositiveButton("OK", null)
                .show());

        btnLogout.setOnClickListener(v -> confirmLogout());
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStats();
    }

    private void loadStats() {
        int userId = sessionManager.getUserId();
        ApiClient.getApiService().getTasks(userId).enqueue(new Callback<ApiResponse<List<Task>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Task>>> call, Response<ApiResponse<List<Task>>> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()
                        && response.body().getData() != null) {
                    List<Task> tasks = response.body().getData();
                    int total = tasks.size();
                    int done = 0;
                    for (Task t : tasks) {
                        if (t.isDone()) done++;
                    }
                    txtTotal.setText(String.valueOf(total));
                    txtDone.setText(String.valueOf(done));
                    txtPending.setText(String.valueOf(total - done));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Task>>> call, Throwable t) {
                // Diamkan, statistik cukup gagal senyap agar UX tidak terganggu
            }
        });
    }

    private void confirmLogout() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Apakah Anda yakin ingin keluar?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    sessionManager.logout();
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                })
                .setNegativeButton("Batal", null)
                .show();
    }
}
