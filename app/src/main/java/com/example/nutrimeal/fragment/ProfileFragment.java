package com.example.nutrimeal.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.nutrimeal.R;
import com.example.nutrimeal.activity.LoginActivity;
import com.example.nutrimeal.database.MealDao;
import com.example.nutrimeal.database.UserDao;
import com.example.nutrimeal.utils.SessionManager;
import com.example.nutrimeal.utils.ThemeUtils;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileFragment extends Fragment {

    private SwitchMaterial switchDarkMode;
    private LinearLayout rowFavorites, rowNotes, rowLogout;
    private TextView tvFavoritesCount, tvNotesCount;
    private TextView tvUserName, tvUsername;
    private ImageView ivProfilePhoto;

    private MealDao mealDao;
    private UserDao userDao;
    private SessionManager sessionManager;
    private int userId;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private ActivityResultLauncher<Intent> pickImageLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK
                            && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            // Persist read permission
                            requireActivity().getContentResolver()
                                    .takePersistableUriPermission(imageUri,
                                            Intent.FLAG_GRANT_READ_URI_PERMISSION);

                            String path = imageUri.toString();
                            sessionManager.updatePhoto(path);
                            executor.execute(() ->
                                    userDao.updatePhotoPath(userId, path));
                            Glide.with(this)
                                    .load(imageUri)
                                    .centerCrop()
                                    .circleCrop()
                                    .into(ivProfilePhoto);
                        }
                    }
                });
    }

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
        userDao = new UserDao(requireContext());
        sessionManager = new SessionManager(requireContext());
        userId = sessionManager.getUserId();

        bindViews(view);
        setupUserInfo();
        setupDarkModeToggle();
        setupRowClicks();
        loadCounts();
    }

    private void bindViews(View view) {
        switchDarkMode = view.findViewById(R.id.switch_dark_mode);
        rowFavorites = view.findViewById(R.id.row_favorites);
        rowNotes = view.findViewById(R.id.row_notes);
        rowLogout = view.findViewById(R.id.row_logout);
        tvFavoritesCount = view.findViewById(R.id.tv_favorites_count);
        tvNotesCount = view.findViewById(R.id.tv_notes_count);
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvUsername = view.findViewById(R.id.tv_username);
        ivProfilePhoto = view.findViewById(R.id.iv_profile_photo);
    }

    private void setupUserInfo() {
        tvUserName.setText(sessionManager.getUserName());
        tvUsername.setText("@" + sessionManager.getUsername());

        String photoPath = sessionManager.getPhotoPath();
        if (photoPath != null && !photoPath.isEmpty()) {
            Glide.with(this)
                    .load(Uri.parse(photoPath))
                    .centerCrop()
                    .circleCrop()
                    .into(ivProfilePhoto);
        }

        ivProfilePhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            pickImageLauncher.launch(intent);
        });
    }

    private void setupDarkModeToggle() {
        switchDarkMode.setChecked(ThemeUtils.isDarkMode(requireContext()));
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) ->
                ThemeUtils.setDarkMode(requireContext(), isChecked));
    }

    private void setupRowClicks() {
        rowFavorites.setOnClickListener(v ->
                androidx.navigation.Navigation.findNavController(requireView())
                        .navigate(R.id.action_profile_to_favorites));

        rowNotes.setOnClickListener(v ->
                androidx.navigation.Navigation.findNavController(requireView())
                        .navigate(R.id.action_profile_to_notes));

        rowLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void showLogoutDialog() {
        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    sessionManager.logout(requireContext());
                    Intent intent = new Intent(requireActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }

    private void loadCounts() {
        executor.execute(() -> {
            int favCount = mealDao.getAllFavorites(userId).size();
            int notesCount = mealDao.getAllNotes(userId).size();
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