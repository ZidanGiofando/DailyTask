package com.example.dailytask.network;

import com.example.dailytask.model.ApiResponse;
import com.example.dailytask.model.Task;
import com.example.dailytask.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    // ================= AUTH =================
    @FormUrlEncoded
    @POST("auth/register.php")
    Call<ApiResponse<User>> register(
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("auth/login.php")
    Call<ApiResponse<User>> login(
            @Field("email") String email,
            @Field("password") String password
    );

    // ================= TASK =================
    @GET("tasks/read.php")
    Call<ApiResponse<List<Task>>> getTasks(@Query("user_id") int userId);

    @FormUrlEncoded
    @POST("tasks/create.php")
    Call<ApiResponse<Task>> createTask(
            @Field("user_id") int userId,
            @Field("title") String title,
            @Field("description") String description,
            @Field("deadline") String deadline,
            @Field("time") String time,
            @Field("priority") String priority
    );

    @FormUrlEncoded
    @POST("tasks/update.php")
    Call<ApiResponse<Task>> updateTask(
            @Field("id") int id,
            @Field("user_id") int userId,
            @Field("title") String title,
            @Field("description") String description,
            @Field("deadline") String deadline,
            @Field("time") String time,
            @Field("priority") String priority
    );

    @FormUrlEncoded
    @POST("tasks/update_status.php")
    Call<ApiResponse<Task>> updateStatus(
            @Field("id") int id,
            @Field("status") String status
    );

    @FormUrlEncoded
    @POST("tasks/delete.php")
    Call<ApiResponse<Object>> deleteTask(@Field("id") int id);
}
