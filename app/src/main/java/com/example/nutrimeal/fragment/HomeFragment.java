package com.example.nutrimeal.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.nutrimeal.R;
import com.example.nutrimeal.adapter.CategoryAdapter;
import com.example.nutrimeal.adapter.MealAdapter;
import com.example.nutrimeal.model.Category;
import com.example.nutrimeal.model.Meal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private RecyclerView rvCategories, rvRecipes;
    private SwipeRefreshLayout swipeRefresh;
    private LinearLayout layoutOffline;
    private TextView tvGreeting, tvDate;

    private CategoryAdapter categoryAdapter;
    private MealAdapter mealAdapter;

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

        bindViews(view);
        setupGreeting();
        setupAdapters();
        loadDummyData();

        swipeRefresh.setColorSchemeColors(
                requireContext().getColor(R.color.accent));
        swipeRefresh.setOnRefreshListener(() -> {
            loadDummyData();
            swipeRefresh.setRefreshing(false);
        });
    }

    private void bindViews(View view) {
        rvCategories = view.findViewById(R.id.rv_categories);
        rvRecipes = view.findViewById(R.id.rv_recipes);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        layoutOffline = view.findViewById(R.id.layout_offline);
        tvGreeting = view.findViewById(R.id.tv_greeting);
        tvDate = view.findViewById(R.id.tv_date);
    }

    private void setupGreeting() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);

        String greeting;
        if (hour < 12) greeting = getString(R.string.good_morning);
        else if (hour < 17) greeting = getString(R.string.good_afternoon);
        else greeting = getString(R.string.good_evening);

        tvGreeting.setText(greeting);

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMMM", Locale.getDefault());
        tvDate.setText(sdf.format(cal.getTime()));
    }

    private void setupAdapters() {
        categoryAdapter = new CategoryAdapter(category -> {
        });
        rvCategories.setAdapter(categoryAdapter);

        mealAdapter = new MealAdapter(new MealAdapter.OnMealClickListener() {
            @Override
            public void onMealClick(Meal meal) {
            }
            @Override
            public void onFavoriteClick(Meal meal, int position) {
            }
        });
        rvRecipes.setAdapter(mealAdapter);
    }

    private void loadDummyData() {
        List<Category> categories = new ArrayList<>();
        categories.add(new Category("1", "Beef", ""));
        categories.add(new Category("2", "Chicken", ""));
        categories.add(new Category("3", "Seafood", ""));
        categories.add(new Category("4", "Pasta", ""));
        categories.add(new Category("5", "Dessert", ""));
        categoryAdapter.submitList(categories);

        List<Meal> meals = new ArrayList<>();
        meals.add(new Meal("1", "Spaghetti Bolognese", "Pasta", "Italian", ""));
        meals.add(new Meal("2", "Chicken Tikka Masala", "Chicken", "Indian", ""));
        meals.add(new Meal("3", "Beef Stroganoff", "Beef", "Russian", ""));
        meals.add(new Meal("4", "Pad Thai", "Pasta", "Thai", ""));
        mealAdapter.submitList(meals);
    }
}