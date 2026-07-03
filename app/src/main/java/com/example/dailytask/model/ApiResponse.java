package com.example.dailytask.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Generic wrapper untuk semua response REST API PHP.
 * Struktur JSON backend selalu:
 * {
 *   "status": "success" | "error",
 *   "message": "...",
 *   "data": { ... }  atau  [ ... ]  atau null
 * }
 */
public class ApiResponse<T> {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private T data;

    public boolean isSuccess() {
        return "success".equalsIgnoreCase(status);
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
