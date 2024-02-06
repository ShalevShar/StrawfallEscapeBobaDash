package com.example.strawfallescapebobadash;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.Toast;

import com.example.strawfallescapebobadash.Logic.GameManager;
import com.google.android.material.imageview.ShapeableImageView;

public class NewGameActivity extends AppCompatActivity {
    private AppCompatImageButton new_game_BTN_left;
    private AppCompatImageButton new_game_BTN_right;
    private AppCompatImageButton new_game_BTN_exit;
    private ShapeableImageView new_game_IMG_bobaDrink0;
    private ShapeableImageView new_game_IMG_bobaDrink1;
    private ShapeableImageView new_game_IMG_bobaDrink2;
    private ShapeableImageView [] new_game_IMG_bobaLifes;
    private AppCompatTextView new_game_LBL_score;
    private StrawHandler strawHandler;
    private GridLayout strawGridLayout;
    private GameManager gameManager;
    private final Handler scoreHandler = new Handler();
    private Runnable scoreRunnable;
    private Handler UIHandler = new Handler();
    private Runnable refreshRunnable;
    private int bobaDrinkPosition = 1;
    private int maxColumns = 3;
    private int maxRows = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        findViews();
        gameManager = new GameManager(new_game_IMG_bobaLifes.length, this);
        setBobaStartPosition();
        strawHandler = new StrawHandler(strawGridLayout);

        startScoreCount();
        refreshUI();

        new_game_BTN_left.setOnClickListener(v -> moveBobaDrinkLeft());

        new_game_BTN_right.setOnClickListener(v -> moveBobaDrinkRight());

        new_game_BTN_exit.setOnClickListener(v -> exitActivity());

    }

    private void exitActivity() {
        changeActivity("KEY_PAUSE", gameManager.getScore());
    }

    private void refreshUI() {
        final int delay = 100;

        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                // Check if game lost -> continue but reset parameters.
                if (gameManager.isGameLost()) {
                    strawHandler.stopFlowingStraws();
                    showToast(getApplicationContext(), "You Lost! Starting Over");
                    for (ShapeableImageView newGameImgBobaLife : new_game_IMG_bobaLifes) {
                        newGameImgBobaLife.setVisibility(View.VISIBLE);
                    }
                    gameManager.setHits(0);
                    gameManager.setScore(0);
                    strawHandler = new StrawHandler(strawGridLayout);
                }

                // Check if there's a hit
                else if (gameManager.checkIfHit(bobaDrinkPosition, strawHandler.getLastRowStraws())) {
                    if (gameManager.getHits() != 0 && gameManager.getHits() < 4)
                        new_game_IMG_bobaLifes[new_game_IMG_bobaLifes.length - gameManager.getHits()].setVisibility(View.INVISIBLE);
                    showToast(getApplicationContext(), "Ouch!");
                }

                UIHandler.postDelayed(this, delay);
            }
        };

        UIHandler.postDelayed(refreshRunnable, delay); // Initial delay before starting the loop
    }

    private void startScoreCount() {
        scoreRunnable = new Runnable() {
            @Override
            public void run() {
                if (!gameManager.isGameLost()) {
                    gameManager.setScore(gameManager.getScore() + 1); // Increment score by 1
                    new_game_LBL_score.setText(String.valueOf(gameManager.getScore()));
                }
                // Update score display
                scoreHandler.postDelayed(this, 100); // Adjust the delay time (in milliseconds) as needed
            }
        };

        scoreHandler.postDelayed(scoreRunnable, 100); // Initial delay before starting the loop
    }

    private void findViews() {
        new_game_BTN_left = findViewById(R.id.new_game_BTN_left);
        new_game_BTN_right = findViewById(R.id.new_game_BTN_right);
        new_game_IMG_bobaDrink0 = findViewById(R.id.new_game_IMG_bobaDrink0);
        new_game_IMG_bobaDrink1 = findViewById(R.id.new_game_IMG_bobaDrink1);
        new_game_IMG_bobaDrink2 = findViewById(R.id.new_game_IMG_bobaDrink2);
        new_game_IMG_bobaLifes = new ShapeableImageView[]{
                findViewById(R.id.new_game_IMG_bobaLife1),
                findViewById(R.id.new_game_IMG_bobaLife2),
                findViewById(R.id.new_game_IMG_bobaLife3)
        };
        strawGridLayout = findViewById(R.id.new_game_GRV_gameGrid);
        new_game_LBL_score = findViewById(R.id.new_game_LBL_score);
        new_game_BTN_exit = findViewById(R.id.new_game_BTN_exit);
        new_game_LBL_score.setText("0");
    }
    public void moveBobaDrinkRight() {
        if (bobaDrinkPosition + 1 <= maxColumns - 1) {
            setBobaPosition(bobaDrinkPosition + 1);
        }
    }
    public void moveBobaDrinkLeft() {
        if (bobaDrinkPosition - 1 >= 0) {
            setBobaPosition(bobaDrinkPosition - 1);
        }
    }

    private void setBobaPosition(int position) {
        bobaDrinkPosition = position;
        updateBobaPositionInView();
    }
    private void updateBobaPositionInView() {
        // Hide all boba images initially
        new_game_IMG_bobaDrink0.setVisibility(View.INVISIBLE);
        new_game_IMG_bobaDrink1.setVisibility(View.INVISIBLE);
        new_game_IMG_bobaDrink2.setVisibility(View.INVISIBLE);

        // Show the relevant boba image based on the position
        switch (bobaDrinkPosition) {
            case 0:
                new_game_IMG_bobaDrink0.setVisibility(View.VISIBLE);
                break;
            case 1:
                new_game_IMG_bobaDrink1.setVisibility(View.VISIBLE);
                break;
            case 2:
                new_game_IMG_bobaDrink2.setVisibility(View.VISIBLE);
                break;
        }
    }
    private void setBobaStartPosition() {
        new_game_IMG_bobaDrink0.setVisibility(View.INVISIBLE);
        new_game_IMG_bobaDrink2.setVisibility(View.INVISIBLE);
    }

    private void changeActivity(String status, int score) {
        strawHandler.stopFlowingStraws();
        Intent scoreIntent = new Intent(this, ScoreActivity.class);
        scoreIntent.putExtra(ScoreActivity.KEY_STATUS, status);
        scoreIntent.putExtra(ScoreActivity.KEY_SCORE, score);
        startActivity(scoreIntent);
        finish();
    }
    private void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        strawHandler.pauseFlowingStraws();
        scoreHandler.removeCallbacksAndMessages(null);
        UIHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        strawHandler.resumeFlowingStraws();
        scoreHandler.post(scoreRunnable);
        UIHandler.post(refreshRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        strawHandler.stopFlowingStraws();
        scoreHandler.removeCallbacksAndMessages(null);
        UIHandler.removeCallbacksAndMessages(null);
    }


}