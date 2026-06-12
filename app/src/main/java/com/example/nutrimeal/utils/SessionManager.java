package com.example.nutrimeal.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "nutrimeal_session";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PHOTO_PATH = "photo_path";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveSession(int userId, String name, String username, String photoPath) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PHOTO_PATH, photoPath != null ? photoPath : "");
        editor.apply();
    }

    public void updatePhoto(String photoPath) {
        editor.putString(KEY_PHOTO_PATH, photoPath);
        editor.apply();
    }

    public void updateName(String name) {
        editor.putString(KEY_USER_NAME, name);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, -1);
    }

    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, "");
    }

    public String getUsername() {
        return prefs.getString(KEY_USERNAME, "");
    }

    public String getPhotoPath() {
        return prefs.getString(KEY_PHOTO_PATH, "");
    }

    public void logout(Context context) {
        editor.clear();
        editor.apply();
    }
}