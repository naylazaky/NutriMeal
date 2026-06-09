package com.example.nutrimeal.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MealDetailResponse {
    @SerializedName("meals")
    private List<MealDetail> meals;

    public List<MealDetail> getMeals() { return meals; }
}