package com.example.nutrimeal.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nutrimeal.R;
import com.example.nutrimeal.adapter.IngredientAdapter;
import com.example.nutrimeal.api.ApiClient;
import com.example.nutrimeal.database.MealDao;
import com.example.nutrimeal.model.FavoriteEntity;
import com.example.nutrimeal.model.MealDetail;
import com.example.nutrimeal.model.MealResponse;
import com.example.nutrimeal.model.NoteEntity;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DetailFragment extends Fragment {

    private ImageView ivHero, btnBack, btnFavorite, btnNote, btnShare;
    private TextView tvMealName, tvCategory, tvArea;
    private TabLayout tabLayout;
    private FrameLayout tabContent;
    private LinearLayout layoutStars;

    private MealDetail currentMeal;
    private MealDao mealDao;
    private boolean isFavorite = false;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mealDao = new MealDao(requireContext());

        bindViews(view);

        String mealId = getArguments() != null ? getArguments().getString("mealId") : "";
        loadMealDetail(mealId);

        btnBack.setOnClickListener(v ->
                requireActivity().getOnBackPressedDispatcher().onBackPressed());
    }

    private void bindViews(View view) {
        ivHero = view.findViewById(R.id.iv_hero);
        btnBack = view.findViewById(R.id.btn_back);
        btnFavorite = view.findViewById(R.id.btn_favorite);
        btnNote = view.findViewById(R.id.btn_note);
        btnShare = view.findViewById(R.id.btn_share);
        tvMealName = view.findViewById(R.id.tv_meal_name);
        tvCategory = view.findViewById(R.id.tv_category);
        tvArea = view.findViewById(R.id.tv_area);
        tabLayout = view.findViewById(R.id.tab_layout);
        tabContent = view.findViewById(R.id.tab_content);
        layoutStars = view.findViewById(R.id.layout_stars);
    }

    private void loadMealDetail(String mealId) {
        ApiClient.getMealApiService().getMealDetail(mealId)
                .enqueue(new retrofit2.Callback<MealResponse>() {
                    @Override
                    public void onResponse(@NonNull retrofit2.Call<MealResponse> call,
                                           @NonNull retrofit2.Response<MealResponse> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().getMeals() != null) {
                            // MealResponse returns List<Meal> but detail has MealDetail fields
                            // We cast via Gson by using MealDetail directly
                            com.google.gson.Gson gson = new com.google.gson.Gson();
                            String json = gson.toJson(response.body().getMeals().get(0));
                            MealDetail meal = gson.fromJson(json, MealDetail.class);
                            currentMeal = meal;
                            mainHandler.post(() -> populateUI(meal));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull retrofit2.Call<MealResponse> call,
                                          @NonNull Throwable t) {
                        mainHandler.post(() -> Toast.makeText(getContext(),
                                "Failed to load detail", Toast.LENGTH_SHORT).show());
                    }
                });
    }

    private void populateUI(MealDetail meal) {
        tvMealName.setText(meal.getStrMeal());
        tvCategory.setText(meal.getStrCategory());
        tvArea.setText(meal.getStrArea());

        Glide.with(this)
                .load(meal.getStrMealThumb())
                .centerCrop()
                .into(ivHero);

        executor.execute(() -> {
            isFavorite = mealDao.isFavorite(meal.getIdMeal());
            mainHandler.post(() -> btnFavorite.setImageResource(
                    isFavorite ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline));
        });

        loadStarRating(meal.getIdMeal());
        setupTabs(meal);
        setupActions(meal);
    }

    private void loadStarRating(String mealId) {
        executor.execute(() -> {
            NoteEntity note = mealDao.getNoteByMealId(mealId);
            int rating = note != null ? note.getStarRating() : 0;
            mainHandler.post(() -> {
                layoutStars.removeAllViews();
                for (int i = 1; i <= 5; i++) {
                    ImageView star = new ImageView(requireContext());
                    LinearLayout.LayoutParams params =
                            new LinearLayout.LayoutParams(48, 48);
                    params.setMarginEnd(4);
                    star.setLayoutParams(params);
                    star.setImageResource(i <= rating ?
                            R.drawable.ic_star_filled : R.drawable.ic_star_outline);
                    layoutStars.addView(star);
                }
            });
        });
    }

    private void setupTabs(MealDetail meal) {
        tabLayout.removeAllTabs();
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_ingredients)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_instructions)));

        showIngredientsTab(meal);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: showIngredientsTab(meal); break;
                    case 1: showInstructionsTab(meal); break;
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void showIngredientsTab(MealDetail meal) {
        tabContent.removeAllViews();
        RecyclerView rv = new RecyclerView(requireContext());
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setNestedScrollingEnabled(false);
        IngredientAdapter adapter = new IngredientAdapter();
        rv.setAdapter(adapter);
        adapter.submitList(meal.getIngredients());
        tabContent.addView(rv);
    }

    private void showInstructionsTab(MealDetail meal) {
        tabContent.removeAllViews();
        TextView tv = new TextView(requireContext());
        tv.setText(meal.getStrInstructions());
        tv.setTextSize(14);
        tv.setLineSpacing(6, 1);
        tv.setTextColor(requireContext().getColor(R.color.text_primary_light));
        tabContent.addView(tv);
    }

    private void setupActions(MealDetail meal) {
        btnFavorite.setOnClickListener(v -> {
            executor.execute(() -> {
                if (isFavorite) {
                    mealDao.deleteFavorite(meal.getIdMeal());
                    isFavorite = false;
                } else {
                    String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                            Locale.getDefault()).format(new Date());
                    FavoriteEntity fav = new FavoriteEntity(
                            meal.getIdMeal(), meal.getStrMeal(),
                            meal.getStrMealThumb(), meal.getStrCategory(),
                            meal.getStrArea(), meal.getStrInstructions(), date);
                    mealDao.insertFavorite(fav);
                    isFavorite = true;
                }
                mainHandler.post(() -> {
                    btnFavorite.setImageResource(isFavorite ?
                            R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
                    Toast.makeText(getContext(),
                            isFavorite ? "Added to favorites" : "Removed from favorites",
                            Toast.LENGTH_SHORT).show();
                });
            });
        });

        btnNote.setOnClickListener(v -> showNoteDialog(meal.getIdMeal()));

        btnShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, meal.getStrMeal());
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    "Check out this recipe: " + meal.getStrMeal() +
                            "\n\n" + meal.getStrInstructions());
            startActivity(Intent.createChooser(shareIntent,
                    getString(R.string.share_recipe)));
        });
    }

    private void showNoteDialog(String mealId) {
        android.app.AlertDialog.Builder builder =
                new android.app.AlertDialog.Builder(requireContext());
        builder.setTitle("My Note");

        android.widget.EditText editText = new android.widget.EditText(requireContext());
        editText.setHint("Write your note here...");
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        editText.setPadding(pad, pad, pad, pad);

        executor.execute(() -> {
            NoteEntity existing = mealDao.getNoteByMealId(mealId);
            mainHandler.post(() -> {
                if (existing != null) editText.setText(existing.getNoteText());
            });
        });

        builder.setView(editText);
        builder.setPositiveButton(getString(R.string.save_note), (dialog, which) -> {
            String noteText = editText.getText().toString().trim();
            executor.execute(() -> {
                String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                        Locale.getDefault()).format(new Date());
                NoteEntity note = new NoteEntity(mealId, noteText, 0, date);
                mealDao.insertOrUpdateNote(note);
                mainHandler.post(() -> Toast.makeText(getContext(),
                        "Note saved", Toast.LENGTH_SHORT).show());
            });
        });
        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.show();
    }
}