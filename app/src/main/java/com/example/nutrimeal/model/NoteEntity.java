package com.example.nutrimeal.model;

public class NoteEntity {
    private int id;
    private String mealId;
    private String noteText;
    private int starRating;
    private String dateModified;

    public NoteEntity(String mealId, String noteText, int starRating, String dateModified) {
        this.mealId = mealId;
        this.noteText = noteText;
        this.starRating = starRating;
        this.dateModified = dateModified;
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

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public int getStarRating() {
        return starRating;
    }

    public void setStarRating(int starRating) {
        this.starRating = starRating;
    }

    public String getDateModified() {
        return dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }
}