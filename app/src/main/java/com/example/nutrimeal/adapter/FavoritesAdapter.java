package com.example.nutrimeal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nutrimeal.R;
import com.example.nutrimeal.model.FavoriteEntity;

import java.util.ArrayList;
import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    public interface OnFavoriteClickListener {
        void onFavoriteClick(FavoriteEntity favorite);
    }

    private List<FavoriteEntity> favorites = new ArrayList<>();
    private List<FavoriteEntity> allFavorites = new ArrayList<>();
    private OnFavoriteClickListener listener;

    public FavoritesAdapter(OnFavoriteClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<FavoriteEntity> list) {
        this.favorites = new ArrayList<>(list);
        this.allFavorites = new ArrayList<>(list);
        notifyDataSetChanged();
    }

    public void filter(String query) {
        if (query.isEmpty()) {
            favorites = new ArrayList<>(allFavorites);
        } else {
            List<FavoriteEntity> filtered = new ArrayList<>();
            for (FavoriteEntity fav : allFavorites) {
                if (fav.getMealName().toLowerCase()
                        .contains(query.toLowerCase())) {
                    filtered.add(fav);
                }
            }
            favorites = filtered;
        }
        notifyDataSetChanged();
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
        holder.bind(favorites.get(position));
    }

    @Override
    public int getItemCount() { return favorites.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumb;
        TextView tvName, tvCategory, tvArea;

        ViewHolder(View itemView) {
            super(itemView);
            ivThumb = itemView.findViewById(R.id.iv_meal_thumb);
            tvName = itemView.findViewById(R.id.tv_meal_name);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvArea = itemView.findViewById(R.id.tv_area);
            itemView.findViewById(R.id.iv_favorite).setVisibility(View.GONE);
        }

        void bind(FavoriteEntity fav) {
            tvName.setText(fav.getMealName());
            tvCategory.setText(fav.getCategory());
            tvArea.setText(fav.getArea());
            Glide.with(itemView.getContext())
                    .load(fav.getMealThumb())
                    .centerCrop()
                    .placeholder(R.color.shimmer_base)
                    .into(ivThumb);
            itemView.setOnClickListener(v -> listener.onFavoriteClick(fav));
        }
    }
}