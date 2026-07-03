package com.example.dailytask.ui;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailytask.R;
import com.example.dailytask.adapter.TaskAdapter;
import com.example.dailytask.model.ApiResponse;
import com.example.dailytask.model.Task;
import com.example.dailytask.network.ApiClient;
import com.example.dailytask.network.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment implements TaskAdapter.OnTaskActionListener {

    private TextView tvGreeting, tvSubtitle, tvTotalTask, tvActiveTask, tvDoneTask;
    private RecyclerView rvRecentTask;

    private TaskAdapter adapter;
    private SessionManager sessionManager;

    public DashboardFragment() {
        super(R.layout.fragment_dashboard);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());

        tvGreeting = view.findViewById(R.id.tvGreeting);
        tvSubtitle = view.findViewById(R.id.tvSubtitle);
        tvTotalTask = view.findViewById(R.id.tvTotalTask);
        tvActiveTask = view.findViewById(R.id.tvActiveTask);
        tvDoneTask = view.findViewById(R.id.tvDoneTask);
        rvRecentTask = view.findViewById(R.id.rvRecentTask);

        tvGreeting.setText("Halo, " + sessionManager.getName() + " \uD83D\uDC4B");
        tvSubtitle.setText("Selamat datang kembali");

        adapter = new TaskAdapter(this, false);
        rvRecentTask.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvRecentTask.setAdapter(adapter);

        loadTasks();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTasks();
    }

    private void loadTasks() {
        int userId = sessionManager.getUserId();
        ApiClient.getApiService().getTasks(userId).enqueue(new Callback<ApiResponse<List<Task>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Task>>> call, Response<ApiResponse<List<Task>>> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Task> tasks = response.body().getData();
                    if (tasks == null) tasks = new ArrayList<>();
                    updateStats(tasks);

                    // Tampilkan maksimal 5 task terbaru di dashboard
                    List<Task> recent = tasks.size() > 5 ? tasks.subList(0, 5) : tasks;
                    adapter.setTasks(recent);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Task>>> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Gagal memuat data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateStats(List<Task> tasks) {
        int total = tasks.size();
        int done = 0;
        for (Task t : tasks) {
            if (t.isDone()) done++;
        }
        int active = total - done;

        tvTotalTask.setText(String.valueOf(total));
        tvActiveTask.setText(String.valueOf(active));
        tvDoneTask.setText(String.valueOf(done));
    }

    private void openDetail(Task task) {
        Intent intent = new Intent(requireContext(), DetailTaskActivity.class);
        intent.putExtra(DetailTaskActivity.EXTRA_TASK, task);
        startActivity(intent);
    }

    @Override
    public void onItemClick(Task task) {
        openDetail(task);
    }

    @Override
    public void onEditClick(Task task) {
        openDetail(task);
    }

    @Override
    public void onDeleteClick(Task task) {
        openDetail(task);
    }

    @Override
    public void onDoneClick(Task task) {
        openDetail(task);
    }
}
