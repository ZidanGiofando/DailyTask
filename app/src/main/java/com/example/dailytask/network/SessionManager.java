package com.example.dailytask.network;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.dailytask.model.User;

/**
 * Menyimpan status login user aktif menggunakan SharedPreferences.
 * Catatan: ini BUKAN basis data lokal untuk data utama aplikasi (task),
 * hanya menyimpan session (id/nama/email user yang sedang login) agar
 * tidak perlu login ulang setiap membuka aplikasi. Seluruh data Task
 * tetap disimpan & diambil dari MySQL melalui REST API.
 */
public class SessionManager {

    private static final String PREF_NAME = "dailytask_session";
    private static final String KEY_IS_LOGIN = "is_login";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";

    private final SharedPreferences pref;

    public SessionManager(Context context) {
        pref = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveSession(User user) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(KEY_IS_LOGIN, true);
        editor.putInt(KEY_USER_ID, user.getId());
        editor.putString(KEY_NAME, user.getName());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGIN, false);
    }

    public int getUserId() {
        return pref.getInt(KEY_USER_ID, -1);
    }

    public String getName() {
        return pref.getString(KEY_NAME, "");
    }

    public String getEmail() {
        return pref.getString(KEY_EMAIL, "");
    }

    public void logout() {
        pref.edit().clear().apply();
    }
}
