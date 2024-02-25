package com.example.strawfallescapebobadash;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.CompoundButton;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import com.example.strawfallescapebobadash.GlobalScores.ScoreActivity;
import com.example.strawfallescapebobadash.Logic.NewGameActivity;
import com.example.strawfallescapebobadash.Utils.CustomToast;
import com.example.strawfallescapebobadash.Utils.SharedPreferencesManager;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

public class MainActivity extends AppCompatActivity {

    private ShapeableImageView main_IMG_logo;
    private MaterialTextView main_BTN_startGame;
    private MaterialTextView main_BTN_globalScore;
    private AppCompatEditText main_EDT_name;
    private Switch main_SWCH_fastMode;
    private Switch main_SWCH_sensorMode;
    private boolean isSensorMode = false;
    private boolean isFastMode = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();

        animateLogo();

        main_BTN_startGame.setOnClickListener(v -> startGameActivity());

        main_BTN_globalScore.setOnClickListener(v -> globalScoreActivity());

        main_SWCH_sensorMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean sensorOn) {
                isSensorMode = sensorOn;
            }
        });
        main_SWCH_fastMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean fasterModeOn) {
                isFastMode = fasterModeOn;
            }
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
        main_EDT_name = findViewById(R.id.main_EDT_name);
        main_SWCH_fastMode = findViewById(R.id.main_SWCH_fastMode);
        main_SWCH_sensorMode = findViewById(R.id.main_SWCH_sensorMode);
    }

    private void startGameActivity() {
        String playerName = main_EDT_name.getText().toString().trim();

        if (playerName.isEmpty()) {
            // Player name is empty
            CustomToast.show(MainActivity.this, "Please enter player name", CustomToast.ToastType.DEFAULT);
        } else if (SharedPreferencesManager.isPlayerNameExists(this, playerName)) {
            // Player name already exists
            CustomToast.show(MainActivity.this, "Player name already exists", CustomToast.ToastType.DEFAULT);
        } else {
            // Player name is valid
            Intent intent = new Intent(this, NewGameActivity.class);
            intent.putExtra("PLAYER_NAME", playerName);
            intent.putExtra("KEY_SENSOR", isSensorMode);
            intent.putExtra("KEY_DELAY", isFastMode);
            startActivity(intent);
            finish();
        }
    }

    private void globalScoreActivity() {
        Intent intent = new Intent(this, ScoreActivity.class);
        startActivity(intent);
        finish();
    }
}
