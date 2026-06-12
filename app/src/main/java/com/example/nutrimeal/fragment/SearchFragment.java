package com.example.nutrimeal.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutrimeal.R;
import com.example.nutrimeal.adapter.MealAdapter;
import com.example.nutrimeal.api.ApiClient;
import com.example.nutrimeal.model.Meal;
import com.example.nutrimeal.model.MealResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private MaterialButtonToggleGroup toggleMode;
    private LinearLayout layoutSearchMode, layoutFridgeMode;
    private EditText etSearch, etIngredient;
    private MaterialButton btnFindRecipes;
    private RecyclerView rvResults;

    private MealAdapter mealAdapter;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private static final List<Meal> savedResults = new ArrayList<>();
    private static String savedSearchQuery = "";
    private static String savedIngredientQuery = "";

    private boolean isRestoringState = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindViews(view);
        setupAdapter();
        setupToggle();
        setupSearchMode();
        setupFridgeMode();

        isRestoringState = true;
        if (!savedSearchQuery.isEmpty()) {
            etSearch.setText(savedSearchQuery);
            etSearch.setSelection(savedSearchQuery.length());
        }
        if (!savedIngredientQuery.isEmpty()) {
            etIngredient.setText(savedIngredientQuery);
            etIngredient.setSelection(savedIngredientQuery.length());
        }
        if (!savedResults.isEmpty()) {
            mealAdapter.submitList(new ArrayList<>(savedResults));
        }
        isRestoringState = false;
    }

    private void bindViews(View view) {
        toggleMode = view.findViewById(R.id.toggle_mode);
        layoutSearchMode = view.findViewById(R.id.layout_search_mode);
        layoutFridgeMode = view.findViewById(R.id.layout_fridge_mode);
        etSearch = view.findViewById(R.id.et_search);
        etIngredient = view.findViewById(R.id.et_ingredient);
        btnFindRecipes = view.findViewById(R.id.btn_find_recipes);
        rvResults = view.findViewById(R.id.rv_search_results);
    }

    private void setupAdapter() {
        mealAdapter = new MealAdapter(new MealAdapter.OnMealClickListener() {
            @Override
            public void onMealClick(Meal meal) {
                Bundle args = new Bundle();
                args.putString("mealId", meal.getIdMeal());
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_search_to_detail, args);
            }

            @Override
            public void onFavoriteClick(Meal meal, int position) {
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(),
                            "Added to favorites", Toast.LENGTH_SHORT).show();
                }
            }
        });
        rvResults.setAdapter(mealAdapter);
    }

    private void setupToggle() {
        toggleMode.check(R.id.btn_search_mode);
        toggleMode.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) return;
            if (checkedId == R.id.btn_search_mode) {
                layoutSearchMode.setVisibility(View.VISIBLE);
                layoutFridgeMode.setVisibility(View.GONE);
            } else {
                layoutSearchMode.setVisibility(View.GONE);
                layoutFridgeMode.setVisibility(View.VISIBLE);
            }

            savedResults.clear();
            savedSearchQuery = "";
            savedIngredientQuery = "";
            mealAdapter.submitList(new ArrayList<>());
        });
    }

    private void setupSearchMode() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isRestoringState) return;
                String query = s.toString().trim();
                savedSearchQuery = query;
                if (query.length() >= 2) {
                    searchRecipes(query);
                } else if (query.isEmpty()) {
                    savedResults.clear();
                    mealAdapter.submitList(new ArrayList<>());
                }
            }
        });
    }

    private void setupFridgeMode() {
        btnFindRecipes.setOnClickListener(v -> {
            String ingredient = etIngredient.getText().toString().trim();
            if (ingredient.isEmpty()) {
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(),
                            "Enter an ingredient first", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            savedIngredientQuery = ingredient;
            searchByIngredient(ingredient);
        });

        etIngredient.setOnEditorActionListener((v, actionId, event) -> {
            String ingredient = etIngredient.getText().toString().trim();
            if (!ingredient.isEmpty()) {
                savedIngredientQuery = ingredient;
                searchByIngredient(ingredient);
            }
            return true;
        });
    }

    private void searchRecipes(String query) {
        ApiClient.getMealApiService().searchMeals(query)
                .enqueue(new Callback<MealResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<MealResponse> call,
                                           @NonNull Response<MealResponse> response) {
                        if (!isAdded()) return;
                        if (response.isSuccessful() && response.body() != null) {
                            List<Meal> meals = response.body().getMeals();
                            List<Meal> result = meals != null ? meals : new ArrayList<>();
                            savedResults.clear();
                            savedResults.addAll(result);
                            mainHandler.post(() -> {
                                if (isAdded()) mealAdapter.submitList(new ArrayList<>(result));
                            });
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<MealResponse> call, @NonNull Throwable t) {
                        mainHandler.post(() -> {
                            if (isAdded() && getContext() != null)
                                Toast.makeText(getContext(), "Search failed", Toast.LENGTH_SHORT).show();
                        });
                    }
                });
    }

    private void searchByIngredient(String ingredient) {
        ApiClient.getMealApiService().filterByIngredient(ingredient)
                .enqueue(new Callback<MealResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<MealResponse> call,
                                           @NonNull Response<MealResponse> response) {
                        if (!isAdded()) return;
                        if (response.isSuccessful() && response.body() != null) {
                            List<Meal> meals = response.body().getMeals();
                            List<Meal> result = meals != null ? meals : new ArrayList<>();
                            savedResults.clear();
                            savedResults.addAll(result);
                            mainHandler.post(() -> {
                                if (isAdded()) {
                                    mealAdapter.submitList(new ArrayList<>(result));
                                    if (result.isEmpty() && getContext() != null) {
                                        Toast.makeText(getContext(),
                                                "No recipes found", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<MealResponse> call, @NonNull Throwable t) {
                        mainHandler.post(() -> {
                            if (isAdded() && getContext() != null)
                                Toast.makeText(getContext(), "Search failed", Toast.LENGTH_SHORT).show();
                        });
                    }
                });
    }
}