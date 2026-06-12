package com.example.nutrimeal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutrimeal.R;
import com.example.nutrimeal.model.NoteEntity;

import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    public interface OnNoteClickListener {
        void onNoteClick(NoteEntity note);
    }

    private List<NoteEntity> notes = new ArrayList<>();
    private OnNoteClickListener listener;

    public NotesAdapter(OnNoteClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<NoteEntity> list) {
        this.notes = new ArrayList<>(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(notes.get(position));
    }

    @Override
    public int getItemCount() { return notes.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMealId, tvNoteText, tvDate;
        LinearLayout layoutStars;

        ViewHolder(View itemView) {
            super(itemView);
            tvMealId = itemView.findViewById(R.id.tv_meal_id);
            tvNoteText = itemView.findViewById(R.id.tv_note_text);
            tvDate = itemView.findViewById(R.id.tv_date);
            layoutStars = itemView.findViewById(R.id.layout_stars);
        }

        void bind(NoteEntity note) {
            String name = note.getMealName() != null && !note.getMealName().isEmpty()
                    ? note.getMealName() : "Meal ID: " + note.getMealId();
            tvMealId.setText(name);
            tvNoteText.setText(note.getNoteText() != null && !note.getNoteText().isEmpty()
                    ? note.getNoteText() : "No note written");
            tvDate.setText(note.getDateModified());

            layoutStars.removeAllViews();
            for (int i = 1; i <= 5; i++) {
                ImageView star = new ImageView(itemView.getContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(40, 40);
                params.setMarginEnd(4);
                star.setLayoutParams(params);
                star.setImageResource(i <= note.getStarRating() ?
                        R.drawable.ic_star_filled : R.drawable.ic_star_outline);
                layoutStars.addView(star);
            }

            itemView.setOnClickListener(v -> listener.onNoteClick(note));
        }
    }
}