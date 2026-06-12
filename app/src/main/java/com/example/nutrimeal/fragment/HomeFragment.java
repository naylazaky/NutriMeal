package com.example.nutrimeal.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.nutrimeal.R;
import com.example.nutrimeal.adapter.CategoryAdapter;
import com.example.nutrimeal.adapter.MealAdapter;
import com.example.nutrimeal.api.ApiClient;
import com.example.nutrimeal.database.MealDao;
import com.example.nutrimeal.model.Category;
import com.example.nutrimeal.model.CategoryResponse;
import com.example.nutrimeal.model.FavoriteEntity;
import com.example.nutrimeal.model.Meal;
import com.example.nutrimeal.model.MealResponse;
import com.example.nutrimeal.utils.NetworkUtils;
import com.example.nutrimeal.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView rvCategories, rvRecipes;
    private SwipeRefreshLayout swipeRefresh;
    private LinearLayout layoutOffline;
    private TextView tvGreeting, tvDate;
    private ImageView ivProfilePhoto;

    private CategoryAdapter categoryAdapter;
    private MealAdapter mealAdapter;
    private MealDao mealDao;
    private SessionManager sessionManager;
    private int userId;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final String[] ALL_KEYWORDS = {
            "beef", "chicken", "salmon", "pasta",
            "lamb", "potato", "prawn"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mealDao = new MealDao(requireContext());
        sessionManager = new SessionManager(requireContext());
        userId = sessionManager.getUserId();

        bindViews(view);
        setupGreeting();
        setupAdapters();
        checkNetworkAndLoad();

        swipeRefresh.setColorSchemeColors(
                requireContext().getColor(R.color.accent));
        swipeRefresh.setOnRefreshListener(this::checkNetworkAndLoad);
        view.findViewById(R.id.btn_refresh).setOnClickListener(v -> checkNetworkAndLoad());
    }

    private void bindViews(View view) {
        rvCategories = view.findViewById(R.id.rv_categories);
        rvRecipes = view.findViewById(R.id.rv_recipes);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        layoutOffline = view.findViewById(R.id.layout_offline);
        tvGreeting = view.findViewById(R.id.tv_greeting);
        tvDate = view.findViewById(R.id.tv_date);
        ivProfilePhoto = view.findViewById(R.id.iv_profile_photo);
    }

    private void setupGreeting() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);

        String greeting;
        if (hour < 12) greeting = getString(R.string.good_morning);
        else if (hour < 17) greeting = getString(R.string.good_afternoon);
        else greeting = getString(R.string.good_evening);

        String name = sessionManager.getUserName();
        if (name != null && !name.isEmpty()) {
            tvGreeting.setText(greeting + ", " + name);
        } else {
            tvGreeting.setText(greeting);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMMM", Locale.getDefault());
        tvDate.setText(sdf.format(cal.getTime()));

        String photoPath = sessionManager.getPhotoPath();
        if (photoPath != null && !photoPath.isEmpty() && ivProfilePhoto != null) {
            Glide.with(this)
                    .load(Uri.parse(photoPath))
                    .centerCrop()
                    .circleCrop()
                    .into(ivProfilePhoto);
        }
    }

    private void setupAdapters() {
        categoryAdapter = new CategoryAdapter(category -> {
            if (category.getIdCategory().equals("0")) {
                loadMixedRecipes();
            } else {
                loadRecipesByCategory(category.getStrCategory());
            }
        });
        rvCategories.setAdapter(categoryAdapter);

        mealAdapter = new MealAdapter(new MealAdapter.OnMealClickListener() {
            @Override
            public void onMealClick(Meal meal) {
                Bundle args = new Bundle();
                args.putString("mealId", meal.getIdMeal());
                androidx.navigation.Navigation.findNavController(requireView())
                        .navigate(R.id.action_home_to_detail, args);
            }

            @Override
            public void onFavoriteClick(Meal meal, int position) {
                executor.execute(() -> {
                    boolean isFav = mealDao.isFavorite(userId, meal.getIdMeal());
                    if (isFav) {
                        mealDao.deleteFavorite(userId, meal.getIdMeal());
                        meal.setFavorite(false);
                    } else {
                        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                                Locale.getDefault()).format(new Date());
                        FavoriteEntity fav = new FavoriteEntity(
                                meal.getIdMeal(),
                                meal.getStrMeal(),
                                meal.getStrMealThumb(),
                                meal.getStrCategory() != null ? meal.getStrCategory() : "",
                                meal.getStrArea() != null ? meal.getStrArea() : "",
                                "",
                                date);
                        mealDao.insertFavorite(userId, fav);
                        meal.setFavorite(true);
                    }
                    mainHandler.post(() -> {
                        if (isAdded() && getContext() != null) {
                            mealAdapter.notifyItemChanged(position);
                            Toast.makeText(getContext(),
                                    meal.isFavorite() ? "Added to favorites"
                                            : "Removed from favorites",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            }
        });
        rvRecipes.setAdapter(mealAdapter);
    }

    private void checkNetworkAndLoad() {
        if (NetworkUtils.isNetworkAvailable(requireContext())) {
            layoutOffline.setVisibility(View.GONE);
            loadCategories();
            loadMixedRecipes();
        } else {
            layoutOffline.setVisibility(View.VISIBLE);
            swipeRefresh.setRefreshing(false);
        }
    }

    private void loadMixedRecipes() {
        swipeRefresh.setRefreshing(true);
        final List<Meal> mixedList = new ArrayList<>();

        List<String> keywords = new ArrayList<>();
        Collections.addAll(keywords, ALL_KEYWORDS);
        Collections.shuffle(keywords);
        List<String> selected = keywords.subList(0, 4);

        AtomicInteger counter = new AtomicInteger(0);
        int total = selected.size();

        for (String keyword : selected) {
            ApiClient.getMealApiService().searchMeals(keyword)
                    .enqueue(new Callback<MealResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<MealResponse> call,
                                               @NonNull Response<MealResponse> response) {
                            if (response.isSuccessful() && response.body() != null
                                    && response.body().getMeals() != null) {
                                List<Meal> meals = response.body().getMeals();
                                int take = Math.min(3, meals.size());
                                synchronized (mixedList) {
                                    mixedList.addAll(meals.subList(0, take));
                                }
                            }
                            if (counter.incrementAndGet() == total) {
                                executor.execute(() -> {
                                    Collections.shuffle(mixedList);
                                    for (Meal meal : mixedList) {
                                        meal.setFavorite(mealDao.isFavorite(
                                                userId, meal.getIdMeal()));
                                    }
                                    mainHandler.post(() -> {
                                        if (isAdded()) {
                                            swipeRefresh.setRefreshing(false);
                                            mealAdapter.submitList(new ArrayList<>(mixedList));
                                        }
                                    });
                                });
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<MealResponse> call,
                                              @NonNull Throwable t) {
                            if (counter.incrementAndGet() == total) {
                                mainHandler.post(() -> {
                                    if (isAdded()) swipeRefresh.setRefreshing(false);
                                });
                            }
                        }
                    });
        }
    }

    private void loadCategories() {
        ApiClient.getMealApiService().getCategories()
                .enqueue(new Callback<CategoryResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<CategoryResponse> call,
                                           @NonNull Response<CategoryResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Category> list = response.body().getCategories();
                            if (list != null && isAdded()) {
                                mainHandler.post(() -> categoryAdapter.submitList(list));
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<CategoryResponse> call,
                                          @NonNull Throwable t) {}
                });
    }

    private void loadRecipesByCategory(String category) {
        swipeRefresh.setRefreshing(true);
        ApiClient.getMealApiService().filterByCategory(category)
                .enqueue(new Callback<MealResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<MealResponse> call,
                                           @NonNull Response<MealResponse> response) {
                        if (!isAdded()) return;
                        swipeRefresh.setRefreshing(false);
                        if (response.isSuccessful() && response.body() != null) {
                            List<Meal> list = response.body().getMeals();
                            if (list != null) {
                                for (Meal meal : list) {
                                    meal.setStrCategory(category);
                                }
                                executor.execute(() -> {
                                    for (Meal meal : list) {
                                        meal.setFavorite(mealDao.isFavorite(
                                                userId, meal.getIdMeal()));
                                    }
                                    mainHandler.post(() -> {
                                        if (isAdded()) mealAdapter.submitList(list);
                                    });
                                });
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<MealResponse> call,
                                          @NonNull Throwable t) {
                        mainHandler.post(() -> {
                            if (!isAdded()) return;
                            swipeRefresh.setRefreshing(false);
                        });
                    }
                });
    }
}