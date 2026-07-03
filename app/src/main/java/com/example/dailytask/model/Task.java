package com.example.dailytask.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * Model class untuk data Task (Tugas).
 * Field disesuaikan dengan kolom tabel `tasks` pada database MySQL
 * dan response JSON dari REST API PHP.
 */
public class Task implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("deadline")
    private String deadline; // format: yyyy-MM-dd

    @SerializedName("time")
    private String time; // format: HH:mm

    @SerializedName("priority")
    private String priority; // High / Medium / Low

    @SerializedName("status")
    private String status; // pending / done

    @SerializedName("created_at")
    private String createdAt;

    public Task() {
    }

    public Task(int userId, String title, String description, String deadline,
                String time, String priority) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.time = time;
        this.priority = priority;
        this.status = "pending";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isDone() {
        return status != null && status.equalsIgnoreCase("done");
    }
}
