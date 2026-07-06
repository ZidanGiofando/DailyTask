package com.example.dailytask.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment implements TaskAdapter.OnTaskActionListener {

    private TextView tvGreeting;
    private TextView tvSubtitle;

    private TextView tvTotalTask;
    private TextView tvActiveTask;
    private TextView tvDoneTask;

    // Tambahan
    private TextView tvProgress;
    private TextView tvXp;
    private TextView tvStreak;

    private ProgressBar progressBar;

    private RecyclerView rvRecentTask;

    private TaskAdapter adapter;
    private SessionManager sessionManager;

    public DashboardFragment() {
        super(R.layout.fragment_dashboard);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());

        tvGreeting = view.findViewById(R.id.tvGreeting);
        tvSubtitle = view.findViewById(R.id.tvSubtitle);

        tvTotalTask = view.findViewById(R.id.tvTotalTask);
        tvActiveTask = view.findViewById(R.id.tvActiveTask);
        tvDoneTask = view.findViewById(R.id.tvDoneTask);

        tvProgress = view.findViewById(R.id.tvProgress);
        tvXp = view.findViewById(R.id.tvXp);
        tvStreak = view.findViewById(R.id.tvStreak);

        progressBar = view.findViewById(R.id.progressBar);

        rvRecentTask = view.findViewById(R.id.rvRecentTask);

        tvGreeting.setText(getGreeting() + ", " + sessionManager.getName() + " 👋");
        tvSubtitle.setText("Let's finish today's mission!");

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

        ApiClient.getApiService()
                .getTasks(sessionManager.getUserId())
                .enqueue(new Callback<ApiResponse<List<Task>>>() {

                    @Override
                    public void onResponse(Call<ApiResponse<List<Task>>> call,
                                           Response<ApiResponse<List<Task>>> response) {

                        if (!isAdded()) return;

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()) {

                            List<Task> tasks = response.body().getData();

                            if (tasks == null)
                                tasks = new ArrayList<>();

                            updateDashboard(tasks);

                            List<Task> recent =
                                    tasks.size() > 5 ?
                                            tasks.subList(0,5) :
                                            tasks;

                            adapter.setTasks(recent);
                        }

                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<Task>>> call,
                                          Throwable t) {

                        if (!isAdded()) return;

                        Toast.makeText(requireContext(),
                                "Gagal memuat data",
                                Toast.LENGTH_SHORT).show();

                    }

                });

    }

    private void updateDashboard(List<Task> tasks){

        int total = tasks.size();
        int done = 0;

        for(Task task : tasks){
            if(task.isDone()){
                done++;
            }
        }

        int active = total - done;

        tvTotalTask.setText(String.valueOf(total));
        tvActiveTask.setText(String.valueOf(active));
        tvDoneTask.setText(String.valueOf(done));

        // Progress %

        int percent = 0;

        if(total > 0){
            percent = (done * 100) / total;
        }

        progressBar.setProgress(percent);
        tvProgress.setText(percent + "% Completed");

        // XP

        int xp = done * 25;
        tvXp.setText(String.valueOf(xp));

        // Dummy streak

        tvStreak.setText(done + " Days");

    }

    private String getGreeting(){

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        if(hour < 12){
            return "Good Morning";
        }

        if(hour < 17){
            return "Good Afternoon";
        }

        if(hour < 20){
            return "Good Evening";
        }

        return "Good Night";
    }

    private void openDetail(Task task){

        Intent intent =
                new Intent(requireContext(),
                        DetailTaskActivity.class);

        intent.putExtra(
                DetailTaskActivity.EXTRA_TASK,
                task
        );

        startActivity(intent);

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
        startActivity(intent);
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

    @Override
    public void onDoneClick(Task task) {
        ApiClient.getApiService()
                .updateStatus(task.getId(), "done")
                .enqueue(new Callback<ApiResponse<Task>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Task>> call, Response<ApiResponse<Task>> response) {
                        if (!isAdded()) return;
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            Toast.makeText(requireContext(), "Tugas ditandai selesai", Toast.LENGTH_SHORT).show();
                            loadTasks();
                        } else {
                            Toast.makeText(requireContext(), "Gagal memperbarui status", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Task>> call, Throwable t) {
                        if (!isAdded()) return;
                        Toast.makeText(requireContext(), "Gagal terhubung ke server", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteTask(Task task) {
        ApiClient.getApiService()
                .deleteTask(task.getId())
                .enqueue(new Callback<ApiResponse<Object>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                        if (!isAdded()) return;
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            Toast.makeText(requireContext(), "Tugas berhasil dihapus", Toast.LENGTH_SHORT).show();
                            loadTasks();
                        } else {
                            Toast.makeText(requireContext(), "Gagal menghapus tugas", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                        if (!isAdded()) return;
                        Toast.makeText(requireContext(), "Gagal terhubung ke server", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
