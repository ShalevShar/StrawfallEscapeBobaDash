package com.example.strawfallescapebobadash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

public class ScoreActivity extends AppCompatActivity {
    public static final String KEY_STATUS = "KEY_STATUS";
    public static final String KEY_SCORE = "KEY_SCORE";
    private AppCompatTextView score_LBL_score;
    private AppCompatImageButton score_BTN_quit;
    private AppCompatImageButton score_BTN_resume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        findViews();
        Intent previousScreen = getIntent();
        String status = previousScreen.getStringExtra(KEY_STATUS);
        int score = previousScreen.getIntExtra(KEY_SCORE,0);
        showScore(status, score);

        score_BTN_quit.setOnClickListener(v -> backToMain());
        score_BTN_resume.setOnClickListener(v -> resumeGame());
    }
    private void showScore(String status, int score) {
        if(status.matches("YOU WON!")) {
            score_LBL_score.setTextColor(Color.BLUE);
            score_LBL_score.setText(status + "\n" + score);
        }
        else if(status.matches("YOU LOST!")) {
            score_LBL_score.setTextColor(Color.RED);
            score_LBL_score.setText(status + "\n" + score);
        }
            else{
            score_LBL_score.setText(score + "");
        }

    }
    private void backToMain() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
    private void resumeGame() {
        Intent mainIntent = new Intent(this, NewGameActivity.class);
        startActivity(mainIntent);
        finish();
    }
    private void findViews() {
        score_LBL_score = findViewById(R.id.score_LBL_score);
        score_BTN_quit = findViewById(R.id.score_BTN_quit);
        score_BTN_resume = findViewById(R.id.score_BTN_resume);
    }
}