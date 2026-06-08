package com.example.nutrimeal.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String MEAL_BASE_URL = "https://www.themealdb.com/api/json/v1/1/";
    private static Retrofit mealRetrofit = null;

    public static Retrofit getMealClient() {
        if (mealRetrofit == null) {
            mealRetrofit = new Retrofit.Builder()
                    .baseUrl(MEAL_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return mealRetrofit;
    }

    public static MealApiService getMealApiService() {
        return getMealClient().create(MealApiService.class);
    }
}