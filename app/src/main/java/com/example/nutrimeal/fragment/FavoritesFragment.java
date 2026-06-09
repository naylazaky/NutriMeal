package com.example.nutrimeal.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutrimeal.R;
import com.example.nutrimeal.adapter.FavoritesAdapter;
import com.example.nutrimeal.database.MealDao;
import com.example.nutrimeal.model.FavoriteEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavoritesFragment extends Fragment {

    private RecyclerView rvFavorites;
    private TextView tvEmpty;
    private FavoritesAdapter adapter;
    private MealDao mealDao;
    private List<FavoriteEntity> favoritesList;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mealDao = new MealDao(requireContext());
        rvFavorites = view.findViewById(R.id.rv_favorites);
        tvEmpty = view.findViewById(R.id.tv_empty);

        view.findViewById(R.id.btn_back).setOnClickListener(v ->
                requireActivity().getOnBackPressedDispatcher().onBackPressed());

        setupAdapter();
        loadFavorites();
    }

    private void setupAdapter() {
        adapter = new FavoritesAdapter(fav -> {
            Bundle args = new Bundle();
            args.putString("mealId", fav.getMealId());
            androidx.navigation.Navigation.findNavController(requireView())
                    .navigate(R.id.action_favorites_to_detail, args);
        });

        rvFavorites.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvFavorites.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView rv,
                                  @NonNull RecyclerView.ViewHolder vh,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder vh, int direction) {
                int position = vh.getAdapterPosition();
                FavoriteEntity fav = favoritesList.get(position);
                executor.execute(() -> {
                    mealDao.deleteFavorite(fav.getMealId());
                    mainHandler.post(() -> {
                        favoritesList.remove(position);
                        adapter.submitList(favoritesList);
                        if (isAdded() && getContext() != null) {
                            Toast.makeText(getContext(),
                                    "Removed from favorites",
                                    Toast.LENGTH_SHORT).show();
                        }
                        tvEmpty.setVisibility(
                                favoritesList.isEmpty() ? View.VISIBLE : View.GONE);
                    });
                });
            }
        }).attachToRecyclerView(rvFavorites);
    }

    private void loadFavorites() {
        executor.execute(() -> {
            favoritesList = mealDao.getAllFavorites();
            mainHandler.post(() -> {
                if (isAdded()) {
                    adapter.submitList(favoritesList);
                    tvEmpty.setVisibility(
                            favoritesList.isEmpty() ? View.VISIBLE : View.GONE);
                }
            });
        });
    }
}