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
import com.example.nutrimeal.model.Category;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    private List<Category> categories = new ArrayList<>();
    private OnCategoryClickListener listener;
    private int selectedPosition = 0;

    public CategoryAdapter(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<Category> newList) {
        // Tambahkan "All" di posisi pertama
        List<Category> withAll = new ArrayList<>();
        withAll.add(new Category("0", "All", ""));
        withAll.addAll(newList);

        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override public int getOldListSize() { return categories.size(); }
            @Override public int getNewListSize() { return withAll.size(); }
            @Override public boolean areItemsTheSame(int oldPos, int newPos) {
                return categories.get(oldPos).getIdCategory()
                        .equals(withAll.get(newPos).getIdCategory());
            }
            @Override public boolean areContentsTheSame(int oldPos, int newPos) {
                return categories.get(oldPos).getStrCategory()
                        .equals(withAll.get(newPos).getStrCategory());
            }
        });
        categories = withAll;
        result.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(categories.get(position), position == selectedPosition);
    }

    @Override
    public int getItemCount() { return categories.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCategory;
        TextView tvCategoryName;
        MaterialCardView card;

        ViewHolder(View itemView) {
            super(itemView);
            ivCategory = itemView.findViewById(R.id.iv_category);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
            card = (MaterialCardView) itemView;
        }

        void bind(Category category, boolean isSelected) {
            tvCategoryName.setText(category.getStrCategory());

            // Visual selected state
            if (isSelected) {
                card.setCardBackgroundColor(
                        itemView.getContext().getColor(R.color.accent));
                card.setStrokeWidth(0);
                tvCategoryName.setTextColor(
                        itemView.getContext().getColor(R.color.white));
            } else {
                card.setCardBackgroundColor(
                        itemView.getContext().getColor(R.color.white));
                card.setStrokeWidth(0);
                tvCategoryName.setTextColor(
                        itemView.getContext().getColor(R.color.text_primary_light));
            }

            // Load image — "All" pakai icon default
            if (category.getStrCategoryThumb() != null
                    && !category.getStrCategoryThumb().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(category.getStrCategoryThumb())
                        .centerCrop()
                        .into(ivCategory);
            } else {
                ivCategory.setImageResource(R.drawable.ic_nav_home);
            }

            itemView.setOnClickListener(v -> {
                int previousSelected = selectedPosition;
                selectedPosition = getAdapterPosition();
                notifyItemChanged(previousSelected);
                notifyItemChanged(selectedPosition);
                listener.onCategoryClick(category);
            });
        }
    }
}