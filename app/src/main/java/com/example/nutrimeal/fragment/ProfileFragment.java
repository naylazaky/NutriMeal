package com.example.nutrimeal.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nutrimeal.R;
import com.example.nutrimeal.database.MealDao;
import com.example.nutrimeal.utils.ThemeUtils;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileFragment extends Fragment {

    private SwitchMaterial switchDarkMode;
    private LinearLayout rowFavorites, rowNotes;
    private TextView tvFavoritesCount, tvNotesCount;

    private MealDao mealDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mealDao = new MealDao(requireContext());

        bindViews(view);
        setupDarkModeToggle();
        setupRowClicks();
        loadCounts();
    }

    private void bindViews(View view) {
        switchDarkMode = view.findViewById(R.id.switch_dark_mode);
        rowFavorites = view.findViewById(R.id.row_favorites);
        rowNotes = view.findViewById(R.id.row_notes);
        tvFavoritesCount = view.findViewById(R.id.tv_favorites_count);
        tvNotesCount = view.findViewById(R.id.tv_notes_count);
    }

    private void setupDarkModeToggle() {
        switchDarkMode.setChecked(ThemeUtils.isDarkMode(requireContext()));
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) ->
                ThemeUtils.setDarkMode(requireContext(), isChecked));
    }

    private void setupRowClicks() {
        rowFavorites.setOnClickListener(v -> {
            androidx.navigation.Navigation.findNavController(requireView())
                    .navigate(R.id.action_profile_to_favorites);
        });

        rowNotes.setOnClickListener(v -> {
            androidx.navigation.Navigation.findNavController(requireView())
                    .navigate(R.id.action_profile_to_notes);
        });
    }

    private void loadCounts() {
        executor.execute(() -> {
            int favCount = mealDao.getAllFavorites().size();
            int notesCount = mealDao.getAllNotes().size();
            mainHandler.post(() -> {
                if (isAdded()) {
                    tvFavoritesCount.setText(favCount + " saved");
                    tvNotesCount.setText(notesCount + " notes");
                }
            });
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCounts();
    }
}