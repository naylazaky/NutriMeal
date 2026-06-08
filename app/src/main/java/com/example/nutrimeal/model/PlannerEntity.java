package com.example.nutrimeal.model;

public class PlannerEntity {
    private int id;
    private String dayOfWeek;
    private String mealId;
    private String mealName;
    private String mealThumb;

    public PlannerEntity(String dayOfWeek, String mealId, String mealName, String mealThumb) {
        this.dayOfWeek = dayOfWeek;
        this.mealId = mealId;
        this.mealName = mealName;
        this.mealThumb = mealThumb;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public String getMealId() {
        return mealId;
    }

    public String getMealName() {
        return mealName;
    }

    public String getMealThumb() {
        return mealThumb;
    }

    public void setMealId(String mealId) {
        this.mealId = mealId;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public void setMealThumb(String mealThumb) {
        this.mealThumb = mealThumb;
    }
}