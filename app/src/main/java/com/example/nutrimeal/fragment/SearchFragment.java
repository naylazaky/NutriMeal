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
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private MaterialButtonToggleGroup toggleMode;
    private LinearLayout layoutSearchMode, layoutFridgeMode;
    private EditText etSearch, etIngredient;
    private MaterialButton btnAddIngredient, btnFindRecipes;
    private ChipGroup chipGroupIngredients;
    private RecyclerView rvResults;

    private MealAdapter mealAdapter;
    private final List<String> fridgeIngredients = new ArrayList<>();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

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
    }

    private void bindViews(View view) {
        toggleMode = view.findViewById(R.id.toggle_mode);
        layoutSearchMode = view.findViewById(R.id.layout_search_mode);
        layoutFridgeMode = view.findViewById(R.id.layout_fridge_mode);
        etSearch = view.findViewById(R.id.et_search);
        etIngredient = view.findViewById(R.id.et_ingredient);
        btnAddIngredient = view.findViewById(R.id.btn_add_ingredient);
        btnFindRecipes = view.findViewById(R.id.btn_find_recipes);
        chipGroupIngredients = view.findViewById(R.id.chip_group_ingredients);
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
        });
    }

    private void setupSearchMode() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                if (query.length() >= 2) {
                    searchRecipes(query);
                } else if (query.isEmpty()) {
                    mealAdapter.submitList(new ArrayList<>());
                }
            }
        });
    }

    private void setupFridgeMode() {
        btnAddIngredient.setOnClickListener(v -> {
            String ingredient = etIngredient.getText().toString().trim();
            if (!ingredient.isEmpty() && !fridgeIngredients.contains(ingredient)) {
                fridgeIngredients.add(ingredient);
                addChip(ingredient);
                etIngredient.setText("");
            }
        });

        btnFindRecipes.setOnClickListener(v -> {
            if (fridgeIngredients.isEmpty()) {
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(),
                            "Add at least one ingredient", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            searchByIngredient(fridgeIngredients.get(0));
        });
    }

    private void addChip(String ingredient) {
        Chip chip = new Chip(requireContext());
        chip.setText(ingredient);
        chip.setCloseIconVisible(true);
        chip.setChipBackgroundColorResource(R.color.surface_light);
        chip.setOnCloseIconClickListener(v -> {
            fridgeIngredients.remove(ingredient);
            chipGroupIngredients.removeView(chip);
        });
        chipGroupIngredients.addView(chip);
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
                            mainHandler.post(() -> {
                                if (isAdded()) {
                                    mealAdapter.submitList(meals != null ? meals : new ArrayList<>());
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<MealResponse> call, @NonNull Throwable t) {
                        mainHandler.post(() -> {
                            if (isAdded() && getContext() != null) {
                                Toast.makeText(getContext(),
                                        "Search failed", Toast.LENGTH_SHORT).show();
                            }
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
                            mainHandler.post(() -> {
                                if (isAdded()) {
                                    mealAdapter.submitList(meals != null ? meals : new ArrayList<>());
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<MealResponse> call, @NonNull Throwable t) {
                        mainHandler.post(() -> {
                            if (isAdded() && getContext() != null) {
                                Toast.makeText(getContext(),
                                        "Search failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
    }
}