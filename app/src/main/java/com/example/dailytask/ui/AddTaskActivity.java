package com.example.dailytask.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.dailytask.R;
import com.example.dailytask.model.ApiResponse;
import com.example.dailytask.model.Task;
import com.example.dailytask.network.ApiClient;
import com.example.dailytask.network.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddTaskActivity extends AppCompatActivity {

    public static final String EXTRA_TASK_ID = "extra_task_id";
    public static final String EXTRA_TASK_TITLE = "extra_task_title";
    public static final String EXTRA_TASK_DESCRIPTION = "extra_task_description";
    public static final String EXTRA_TASK_DEADLINE = "extra_task_deadline";
    public static final String EXTRA_TASK_TIME = "extra_task_time";
    public static final String EXTRA_TASK_PRIORITY = "extra_task_priority";

    private Toolbar toolbar;
    private TextInputEditText etTitle, etDescription, etDeadline, etTime;
    private ChipGroup chipPriority;
    private Chip chipHigh, chipMedium, chipLow;
    private MaterialButton btnSave;
    private ProgressBar progressBar;

    private SessionManager sessionManager;
    private int editTaskId = -1;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        sessionManager = new SessionManager(this);

        toolbar = findViewById(R.id.toolbar);
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etDeadline = findViewById(R.id.etDeadline);
        etTime = findViewById(R.id.etTime);
        chipPriority = findViewById(R.id.chipPriority);
        chipHigh = findViewById(R.id.chipHigh);
        chipMedium = findViewById(R.id.chipMedium);
        chipLow = findViewById(R.id.chipLow);
        btnSave = findViewById(R.id.btnSave);
        progressBar = findViewById(R.id.progressBar);

        toolbar.setNavigationOnClickListener(v -> finish());

        etDeadline.setOnClickListener(v -> showDatePicker());
        etTime.setOnClickListener(v -> showTimePicker());

        checkEditMode();

        btnSave.setOnClickListener(v -> saveTask());
    }

    private void checkEditMode() {
        editTaskId = getIntent().getIntExtra(EXTRA_TASK_ID, -1);
        if (editTaskId != -1) {
            isEditMode = true;
            toolbar.setTitle("Edit Tugas");
            toolbar.setTitleTextColor(Color.WHITE);
            btnSave.setText("Update Tugas");

            etTitle.setText(getIntent().getStringExtra(EXTRA_TASK_TITLE));
            etDescription.setText(getIntent().getStringExtra(EXTRA_TASK_DESCRIPTION));
            etDeadline.setText(getIntent().getStringExtra(EXTRA_TASK_DEADLINE));
            etTime.setText(getIntent().getStringExtra(EXTRA_TASK_TIME));

            String priority = getIntent().getStringExtra(EXTRA_TASK_PRIORITY);
            if (priority != null) {
                if (priority.equalsIgnoreCase("High")) {
                    chipHigh.setChecked(true);
                } else if (priority.equalsIgnoreCase("Low")) {
                    chipLow.setChecked(true);
                } else {
                    chipMedium.setChecked(true);
                }
            }
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    etDeadline.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog dialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                    etTime.setText(time);
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true);
        dialog.show();
    }

    private void saveTask() {
        String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
        String description = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";
        String deadline = etDeadline.getText() != null ? etDeadline.getText().toString().trim() : "";
        String time = etTime.getText() != null ? etTime.getText().toString().trim() : "";

        if (TextUtils.isEmpty(title)) {
            etTitle.setError("Judul tidak boleh kosong");
            return;
        }

        int checkedChipId = chipPriority.getCheckedChipId();
        String priority = "Medium";
        if (checkedChipId == R.id.chipHigh) priority = "High";
        else if (checkedChipId == R.id.chipLow) priority = "Low";

        setLoading(true);
        int userId = sessionManager.getUserId();

        if (isEditMode) {
            // Memasukkan 7 parameter sesuai ApiService (termasuk userId)
            ApiClient.getApiService()
                    .updateTask(editTaskId, userId, title, description, deadline, time, priority)
                    .enqueue(new Callback<ApiResponse<Task>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<Task>> call, Response<ApiResponse<Task>> response) {
                            handleSaveResponse(response, "Tugas berhasil diperbarui");
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<Task>> call, Throwable t) {
                            handleSaveFailure(t);
                        }
                    });
        } else {
            ApiClient.getApiService()
                    .createTask(userId, title, description, deadline, time, priority)
                    .enqueue(new Callback<ApiResponse<Task>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<Task>> call, Response<ApiResponse<Task>> response) {
                            handleSaveResponse(response, "Tugas berhasil ditambahkan");
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<Task>> call, Throwable t) {
                            handleSaveFailure(t);
                        }
                    });
        }
    }

    private void handleSaveResponse(Response<ApiResponse<Task>> response, String successMessage) {
        setLoading(false);
        if (response.isSuccessful() && response.body() != null) {
            if (response.body().isSuccess()) {
                Toast.makeText(this, successMessage, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Gagal: " + response.body().getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Kesalahan Server!", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleSaveFailure(Throwable t) {
        setLoading(false);
        Toast.makeText(this, "Koneksi Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnSave.setEnabled(!loading);
    }
}
