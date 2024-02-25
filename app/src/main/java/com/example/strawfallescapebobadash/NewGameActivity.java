package com.example.strawfallescapebobadash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;

import com.example.strawfallescapebobadash.Logic.GameManager;
import com.example.strawfallescapebobadash.Sensors.SensorDetector;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;

import android.Manifest;

public class NewGameActivity extends AppCompatActivity {
    private AppCompatImageButton new_game_BTN_left;
    private AppCompatImageButton new_game_BTN_right;
    private AppCompatImageButton new_game_BTN_exit;
    private ShapeableImageView new_game_IMG_bobaDrink0;
    private ShapeableImageView new_game_IMG_bobaDrink1;
    private ShapeableImageView new_game_IMG_bobaDrink2;
    private ShapeableImageView new_game_IMG_bobaDrink3;
    private ShapeableImageView new_game_IMG_bobaDrink4;
    private ShapeableImageView[] new_game_IMG_bobaLifes;
    private AppCompatTextView new_game_LBL_score;
    private FallingElementsHandler fallingElementsHandler;
    private GridLayout strawGridLayout;
    private GridLayout coinGridLayout;
    private GameManager gameManager;
    private final Handler scoreHandler = new Handler();
    private Runnable scoreRunnable;
    private Handler UIHandler = new Handler();
    private Runnable refreshRunnable;
    private int bobaDrinkPosition = 2;
    private int maxColumns = 5;
    private String playerName;
    private static final int SCORE_SPEED_MS = 500;

    private FusedLocationProviderClient fusedLocationClient;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private SoundManager soundManager;
    private boolean isSensorMode = false;
    private boolean isFastMode = false;
    private SensorDetector sensorDetector;
    private int delay = 1000;
    private final int fast_delay = 700, slow_delay = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        // Retrieve data from intent extras
        getData();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        soundManager = new SoundManager(this);

        sensorDetector = new SensorDetector(this, listener);

        findViews();
        gameManager = new GameManager(new_game_IMG_bobaLifes.length, this);
        setBobaStartPosition();
        fallingElementsHandler = new FallingElementsHandler(strawGridLayout, coinGridLayout);

        startScoreCount();
        refreshUI();

        setJoysticks();

        new_game_BTN_exit.setOnClickListener(v -> exitActivity());

    }

    private void setJoysticks() {
        new_game_BTN_left.setOnClickListener(v -> moveBobaDrinkLeft());

        new_game_BTN_right.setOnClickListener(v -> moveBobaDrinkRight());

        if (isSensorMode) {
            sensorDetector.startX();
            hideGameButtons();
        } else {
            showGameButtons();
        }
    }

    private void showGameButtons() {
        new_game_BTN_left.setVisibility(View.VISIBLE);
        new_game_BTN_right.setVisibility(View.VISIBLE);
    }

    private void hideGameButtons() {
        new_game_BTN_left.setVisibility(View.INVISIBLE);
        new_game_BTN_right.setVisibility(View.INVISIBLE);
    }

    private void exitActivity() {
        // Check for location permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
                return;
            }
        }

        // Save player name, score, latitude, and longitude
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        int score = gameManager.getScore();
                        savePlayerScore(playerName, score, latitude, longitude);
                        changeActivity("KEY_PAUSE", score);
                    } else {
                        // Handle case where location is null
                        //Toast.makeText(NewGameActivity.this, "Unable to retrieve current location", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private SensorDetector.CallBackView listener = new SensorDetector.CallBackView() {
        @Override
        public void moveBySensor(int index) {
            int newPosition = bobaDrinkPosition + index;
            if (newPosition >= 0 && newPosition < maxColumns) {
                setBobaPosition(newPosition);
            }
        }

        @Override
        public void changeSpeedBySensor(int speed) {
            delay = speed;
        }
    };

    private void refreshUI() {
        final int delay = 100;

        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                // Check if game lost -> continue but reset parameters.
                if (gameManager.isGameLost()) {
                    fallingElementsHandler.stopFlowingElements();
                    CustomToast.show(NewGameActivity.this, "Game Over!", CustomToast.ToastType.DEFAULT);
                    int score = gameManager.getScore();
                    // Get current location
                    if (checkPermission()) {
                        // If permission is granted, get the current location
                        getCurrentLocation(location -> {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            savePlayerScore(playerName, score, latitude, longitude);
                            changeActivity("KEY_STATUS", gameManager.getScore());
                        });
                    } else {
                        // Permission is not granted, request it
                        requestPermission();
                    }
                    SoundManager.playGameOverSound();
                }

                // Check if there's a hit
                else if (gameManager.checkIfHit(bobaDrinkPosition, fallingElementsHandler.getLastRowStraws())) {
                    if (gameManager.getHits() != 0 && gameManager.getHits() < 4)
                        new_game_IMG_bobaLifes[new_game_IMG_bobaLifes.length - gameManager.getHits()].setVisibility(View.INVISIBLE);
                    CustomToast.show(NewGameActivity.this, "Ouch!", CustomToast.ToastType.HIT);
                    SoundManager.playHitSound();
                }
                // check if extra score
                else if (gameManager.checkIfExtra(bobaDrinkPosition, fallingElementsHandler.getLastRowCoins())) {
                    gameManager.setScore(gameManager.getScore() + 100);
                    CustomToast.show(NewGameActivity.this, "Extra 100+", CustomToast.ToastType.BONUS);
                    SoundManager.playBonusSound();
                }

                UIHandler.postDelayed(this, delay);
            }
        };

        UIHandler.postDelayed(refreshRunnable, delay); // Initial delay before starting the loop
    }

    // Method to check for location permission
    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    // Method to request location permission
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
    }

    // Method to get current location
    private void getCurrentLocation(OnSuccessListener<Location> onSuccessListener) {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, onSuccessListener);
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
                scoreHandler.postDelayed(this, SCORE_SPEED_MS); // Adjust the delay time (in milliseconds) as needed
            }
        };

        //scoreHandler.postDelayed(scoreRunnable, SCORE_SPEED_MS); // Initial delay before starting the loop
    }

    private void findViews() {
        new_game_BTN_left = findViewById(R.id.new_game_BTN_left);
        new_game_BTN_right = findViewById(R.id.new_game_BTN_right);
        new_game_IMG_bobaDrink0 = findViewById(R.id.new_game_IMG_bobaDrink0);
        new_game_IMG_bobaDrink1 = findViewById(R.id.new_game_IMG_bobaDrink1);
        new_game_IMG_bobaDrink2 = findViewById(R.id.new_game_IMG_bobaDrink2);
        new_game_IMG_bobaDrink3 = findViewById(R.id.new_game_IMG_bobaDrink3);
        new_game_IMG_bobaDrink4 = findViewById(R.id.new_game_IMG_bobaDrink4);
        new_game_IMG_bobaLifes = new ShapeableImageView[]{
                findViewById(R.id.new_game_IMG_bobaLife1),
                findViewById(R.id.new_game_IMG_bobaLife2),
                findViewById(R.id.new_game_IMG_bobaLife3)
        };
        strawGridLayout = findViewById(R.id.new_game_GRV_strawGameGrid);
        coinGridLayout = findViewById(R.id.new_game_GRV_coinGameGrid);
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
        new_game_IMG_bobaDrink3.setVisibility(View.INVISIBLE);
        new_game_IMG_bobaDrink4.setVisibility(View.INVISIBLE);

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
            case 3:
                new_game_IMG_bobaDrink3.setVisibility(View.VISIBLE);
                break;
            case 4:
                new_game_IMG_bobaDrink4.setVisibility(View.VISIBLE);
                break;
        }
    }
    private void setBobaStartPosition() {
        new_game_IMG_bobaDrink0.setVisibility(View.INVISIBLE);
        new_game_IMG_bobaDrink1.setVisibility(View.INVISIBLE);
        new_game_IMG_bobaDrink3.setVisibility(View.INVISIBLE);
        new_game_IMG_bobaDrink4.setVisibility(View.INVISIBLE);
    }

    private void changeActivity(String status, int score) {
        fallingElementsHandler.stopFlowingElements();
        Intent scoreIntent = new Intent(this, ScoreActivity.class);
        startActivity(scoreIntent);
        finish();
    }
    @Override
    protected void onPause() {
        super.onPause();
        fallingElementsHandler.pauseFlowingElements();
        scoreHandler.removeCallbacksAndMessages(null);
        UIHandler.removeCallbacksAndMessages(null);
        if(isSensorMode) {
            sensorDetector.stopX();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fallingElementsHandler.resumeFlowingElements();
        scoreHandler.post(scoreRunnable);
        UIHandler.post(refreshRunnable);
        if(isSensorMode) {
            sensorDetector.startX();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        fallingElementsHandler.stopFlowingElements();
        scoreHandler.removeCallbacksAndMessages(null);
        UIHandler.removeCallbacksAndMessages(null);
        if(isSensorMode) {
            sensorDetector.stopX();
        }
        soundManager.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fallingElementsHandler.stopFlowingElements();
        scoreHandler.removeCallbacksAndMessages(null);
        UIHandler.removeCallbacksAndMessages(null);
        soundManager.release();
    }

    private void savePlayerScore(String playerName, int score, double latitude, double longitude) {
        PlayerScore player = new PlayerScore(playerName, score, latitude, longitude);
        SharedPreferencesManager.savePlayerScore(NewGameActivity.this, player);

        // Print the saved player score for verification
        PlayerScore savedPlayerScore = SharedPreferencesManager.getPlayerRecord(NewGameActivity.this);
        Log.d("SavedPlayerScore", "Player Name: " + savedPlayerScore.getPlayerName());
        Log.d("SavedPlayerScore", "Score: " + savedPlayerScore.getScore());
        Log.d("SavedPlayerScore", "Latitude: " + savedPlayerScore.getLatitude());
        Log.d("SavedPlayerScore", "Longitude: " + savedPlayerScore.getLongitude());
    }

    private void getData() {
        Intent intent = getIntent();
        playerName = intent.getStringExtra("PLAYER_NAME");
        isSensorMode = intent.getBooleanExtra("KEY_SENSOR", false);
        isFastMode = intent.getBooleanExtra("KEY_DELAY", false);
        setDelay(isFastMode);
    }
    private void setDelay(boolean isFasterMode) {
        if(isFasterMode)
            delay = fast_delay;
        else
            delay = slow_delay;
    }

}