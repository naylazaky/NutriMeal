package com.example.nutrimeal.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NutriMealDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "nutrimeal.db";
    private static final int DATABASE_VERSION = 3;

    private static NutriMealDatabase instance;

    public static final String TABLE_FAVORITES = "favorites";
    public static final String TABLE_NOTES = "personal_notes";
    public static final String TABLE_PLANNER = "meal_planner";
    public static final String TABLE_FRIDGE = "fridge_ingredients";
    public static final String TABLE_NUTRITION_CACHE = "nutrition_cache";
    public static final String TABLE_USERS = "users";

    public static final String COL_ID = "id";
    public static final String COL_USER_ID = "user_id";
    public static final String COL_MEAL_ID = "meal_id";
    public static final String COL_MEAL_NAME = "meal_name";
    public static final String COL_MEAL_THUMB = "meal_thumb";
    public static final String COL_CATEGORY = "category";
    public static final String COL_AREA = "area";
    public static final String COL_INSTRUCTIONS = "instructions";
    public static final String COL_DATE_ADDED = "date_added";
    public static final String COL_NOTE_TEXT = "note_text";
    public static final String COL_STAR_RATING = "star_rating";
    public static final String COL_DATE_MODIFIED = "date_modified";
    public static final String COL_DAY_OF_WEEK = "day_of_week";
    public static final String COL_INGREDIENT_NAME = "ingredient_name";
    public static final String COL_CALORIES = "calories";
    public static final String COL_FAT = "fat";
    public static final String COL_CARBS = "carbs";
    public static final String COL_PROTEIN = "protein";
    public static final String COL_CACHED_AT = "cached_at";
    public static final String COL_NAME = "name";
    public static final String COL_USERNAME = "username";
    public static final String COL_PASSWORD = "password";
    public static final String COL_PHOTO_PATH = "photo_path";

    private static final String CREATE_USERS =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_NAME + " TEXT, " +
                    COL_USERNAME + " TEXT UNIQUE, " +
                    COL_PASSWORD + " TEXT, " +
                    COL_PHOTO_PATH + " TEXT)";

    private static final String CREATE_FAVORITES =
            "CREATE TABLE " + TABLE_FAVORITES + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_USER_ID + " INTEGER, " +
                    COL_MEAL_ID + " TEXT, " +
                    COL_MEAL_NAME + " TEXT, " +
                    COL_MEAL_THUMB + " TEXT, " +
                    COL_CATEGORY + " TEXT, " +
                    COL_AREA + " TEXT, " +
                    COL_INSTRUCTIONS + " TEXT, " +
                    COL_DATE_ADDED + " TEXT, " +
                    "UNIQUE(" + COL_USER_ID + ", " + COL_MEAL_ID + "))";

    private static final String CREATE_NOTES =
            "CREATE TABLE " + TABLE_NOTES + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_USER_ID + " INTEGER, " +
                    COL_MEAL_ID + " TEXT, " +
                    COL_NOTE_TEXT + " TEXT, " +
                    COL_STAR_RATING + " INTEGER DEFAULT 0, " +
                    COL_DATE_MODIFIED + " TEXT, " +
                    "UNIQUE(" + COL_USER_ID + ", " + COL_MEAL_ID + "))";

    private static final String CREATE_PLANNER =
            "CREATE TABLE " + TABLE_PLANNER + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_USER_ID + " INTEGER, " +
                    COL_DAY_OF_WEEK + " TEXT, " +
                    COL_MEAL_ID + " TEXT, " +
                    COL_MEAL_NAME + " TEXT, " +
                    COL_MEAL_THUMB + " TEXT, " +
                    "UNIQUE(" + COL_USER_ID + ", " + COL_DAY_OF_WEEK + "))";

    private static final String CREATE_FRIDGE =
            "CREATE TABLE " + TABLE_FRIDGE + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_USER_ID + " INTEGER, " +
                    COL_INGREDIENT_NAME + " TEXT, " +
                    COL_DATE_ADDED + " TEXT, " +
                    "UNIQUE(" + COL_USER_ID + ", " + COL_INGREDIENT_NAME + "))";

    private static final String CREATE_NUTRITION_CACHE =
            "CREATE TABLE " + TABLE_NUTRITION_CACHE + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_INGREDIENT_NAME + " TEXT UNIQUE, " +
                    COL_CALORIES + " REAL, " +
                    COL_FAT + " REAL, " +
                    COL_CARBS + " REAL, " +
                    COL_PROTEIN + " REAL, " +
                    COL_CACHED_AT + " TEXT)";

    public static synchronized NutriMealDatabase getInstance(Context context) {
        if (instance == null) {
            instance = new NutriMealDatabase(context.getApplicationContext());
        }
        return instance;
    }

    private NutriMealDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USERS);
        db.execSQL(CREATE_FAVORITES);
        db.execSQL(CREATE_NOTES);
        db.execSQL(CREATE_PLANNER);
        db.execSQL(CREATE_FRIDGE);
        db.execSQL(CREATE_NUTRITION_CACHE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLANNER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIDGE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NUTRITION_CACHE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }
}