package com.example.nutrimeal.api;

import com.example.nutrimeal.model.CategoryResponse;
import com.example.nutrimeal.model.MealDetailResponse;
import com.example.nutrimeal.model.MealResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MealApiService {

    @GET("categories.php")
    Call<CategoryResponse> getCategories();

    @GET("search.php")
    Call<MealResponse> searchMeals(@Query("s") String name);

    @GET("filter.php")
    Call<MealResponse> filterByCategory(@Query("c") String category);

    @GET("filter.php")
    Call<MealResponse> filterByIngredient(@Query("i") String ingredient);

    @GET("lookup.php")
    Call<MealDetailResponse> getMealDetail(@Query("i") String id);

    @GET("random.php")
    Call<MealDetailResponse> getRandomMeal();
}