package com.example.strawfallescapebobadash;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

public class MainActivity extends AppCompatActivity {

    private ShapeableImageView main_IMG_logo;
    private MaterialTextView main_BTN_startGame;
    private MaterialTextView main_BTN_globalScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();

        animateLogo();

        main_BTN_startGame.setOnClickListener(v -> startGameActivity());

        main_BTN_globalScore.setOnClickListener(v -> {
            //globalScoreActivity();
        });
    }

    private void animateLogo() { 
        ObjectAnimator logoAnimator = ObjectAnimator.ofFloat(main_IMG_logo, "translationY", -20f, 20f);
        logoAnimator.setDuration(2000);
        logoAnimator.setRepeatMode(ObjectAnimator.REVERSE);
        logoAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        logoAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        logoAnimator.start();
    }

    private void findViews() {
        main_BTN_startGame = findViewById(R.id.main_BTN_startGame);
        main_BTN_globalScore = findViewById(R.id.main_BTN_globalScore);
        main_IMG_logo = findViewById(R.id.main_IMG_logo);
    }

    private void startGameActivity() {
        Intent intent = new Intent(this, NewGameActivity.class);
        startActivity(intent);
        finish();
    }
}
