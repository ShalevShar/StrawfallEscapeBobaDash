package com.example.strawfallescapebobadash.Sound;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.strawfallescapebobadash.R;

public class SoundManager {
    private static MediaPlayer hitSound;
    private static MediaPlayer bonusSound;
    private static MediaPlayer gameOverSound;

    public SoundManager(Context context) {
        hitSound = MediaPlayer.create(context, R.raw.drink_sound);
        bonusSound = MediaPlayer.create(context, R.raw.bonus_sound);
        gameOverSound = MediaPlayer.create(context, R.raw.game_over_sound);
    }

    public static void playHitSound() {
        if (hitSound != null) {
            hitSound.seekTo(0); // Rewind to the beginning
            hitSound.start();
        }
    }

    public static void playBonusSound() {
        if (bonusSound != null) {
            bonusSound.seekTo(0); // Rewind to the beginning
            bonusSound.start();
        }
    }

    public static void playGameOverSound() {
        if (gameOverSound != null) {
            gameOverSound.seekTo(0); // Rewind to the beginning
            gameOverSound.start();
        }
    }

    public void release() {
        if (hitSound != null) {
            hitSound.release();
            hitSound = null;
        }
        if (bonusSound != null) {
            bonusSound.release();
            bonusSound = null;
        }
    }
}
