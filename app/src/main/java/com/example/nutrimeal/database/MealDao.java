package com.example.nutrimeal.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.nutrimeal.model.FavoriteEntity;
import com.example.nutrimeal.model.NoteEntity;
import com.example.nutrimeal.model.PlannerEntity;

import java.util.ArrayList;
import java.util.List;

public class MealDao {

    private final NutriMealDatabase dbHelper;

    public MealDao(Context context) {
        dbHelper = NutriMealDatabase.getInstance(context);
    }

    // ===================== FAVORITES =====================

    public void insertFavorite(int userId, FavoriteEntity favorite) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NutriMealDatabase.COL_USER_ID, userId);
        values.put(NutriMealDatabase.COL_MEAL_ID, favorite.getMealId());
        values.put(NutriMealDatabase.COL_MEAL_NAME, favorite.getMealName());
        values.put(NutriMealDatabase.COL_MEAL_THUMB, favorite.getMealThumb());
        values.put(NutriMealDatabase.COL_CATEGORY, favorite.getCategory());
        values.put(NutriMealDatabase.COL_AREA, favorite.getArea());
        values.put(NutriMealDatabase.COL_INSTRUCTIONS, favorite.getInstructions());
        values.put(NutriMealDatabase.COL_DATE_ADDED, favorite.getDateAdded());
        db.insertWithOnConflict(NutriMealDatabase.TABLE_FAVORITES,
                null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void deleteFavorite(int userId, String mealId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(NutriMealDatabase.TABLE_FAVORITES,
                NutriMealDatabase.COL_USER_ID + "=? AND " +
                        NutriMealDatabase.COL_MEAL_ID + "=?",
                new String[]{String.valueOf(userId), mealId});
    }

    public boolean isFavorite(int userId, String mealId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(NutriMealDatabase.TABLE_FAVORITES,
                new String[]{NutriMealDatabase.COL_MEAL_ID},
                NutriMealDatabase.COL_USER_ID + "=? AND " +
                        NutriMealDatabase.COL_MEAL_ID + "=?",
                new String[]{String.valueOf(userId), mealId},
                null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public List<FavoriteEntity> getAllFavorites(int userId) {
        List<FavoriteEntity> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(NutriMealDatabase.TABLE_FAVORITES,
                null,
                NutriMealDatabase.COL_USER_ID + "=?",
                new String[]{String.valueOf(userId)},
                null, null,
                NutriMealDatabase.COL_DATE_ADDED + " DESC");

        if (cursor.moveToFirst()) {
            do {
                FavoriteEntity fav = new FavoriteEntity(
                        cursor.getString(cursor.getColumnIndexOrThrow(NutriMealDatabase.COL_MEAL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(NutriMealDatabase.COL_MEAL_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(NutriMealDatabase.COL_MEAL_THUMB)),
                        cursor.getString(cursor.getColumnIndexOrThrow(NutriMealDatabase.COL_CATEGORY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(NutriMealDatabase.COL_AREA)),
                        cursor.getString(cursor.getColumnIndexOrThrow(NutriMealDatabase.COL_INSTRUCTIONS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(NutriMealDatabase.COL_DATE_ADDED))
                );
                fav.setId(cursor.getInt(cursor.getColumnIndexOrThrow(NutriMealDatabase.COL_ID)));
                list.add(fav);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // ===================== NOTES =====================

    public void insertOrUpdateNote(int userId, NoteEntity note) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NutriMealDatabase.COL_USER_ID, userId);
        values.put(NutriMealDatabase.COL_MEAL_ID, note.getMealId());
        values.put(NutriMealDatabase.COL_MEAL_NAME, note.getMealName());
        values.put(NutriMealDatabase.COL_NOTE_TEXT, note.getNoteText());
        values.put(NutriMealDatabase.COL_STAR_RATING, note.getStarRating());
        values.put(NutriMealDatabase.COL_DATE_MODIFIED, note.getDateModified());
        db.insertWithOnConflict(NutriMealDatabase.TABLE_NOTES,
                null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void deleteNote(int userId, String mealId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(NutriMealDatabase.TABLE_NOTES,
                NutriMealDatabase.COL_USER_ID + "=? AND " +
                        NutriMealDatabase.COL_MEAL_ID + "=?",
                new String[]{String.valueOf(userId), mealId});
    }

    // ✅ Method yang hilang — dipakai di DetailFragment
    public NoteEntity getNoteByMealId(int userId, String mealId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(NutriMealDatabase.TABLE_NOTES,
                null,
                NutriMealDatabase.COL_USER_ID + "=? AND " +
                        NutriMealDatabase.COL_MEAL_ID + "=?",
                new String[]{String.valueOf(userId), mealId},
                null, null, null);
        NoteEntity note = null;
        if (cursor.moveToFirst()) {
            note = new NoteEntity(
                    cursor.getString(cursor.getColumnIndexOrThrow(NutriMealDatabase.COL_MEAL_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(NutriMealDatabase.COL_MEAL_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(NutriMealDatabase.COL_NOTE_TEXT)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(NutriMealDatabase.COL_STAR_RATING)),
                    cursor.getString(cursor.getColumnIndexOrThrow(NutriMealDatabase.COL_DATE_MODIFIED))
            );
            note.setId(cursor.getInt(cursor.getColumnIndexOrThrow(NutriMealDatabase.COL_ID)));
        }
        cursor.close();
        return note;
    }

    public List<NoteEntity> getAllNotes(int userId) {
        List<NoteEntity> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(NutriMealDatabase.TABLE_NOTES,
                null,
                NutriMealDatabase.COL_USER_ID + "=?",
                new String[]{String.valueOf(userId)},
                null, null,
                NutriMealDatabase.COL_DATE_MODIFIED + " DESC");

        if (cursor.moveToFirst()) {
            do {
                // ✅ Fix — tambah mealName sebagai parameter ke-2
                NoteEntity note = new NoteEntity(
                        cursor.getString(cursor.getColumnIndexOrThrow(NutriMealDatabase.COL_MEAL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(NutriMealDatabase.COL_MEAL_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(NutriMealDatabase.COL_NOTE_TEXT)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(NutriMealDatabase.COL_STAR_RATING)),
                        cursor.getString(cursor.getColumnIndexOrThrow(NutriMealDatabase.COL_DATE_MODIFIED))
                );
                note.setId(cursor.getInt(cursor.getColumnIndexOrThrow(NutriMealDatabase.COL_ID)));
                list.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // ===================== PLANNER =====================

    public void insertOrUpdatePlanner(int userId, PlannerEntity planner) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NutriMealDatabase.COL_USER_ID, userId);
        values.put(NutriMealDatabase.COL_DAY_OF_WEEK, planner.getDayOfWeek());
        values.put(NutriMealDatabase.COL_MEAL_ID, planner.getMealId());
        values.put(NutriMealDatabase.COL_MEAL_NAME, planner.getMealName());
        values.put(NutriMealDatabase.COL_MEAL_THUMB, planner.getMealThumb());
        db.insertWithOnConflict(NutriMealDatabase.TABLE_PLANNER,
                null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void deletePlannerByDay(int userId, String dayOfWeek) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(NutriMealDatabase.TABLE_PLANNER,
                NutriMealDatabase.COL_USER_ID + "=? AND " +
                        NutriMealDatabase.COL_DAY_OF_WEEK + "=?",
                new String[]{String.valueOf(userId), dayOfWeek});
    }

    public List<PlannerEntity> getAllPlanner(int userId) {
        List<PlannerEntity> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(NutriMealDatabase.TABLE_PLANNER,
                null,
                NutriMealDatabase.COL_USER_ID + "=?",
                new String[]{String.valueOf(userId)},
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                PlannerEntity planner = new PlannerEntity(
                        cursor.getString(cursor.getColumnIndexOrThrow(NutriMealDatabase.COL_DAY_OF_WEEK)),
                        cursor.getString(cursor.getColumnIndexOrThrow(NutriMealDatabase.COL_MEAL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(NutriMealDatabase.COL_MEAL_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(NutriMealDatabase.COL_MEAL_THUMB))
                );
                planner.setId(cursor.getInt(cursor.getColumnIndexOrThrow(NutriMealDatabase.COL_ID)));
                list.add(planner);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // ===================== FRIDGE =====================

    public void insertFridgeIngredient(int userId, String ingredientName, String dateAdded) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NutriMealDatabase.COL_USER_ID, userId);
        values.put(NutriMealDatabase.COL_INGREDIENT_NAME, ingredientName);
        values.put(NutriMealDatabase.COL_DATE_ADDED, dateAdded);
        db.insertWithOnConflict(NutriMealDatabase.TABLE_FRIDGE,
                null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public void deleteFridgeIngredient(int userId, String ingredientName) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(NutriMealDatabase.TABLE_FRIDGE,
                NutriMealDatabase.COL_USER_ID + "=? AND " +
                        NutriMealDatabase.COL_INGREDIENT_NAME + "=?",
                new String[]{String.valueOf(userId), ingredientName});
    }

    public List<String> getAllFridgeIngredients(int userId) {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(NutriMealDatabase.TABLE_FRIDGE,
                new String[]{NutriMealDatabase.COL_INGREDIENT_NAME},
                NutriMealDatabase.COL_USER_ID + "=?",
                new String[]{String.valueOf(userId)},
                null, null,
                NutriMealDatabase.COL_DATE_ADDED + " DESC");

        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}