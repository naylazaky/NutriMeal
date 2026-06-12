package com.example.nutrimeal.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.example.nutrimeal.R;
import com.example.nutrimeal.utils.SessionManager;
import com.example.nutrimeal.utils.ThemeUtils;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_splash);

        ImageView ivLogo = findViewById(R.id.iv_logo);
        TextView tvAppName = findViewById(R.id.tv_app_name);
        TextView tvTagline = findViewById(R.id.tv_tagline);

        ObjectAnimator logoFade = ObjectAnimator.ofFloat(ivLogo, View.ALPHA, 0f, 1f);
        logoFade.setDuration(800);

        ObjectAnimator nameFade = ObjectAnimator.ofFloat(tvAppName, View.ALPHA, 0f, 1f);
        nameFade.setDuration(800);
        nameFade.setStartDelay(300);

        ObjectAnimator taglineFade = ObjectAnimator.ofFloat(tvTagline, View.ALPHA, 0f, 1f);
        taglineFade.setDuration(800);
        taglineFade.setStartDelay(500);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(logoFade, nameFade, taglineFade);
        animatorSet.start();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SessionManager sessionManager = new SessionManager(this);

            Intent intent;
            if (sessionManager.isLoggedIn()) {
                intent = new Intent(this, HomeActivity.class);
            } else {
                intent = new Intent(this, LoginActivity.class);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, SPLASH_DURATION);
    }
}