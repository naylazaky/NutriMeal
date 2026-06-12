package com.example.nutrimeal.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.nutrimeal.model.User;

public class UserDao {

    private final NutriMealDatabase dbHelper;

    public UserDao(Context context) {
        dbHelper = NutriMealDatabase.getInstance(context);
    }

    public boolean register(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NutriMealDatabase.COL_NAME, user.getName());
        values.put(NutriMealDatabase.COL_USERNAME, user.getUsername());
        values.put(NutriMealDatabase.COL_PASSWORD, user.getPassword());
        values.put(NutriMealDatabase.COL_PHOTO_PATH, "");
        long result = db.insertWithOnConflict(NutriMealDatabase.TABLE_USERS,
                null, values, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
        return result != -1;
    }

    public User login(String username, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(NutriMealDatabase.TABLE_USERS,
                null,
                NutriMealDatabase.COL_USERNAME + "=? AND " +
                        NutriMealDatabase.COL_PASSWORD + "=?",
                new String[]{username, password},
                null, null, null);

        User user = null;
        if (cursor.moveToFirst()) {
            user = new User(
                    cursor.getString(cursor.getColumnIndexOrThrow(NutriMealDatabase.COL_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(NutriMealDatabase.COL_USERNAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(NutriMealDatabase.COL_PASSWORD))
            );
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(NutriMealDatabase.COL_ID)));
            user.setPhotoPath(cursor.getString(
                    cursor.getColumnIndexOrThrow(NutriMealDatabase.COL_PHOTO_PATH)));
        }
        cursor.close();
        db.close();
        return user;
    }

    public boolean isUsernameTaken(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(NutriMealDatabase.TABLE_USERS,
                new String[]{NutriMealDatabase.COL_ID},
                NutriMealDatabase.COL_USERNAME + "=?",
                new String[]{username}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public void updatePhotoPath(int userId, String photoPath) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NutriMealDatabase.COL_PHOTO_PATH, photoPath);
        db.update(NutriMealDatabase.TABLE_USERS, values,
                NutriMealDatabase.COL_ID + "=?",
                new String[]{String.valueOf(userId)});
        db.close();
    }

    public User getUserById(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(NutriMealDatabase.TABLE_USERS,
                null,
                NutriMealDatabase.COL_ID + "=?",
                new String[]{String.valueOf(userId)},
                null, null, null);

        User user = null;
        if (cursor.moveToFirst()) {
            user = new User(
                    cursor.getString(cursor.getColumnIndexOrThrow(NutriMealDatabase.COL_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(NutriMealDatabase.COL_USERNAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(NutriMealDatabase.COL_PASSWORD))
            );
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(NutriMealDatabase.COL_ID)));
            user.setPhotoPath(cursor.getString(
                    cursor.getColumnIndexOrThrow(NutriMealDatabase.COL_PHOTO_PATH)));
        }
        cursor.close();
        db.close();
        return user;
    }
}