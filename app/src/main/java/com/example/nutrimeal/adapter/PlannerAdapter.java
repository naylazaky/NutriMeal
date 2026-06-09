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
import com.example.nutrimeal.model.PlannerEntity;

import java.util.List;

public class PlannerAdapter extends RecyclerView.Adapter<PlannerAdapter.ViewHolder> {

    public interface OnPlannerClickListener {
        void onDayClick(PlannerEntity planner, int position);
        void onDayLongClick(PlannerEntity planner, int position);
    }

    private List<PlannerEntity> plannerList;
    private OnPlannerClickListener listener;

    public PlannerAdapter(List<PlannerEntity> plannerList, OnPlannerClickListener listener) {
        this.plannerList = plannerList;
        this.listener = listener;
    }

    public void updateItem(int position, String mealId, String mealName, String mealThumb) {
        plannerList.get(position).setMealId(mealId);
        plannerList.get(position).setMealName(mealName);
        plannerList.get(position).setMealThumb(mealThumb);
        notifyItemChanged(position);
    }

    public void clearItem(int position) {
        plannerList.get(position).setMealId(null);
        plannerList.get(position).setMealName(null);
        plannerList.get(position).setMealThumb(null);
        notifyItemChanged(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_planner, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(plannerList.get(position));
    }

    @Override
    public int getItemCount() { return plannerList.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayShort, tvDayFull, tvMealName;
        ImageView ivMealThumb;

        ViewHolder(View itemView) {
            super(itemView);
            tvDayShort = itemView.findViewById(R.id.tv_day_short);
            tvDayFull = itemView.findViewById(R.id.tv_day_full);
            tvMealName = itemView.findViewById(R.id.tv_meal_name);
            ivMealThumb = itemView.findViewById(R.id.iv_meal_thumb);
        }

        void bind(PlannerEntity planner) {
            tvDayFull.setText(planner.getDayOfWeek());
            tvDayShort.setText(planner.getDayOfWeek().substring(0, 3).toUpperCase());

            boolean hasMeal = planner.getMealName() != null
                    && !planner.getMealName().isEmpty();

            tvMealName.setText(hasMeal ?
                    planner.getMealName() : "Tap to add meal");
            tvMealName.setTextColor(itemView.getContext().getColor(
                    hasMeal ? R.color.text_secondary : R.color.accent));

            if (hasMeal && planner.getMealThumb() != null) {
                Glide.with(itemView.getContext())
                        .load(planner.getMealThumb())
                        .centerCrop()
                        .into(ivMealThumb);
            } else {
                ivMealThumb.setImageResource(R.drawable.ic_nav_planner);
            }

            itemView.setOnClickListener(v ->
                    listener.onDayClick(planner, getAdapterPosition()));

            itemView.setOnLongClickListener(v -> {
                if (hasMeal) {
                    listener.onDayLongClick(planner, getAdapterPosition());
                }
                return true;
            });
        }
    }
}