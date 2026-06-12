package com.example.nutrimeal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nutrimeal.R;
import com.example.nutrimeal.model.Meal;

import java.util.ArrayList;
import java.util.List;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.ViewHolder> {

    public interface OnMealClickListener {
        void onMealClick(Meal meal);
        void onFavoriteClick(Meal meal, int position);
    }

    private List<Meal> meals = new ArrayList<>();
    private OnMealClickListener listener;

    public MealAdapter(OnMealClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<Meal> newList) {
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override public int getOldListSize() { return meals.size(); }
            @Override public int getNewListSize() { return newList.size(); }
            @Override public boolean areItemsTheSame(int oldPos, int newPos) {
                return meals.get(oldPos).getIdMeal()
                        .equals(newList.get(newPos).getIdMeal());
            }
            @Override public boolean areContentsTheSame(int oldPos, int newPos) {
                return meals.get(oldPos).getStrMeal()
                        .equals(newList.get(newPos).getStrMeal());
            }
        });
        meals = new ArrayList<>(newList);
        result.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(meals.get(position));
    }

    @Override
    public int getItemCount() { return meals.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumb, ivFavorite;
        TextView tvName, tvCategory, tvArea;

        ViewHolder(View itemView) {
            super(itemView);
            ivThumb = itemView.findViewById(R.id.iv_meal_thumb);
            ivFavorite = itemView.findViewById(R.id.iv_favorite);
            tvName = itemView.findViewById(R.id.tv_meal_name);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvArea = itemView.findViewById(R.id.tv_area);
        }

        void bind(Meal meal) {
            tvName.setText(meal.getStrMeal());

            if (meal.getStrCategory() != null && !meal.getStrCategory().isEmpty()) {
                tvCategory.setVisibility(View.VISIBLE);
                tvCategory.setText(meal.getStrCategory());
            } else {
                tvCategory.setVisibility(View.GONE);
            }

            if (meal.getStrArea() != null && !meal.getStrArea().isEmpty()) {
                tvArea.setVisibility(View.VISIBLE);
                tvArea.setText(meal.getStrArea());
            } else {
                tvArea.setVisibility(View.GONE);
            }

            Glide.with(itemView.getContext())
                    .load(meal.getStrMealThumb())
                    .centerCrop()
                    .placeholder(R.color.shimmer_base)
                    .into(ivThumb);

            ivFavorite.setImageResource(
                    meal.isFavorite() ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);

            itemView.setOnClickListener(v -> listener.onMealClick(meal));
            ivFavorite.setOnClickListener(v ->
                    listener.onFavoriteClick(meal, getAdapterPosition()));
        }
    }
}