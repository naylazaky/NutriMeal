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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutrimeal.R;
import com.example.nutrimeal.adapter.FavoritesAdapter;
import com.example.nutrimeal.adapter.PlannerAdapter;
import com.example.nutrimeal.database.MealDao;
import com.example.nutrimeal.model.FavoriteEntity;
import com.example.nutrimeal.model.PlannerEntity;
import com.example.nutrimeal.utils.SessionManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlannerFragment extends Fragment {

    private RecyclerView rvPlanner;
    private PlannerAdapter plannerAdapter;
    private MealDao mealDao;
    private SessionManager sessionManager;
    private int userId;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private final String[] DAYS = {
            "Monday", "Tuesday", "Wednesday",
            "Thursday", "Friday", "Saturday", "Sunday"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_planner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mealDao = new MealDao(requireContext());
        sessionManager = new SessionManager(requireContext());
        userId = sessionManager.getUserId();

        rvPlanner = view.findViewById(R.id.rv_planner);

        setupPlanner();
        loadPlannerData();
    }

    private void setupPlanner() {
        List<PlannerEntity> plannerList = new ArrayList<>();
        for (String day : DAYS) {
            plannerList.add(new PlannerEntity(day, null, null, null));
        }

        plannerAdapter = new PlannerAdapter(plannerList,
                new PlannerAdapter.OnPlannerClickListener() {
                    @Override
                    public void onDayClick(PlannerEntity planner, int position) {
                        showMealPickerBottomSheet(planner, position);
                    }

                    @Override
                    public void onDayLongClick(PlannerEntity planner, int position) {
                        showDeleteConfirmDialog(planner, position);
                    }
                });

        rvPlanner.setAdapter(plannerAdapter);
    }

    private void loadPlannerData() {
        executor.execute(() -> {
            List<PlannerEntity> savedPlanner = mealDao.getAllPlanner(userId);
            mainHandler.post(() -> {
                for (PlannerEntity saved : savedPlanner) {
                    for (int i = 0; i < DAYS.length; i++) {
                        if (DAYS[i].equals(saved.getDayOfWeek())) {
                            plannerAdapter.updateItem(i,
                                    saved.getMealId(),
                                    saved.getMealName(),
                                    saved.getMealThumb());
                            break;
                        }
                    }
                }
            });
        });
    }

    private void showMealPickerBottomSheet(PlannerEntity planner, int position) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View sheetView = LayoutInflater.from(requireContext())
                .inflate(R.layout.bottom_sheet_meal_picker, null);
        dialog.setContentView(sheetView);

        EditText etSearch = sheetView.findViewById(R.id.et_picker_search);
        RecyclerView rvPicker = sheetView.findViewById(R.id.rv_picker);

        FavoritesAdapter favAdapter = new FavoritesAdapter(fav -> {
            executor.execute(() -> {
                PlannerEntity updated = new PlannerEntity(
                        planner.getDayOfWeek(),
                        fav.getMealId(),
                        fav.getMealName(),
                        fav.getMealThumb());
                mealDao.insertOrUpdatePlanner(userId, updated);
                mainHandler.post(() -> {
                    plannerAdapter.updateItem(position,
                            fav.getMealId(),
                            fav.getMealName(),
                            fav.getMealThumb());
                    dialog.dismiss();
                });
            });
        });

        rvPicker.setAdapter(favAdapter);

        // Load favorites milik user ini saja
        executor.execute(() -> {
            List<FavoriteEntity> favs = mealDao.getAllFavorites(userId);
            mainHandler.post(() -> {
                if (!isAdded()) return;
                if (favs.isEmpty()) {
                    Toast.makeText(getContext(),
                            "No favorites yet. Add some first!",
                            Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    favAdapter.submitList(favs);
                }
            });
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence s, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable s) {
                favAdapter.filter(s.toString());
            }
        });

        dialog.show();
    }

    private void showDeleteConfirmDialog(PlannerEntity planner, int position) {
        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Remove Meal")
                .setMessage("Remove " + planner.getMealName() +
                        " from " + planner.getDayOfWeek() + "?")
                .setPositiveButton("Remove", (dialog, which) -> {
                    executor.execute(() -> {
                        mealDao.deletePlannerByDay(userId, planner.getDayOfWeek());
                        mainHandler.post(() -> plannerAdapter.clearItem(position));
                    });
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }
}