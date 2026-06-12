package com.example.nutrimeal.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutrimeal.R;
import com.example.nutrimeal.adapter.NotesAdapter;
import com.example.nutrimeal.database.MealDao;
import com.example.nutrimeal.model.NoteEntity;
import com.example.nutrimeal.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotesFragment extends Fragment {

    private RecyclerView rvNotes;
    private TextView tvEmpty;
    private NotesAdapter adapter;
    private MealDao mealDao;
    private SessionManager sessionManager;
    private int userId;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mealDao = new MealDao(requireContext());
        sessionManager = new SessionManager(requireContext());
        userId = sessionManager.getUserId();

        rvNotes = view.findViewById(R.id.rv_notes);
        tvEmpty = view.findViewById(R.id.tv_empty);

        view.findViewById(R.id.btn_back).setOnClickListener(v ->
                requireActivity().getOnBackPressedDispatcher().onBackPressed());

        setupAdapter();
        loadNotes();
    }

    private void setupAdapter() {
        adapter = new NotesAdapter(note -> showEditNoteDialog(note));
        rvNotes.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvNotes.setAdapter(adapter);
    }

    private void loadNotes() {
        executor.execute(() -> {
            List<NoteEntity> notes = mealDao.getAllNotes(userId);
            mainHandler.post(() -> {
                if (isAdded()) {
                    adapter.submitList(notes);
                    tvEmpty.setVisibility(notes.isEmpty() ? View.VISIBLE : View.GONE);
                }
            });
        });
    }

    private void showEditNoteDialog(NoteEntity note) {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_note, null);

        android.widget.EditText etNote = dialogView.findViewById(R.id.et_note);
        LinearLayout layoutStars = dialogView.findViewById(R.id.layout_stars_dialog);

        etNote.setText(note.getNoteText());

        final int[] selectedRating = {note.getStarRating()};
        refreshStars(layoutStars, selectedRating[0]);

        for (int i = 0; i < layoutStars.getChildCount(); i++) {
            final int starIndex = i + 1;
            layoutStars.getChildAt(i).setOnClickListener(v -> {
                selectedRating[0] = starIndex;
                refreshStars(layoutStars, selectedRating[0]);
            });
        }

        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Edit Note")
                .setView(dialogView)
                .setPositiveButton(getString(R.string.save_note), (dialog, which) -> {
                    String newText = etNote.getText().toString().trim();
                    executor.execute(() -> {
                        note.setNoteText(newText);
                        note.setStarRating(selectedRating[0]);
                        note.setDateModified(new SimpleDateFormat(
                                "yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                .format(new Date()));
                        mealDao.insertOrUpdateNote(userId, note);
                        mainHandler.post(() -> {
                            loadNotes();
                            if (isAdded() && getContext() != null) {
                                Toast.makeText(getContext(),
                                        "Note updated",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }

    private void refreshStars(LinearLayout layout, int rating) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof ImageView) {
                ((ImageView) child).setImageResource(
                        i < rating ?
                                R.drawable.ic_star_filled :
                                R.drawable.ic_star_outline);
            }
        }
    }
}