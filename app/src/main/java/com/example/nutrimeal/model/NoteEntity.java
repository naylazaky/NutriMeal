package com.example.nutrimeal.model;

public class NoteEntity {
    private int id;
    private String mealId;
    private String mealName;
    private String noteText;
    private int starRating;
    private String dateModified;

    public NoteEntity(String mealId, String mealName, String noteText,
                      int starRating, String dateModified) {
        this.mealId = mealId;
        this.mealName = mealName;
        this.noteText = noteText;
        this.starRating = starRating;
        this.dateModified = dateModified;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getMealId() { return mealId; }
    public String getMealName() { return mealName; }
    public void setMealName(String mealName) { this.mealName = mealName; }
    public String getNoteText() { return noteText; }
    public void setNoteText(String noteText) { this.noteText = noteText; }
    public int getStarRating() { return starRating; }
    public void setStarRating(int starRating) { this.starRating = starRating; }
    public String getDateModified() { return dateModified; }
    public void setDateModified(String dateModified) { this.dateModified = dateModified; }
}