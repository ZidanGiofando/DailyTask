package com.example.dailytask.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailytask.R;
import com.example.dailytask.model.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    public interface OnTaskActionListener {
        void onItemClick(Task task);
        void onEditClick(Task task);
        void onDeleteClick(Task task);
        void onDoneClick(Task task);
    }

    private List<Task> taskList = new ArrayList<>();
    private final OnTaskActionListener listener;
    private final boolean showActions; // false = mode ringkas (dashboard), true = mode lengkap (task list)

    public TaskAdapter(OnTaskActionListener listener, boolean showActions) {
        this.listener = listener;
        this.showActions = showActions;
    }

    public void setTasks(List<Task> tasks) {
        this.taskList = tasks != null ? tasks : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        holder.tvTitle.setText(task.getTitle());
        holder.tvDescription.setText(task.getDescription());
        holder.tvDeadline.setText(task.getDeadline());

        String priority = task.getPriority() != null ? task.getPriority() : "Medium";
        holder.tvPriority.setText(priority.toUpperCase());
        switch (priority) {
            case "High":
                holder.tvPriority.setBackgroundResource(R.drawable.bg_priority_high);
                break;
            case "Low":
                holder.tvPriority.setBackgroundResource(R.drawable.bg_priority_low);
                break;
            default:
                holder.tvPriority.setBackgroundResource(R.drawable.bg_priority_medium);
                break;
        }

        if (task.isDone()) {
            holder.tvStatus.setText("Selesai");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_done);
            holder.btnDone.setEnabled(false);
            holder.btnDone.setAlpha(0.4f);
        } else {
            holder.tvStatus.setText("Aktif");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_pending);
            holder.btnDone.setEnabled(true);
            holder.btnDone.setAlpha(1f);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(task);
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditClick(task);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(task);
        });

        holder.btnDone.setOnClickListener(v -> {
            if (listener != null && !task.isDone()) listener.onDoneClick(task);
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvDeadline, tvPriority, tvStatus;
        ImageButton btnEdit, btnDelete, btnDone;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDeadline = itemView.findViewById(R.id.tvDeadline);
            tvPriority = itemView.findViewById(R.id.tvPriority);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnDone = itemView.findViewById(R.id.btnDone);
        }
    }
}
