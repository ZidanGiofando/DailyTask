package com.example.dailytask.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.dailytask.R;
import com.example.dailytask.model.ApiResponse;
import com.example.dailytask.model.Task;
import com.example.dailytask.network.ApiClient;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Menampilkan detail 1 Task yang dikirim lewat Intent (Serializable) dari
 * TaskFragment / DashboardFragment. Dari sini user bisa Edit (pindah ke
 * AddTaskActivity via Intent), Tandai Selesai, atau Hapus (REST API DELETE).
 */
public class DetailTaskActivity extends AppCompatActivity {

    public static final String EXTRA_TASK = "extra_task";

    private Toolbar toolbar;
    private TextView txtTitle, txtDescription, txtDeadline, txtTime, txtPriority, txtStatus;
    private MaterialButton btnEdit, btnDone, btnDelete;

    private Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_task);

        toolbar = findViewById(R.id.toolbar);
        txtTitle = findViewById(R.id.txtTitle);
        txtDescription = findViewById(R.id.txtDescription);
        txtDeadline = findViewById(R.id.txtDeadline);
        txtTime = findViewById(R.id.txtTime);
        txtPriority = findViewById(R.id.txtPriority);
        txtStatus = findViewById(R.id.txtStatus);
        btnEdit = findViewById(R.id.btnEdit);
        btnDone = findViewById(R.id.btnDone);
        btnDelete = findViewById(R.id.btnDelete);

        toolbar.setNavigationOnClickListener(v -> finish());

        task = (Task) getIntent().getSerializableExtra(EXTRA_TASK);
        if (task == null) {
            Toast.makeText(this, "Data tugas tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bindData();

        btnEdit.setOnClickListener(v -> openEdit());
        btnDone.setOnClickListener(v -> markAsDone());
        btnDelete.setOnClickListener(v -> confirmDelete());
    }

    private void bindData() {
        txtTitle.setText(task.getTitle());
        txtDescription.setText(task.getDescription());
        txtDeadline.setText(task.getDeadline());
        txtTime.setText(task.getTime());
        txtPriority.setText(task.getPriority() != null ? task.getPriority().toUpperCase() : "-");

        if (task.isDone()) {
            txtStatus.setText("Selesai");
            txtStatus.setBackgroundResource(R.drawable.bg_status_done);
            btnDone.setEnabled(false);
            btnDone.setText("SUDAH SELESAI");
        } else {
            txtStatus.setText("Belum Selesai");
            txtStatus.setBackgroundResource(R.drawable.bg_status_pending);
        }

        switch (task.getPriority() != null ? task.getPriority() : "") {
            case "High":
                txtPriority.setBackgroundResource(R.drawable.bg_priority_high);
                break;
            case "Low":
                txtPriority.setBackgroundResource(R.drawable.bg_priority_low);
                break;
            default:
                txtPriority.setBackgroundResource(R.drawable.bg_priority_medium);
                break;
        }
    }

    /** Intent perpindahan ke AddTaskActivity dalam mode edit, membawa data task. */
    private void openEdit() {
        Intent intent = new Intent(this, AddTaskActivity.class);
        intent.putExtra(AddTaskActivity.EXTRA_TASK_ID, task.getId());
        intent.putExtra(AddTaskActivity.EXTRA_TASK_TITLE, task.getTitle());
        intent.putExtra(AddTaskActivity.EXTRA_TASK_DESCRIPTION, task.getDescription());
        intent.putExtra(AddTaskActivity.EXTRA_TASK_DEADLINE, task.getDeadline());
        intent.putExtra(AddTaskActivity.EXTRA_TASK_TIME, task.getTime());
        intent.putExtra(AddTaskActivity.EXTRA_TASK_PRIORITY, task.getPriority());
        startActivity(intent);
        setResult(RESULT_OK);
        finish();
    }

    private void markAsDone() {
        ApiClient.getApiService().updateStatus(task.getId(), "done")
                .enqueue(new Callback<ApiResponse<Task>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Task>> call, Response<ApiResponse<Task>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            Toast.makeText(DetailTaskActivity.this, "Tugas ditandai selesai", Toast.LENGTH_SHORT).show();
                            task.setStatus("done");
                            bindData();
                            setResult(RESULT_OK);
                        } else {
                            Toast.makeText(DetailTaskActivity.this, "Gagal memperbarui status", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Task>> call, Throwable t) {
                        Toast.makeText(DetailTaskActivity.this, "Gagal terhubung ke server", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Tugas")
                .setMessage("Apakah Anda yakin ingin menghapus tugas ini?")
                .setPositiveButton("Hapus", (dialog, which) -> deleteTask())
                .setNegativeButton("Batal", null)
                .show();
    }

    private void deleteTask() {
        ApiClient.getApiService().deleteTask(task.getId())
                .enqueue(new Callback<ApiResponse<Object>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            Toast.makeText(DetailTaskActivity.this, "Tugas berhasil dihapus", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(DetailTaskActivity.this, "Gagal menghapus tugas", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                        Toast.makeText(DetailTaskActivity.this, "Gagal terhubung ke server", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
