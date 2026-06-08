package com.example.nutrimeal.model;

public class FavoriteEntity {
    private int id;
    private String mealId;
    private String mealName;
    private String mealThumb;
    private String category;
    private String area;
    private String instructions;
    private String dateAdded;

    public FavoriteEntity(String mealId, String mealName, String mealThumb,
                          String category, String area, String instructions, String dateAdded) {
        this.mealId = mealId;
        this.mealName = mealName;
        this.mealThumb = mealThumb;
        this.category = category;
        this.area = area;
        this.instructions = instructions;
        this.dateAdded = dateAdded;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getCategory() {
        return category;
    }

    public String getArea() {
        return area;
    }

    public String getInstructions() {
        return instructions;
    }

    public String getDateAdded() {
        return dateAdded;
    }
}