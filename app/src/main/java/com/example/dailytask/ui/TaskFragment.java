package com.example.dailytask.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.dailytask.R;
import com.example.dailytask.adapter.TaskAdapter;
import com.example.dailytask.model.ApiResponse;
import com.example.dailytask.model.Task;
import com.example.dailytask.network.ApiClient;
import com.example.dailytask.network.SessionManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskFragment extends Fragment implements TaskAdapter.OnTaskActionListener {

    private TextInputEditText etSearch;

    private ChipGroup chipGroup;
    private Chip chipAll, chipPending, chipDone;

    private SwipeRefreshLayout swipeRefresh;

    private RecyclerView rvTask;

    private View progressBar;

    private TextView tvEmpty;
    private TextView tvTaskCount;

    private FloatingActionButton fabAddTask;

    private TaskAdapter adapter;
    private SessionManager sessionManager;

    private final List<Task> allTasks = new ArrayList<>();

    private String currentFilter = "all";
    private String currentQuery = "";

    private ActivityResultLauncher<Intent> addEditLauncher;

    public TaskFragment() {
        super(R.layout.fragment_task);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addEditLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        result -> {
                            if (result.getResultCode() == Activity.RESULT_OK) {
                                loadTasks();
                            }
                        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());

        etSearch = view.findViewById(R.id.etSearch);

        chipGroup = view.findViewById(R.id.chipGroup);
        chipAll = view.findViewById(R.id.chipAll);
        chipPending = view.findViewById(R.id.chipPending);
        chipDone = view.findViewById(R.id.chipDone);

        swipeRefresh = view.findViewById(R.id.swipeRefresh);

        rvTask = view.findViewById(R.id.rvTask);

        progressBar = view.findViewById(R.id.progressBar);

        tvEmpty = view.findViewById(R.id.tvEmpty);
        tvTaskCount = view.findViewById(R.id.tvTaskCount);

        fabAddTask = view.findViewById(R.id.fabAddTask);

        fabAddTask.setScaleX(0f);
        fabAddTask.setScaleY(0f);

        fabAddTask.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(350)
                .start();

        adapter = new TaskAdapter(this, true);

        rvTask.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvTask.setHasFixedSize(true);
        rvTask.setItemAnimator(new DefaultItemAnimator());
        rvTask.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(this::loadTasks);

        fabAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddTaskActivity.class);
            addEditLauncher.launch(intent);
        });

        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {

            if (checkedIds.contains(R.id.chipPending)) {
                currentFilter = "pending";
            } else if (checkedIds.contains(R.id.chipDone)) {
                currentFilter = "done";
            } else {
                currentFilter = "all";
            }

            applyFilter();
        });

        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s,
                                          int start,
                                          int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s,
                                      int start,
                                      int before,
                                      int count) {

                currentQuery = s.toString().trim();
                applyFilter();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        loadTasks();
    }
    @Override
    public void onResume() {
        super.onResume();
        loadTasks();
    }

    private void loadTasks() {

        swipeRefresh.setRefreshing(true);

        if (allTasks.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
        }

        int userId = sessionManager.getUserId();

        ApiClient.getApiService().getTasks(userId)
                .enqueue(new Callback<ApiResponse<List<Task>>>() {

                    @Override
                    public void onResponse(Call<ApiResponse<List<Task>>> call,
                                           Response<ApiResponse<List<Task>>> response) {

                        if (!isAdded()) return;

                        swipeRefresh.setRefreshing(false);
                        progressBar.setVisibility(View.GONE);

                        allTasks.clear();

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()
                                && response.body().getData() != null) {

                            allTasks.addAll(response.body().getData());
                        }

                        if (allTasks.isEmpty()) {
                            tvEmpty.setText("🎉 Belum ada task.\nTekan tombol + untuk membuat task pertama.");
                        }

                        applyFilter();
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<Task>>> call,
                                          Throwable t) {

                        if (!isAdded()) return;

                        swipeRefresh.setRefreshing(false);
                        progressBar.setVisibility(View.GONE);

                        Toast.makeText(requireContext(),
                                "Gagal memuat data : " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void applyFilter() {

        List<Task> filtered = new ArrayList<>();

        for (Task task : allTasks) {

            boolean matchStatus;

            switch (currentFilter) {

                case "pending":
                    matchStatus = !task.isDone();
                    break;

                case "done":
                    matchStatus = task.isDone();
                    break;

                default:
                    matchStatus = true;
                    break;
            }

            boolean matchQuery =
                    currentQuery.isEmpty()
                            || (task.getTitle() != null &&
                            task.getTitle().toLowerCase().contains(currentQuery.toLowerCase()))
                            || (task.getDescription() != null &&
                            task.getDescription().toLowerCase().contains(currentQuery.toLowerCase()));

            if (matchStatus && matchQuery) {
                filtered.add(task);
            }
        }

        adapter.setTasks(filtered);

        tvEmpty.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);

        tvTaskCount.setText(
                "You have " +
                        filtered.size() +
                        " mission" +
                        (filtered.size() == 1 ? "" : "s") +
                        " today"
        );
    }

    private void openDetail(Task task) {

        Intent intent = new Intent(requireContext(), DetailTaskActivity.class);
        intent.putExtra(DetailTaskActivity.EXTRA_TASK, task);

        addEditLauncher.launch(intent);
    }

    @Override
    public void onItemClick(Task task) {
        openDetail(task);
    }

    @Override
    public void onEditClick(Task task) {

        Intent intent = new Intent(requireContext(), AddTaskActivity.class);

        intent.putExtra(AddTaskActivity.EXTRA_TASK_ID, task.getId());
        intent.putExtra(AddTaskActivity.EXTRA_TASK_TITLE, task.getTitle());
        intent.putExtra(AddTaskActivity.EXTRA_TASK_DESCRIPTION, task.getDescription());
        intent.putExtra(AddTaskActivity.EXTRA_TASK_DEADLINE, task.getDeadline());
        intent.putExtra(AddTaskActivity.EXTRA_TASK_TIME, task.getTime());
        intent.putExtra(AddTaskActivity.EXTRA_TASK_PRIORITY, task.getPriority());

        addEditLauncher.launch(intent);
    }

    @Override
    public void onDeleteClick(Task task) {

        new AlertDialog.Builder(requireContext())
                .setTitle("Hapus Tugas")
                .setMessage("Apakah Anda yakin ingin menghapus \"" + task.getTitle() + "\"?")
                .setPositiveButton("Hapus", (dialog, which) -> deleteTask(task))
                .setNegativeButton("Batal", null)
                .show();
    }

    private void deleteTask(Task task) {

        ApiClient.getApiService()
                .deleteTask(task.getId())
                .enqueue(new Callback<ApiResponse<Object>>() {

                    @Override
                    public void onResponse(Call<ApiResponse<Object>> call,
                                           Response<ApiResponse<Object>> response) {

                        if (!isAdded()) return;

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()) {

                            Toast.makeText(requireContext(),
                                    "Tugas berhasil dihapus",
                                    Toast.LENGTH_SHORT).show();

                            loadTasks();

                        } else {

                            Toast.makeText(requireContext(),
                                    "Gagal menghapus tugas",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Object>> call,
                                          Throwable t) {

                        if (!isAdded()) return;

                        Toast.makeText(requireContext(),
                                "Gagal terhubung ke server",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDoneClick(Task task) {

        ApiClient.getApiService()
                .updateStatus(task.getId(), "done")
                .enqueue(new Callback<ApiResponse<Task>>() {

                    @Override
                    public void onResponse(Call<ApiResponse<Task>> call,
                                           Response<ApiResponse<Task>> response) {

                        if (!isAdded()) return;

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()) {

                            Toast.makeText(requireContext(),
                                    "Tugas ditandai selesai",
                                    Toast.LENGTH_SHORT).show();

                            loadTasks();

                        } else {

                            Toast.makeText(requireContext(),
                                    "Gagal memperbarui status",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Task>> call,
                                          Throwable t) {

                        if (!isAdded()) return;

                        Toast.makeText(requireContext(),
                                "Gagal terhubung ke server",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
