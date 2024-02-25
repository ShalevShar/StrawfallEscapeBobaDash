package com.example.strawfallescapebobadash.Logic;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import java.util.List;

public class GameManager {
    private int hits = 0;
    private int life;
    private int score = 0;
    private Context context;

    public GameManager(int life, Context context) {
        this.life = life;
        this.context = context;
    }

    public int getHits() {
        return hits;
    }

    public int getLife() {
        return life;
    }

    public int getScore() {
        return score;
    }

    public boolean isGameLost (){
        return getLife() == getHits();
    }

    public boolean checkIfHit(int bobaDrinkPosition, List<Boolean> lastRowStraws){
        if(lastRowStraws.get(bobaDrinkPosition)){
            hits++;
            lastRowStraws.set(bobaDrinkPosition, false);
            vibrateDevice();
            return true;
        }
        return false;
    }
    public boolean checkIfExtra(int bobaDrinkPosition, List<Boolean> lastRowCoins){
        if(lastRowCoins.get(bobaDrinkPosition)){
            lastRowCoins.set(bobaDrinkPosition, false);
            vibrateDevice();
            return true;
        }
        return false;
    }
    public void setScore(int value) {
        this.score = value;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }


    public void vibrateDevice() {
        // Get instance of Vibrator from current Context
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        // Check if device supports vibration
        if (vibrator != null && vibrator.hasVibrator()) {
            // Vibrate for 500 milliseconds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                // Deprecated in API 26
                vibrator.vibrate(500);
            }
        }
    }

}


