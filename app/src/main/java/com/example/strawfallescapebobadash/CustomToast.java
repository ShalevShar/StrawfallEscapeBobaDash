package com.example.strawfallescapebobadash;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CustomToast {
    private static Toast toast;
    private static final int TOAST_DURATION_MS = 1200; // Duration of the toast in milliseconds
    private static final int ANIMATION_DURATION_MS = 400; // Duration of the animation in milliseconds

    public enum ToastType {
        DEFAULT,
        BONUS,
        HIT
    }

    public static void show(Context context, String message, ToastType type) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.custom_toast_layout, null);

        ImageView imageViewBoba = layout.findViewById(R.id.imageView_boba);
        ImageView imageViewCoin = layout.findViewById(R.id.imageView_coin);
        if (type == ToastType.HIT) {
            imageViewCoin.setVisibility(View.INVISIBLE);
            imageViewBoba.setVisibility(View.VISIBLE);
        } else if (type == ToastType.BONUS) {
            imageViewBoba.setVisibility(View.INVISIBLE);
            imageViewCoin.setVisibility(View.VISIBLE);
        } else {
            imageViewBoba.setVisibility(View.INVISIBLE);
            imageViewCoin.setVisibility(View.INVISIBLE);
        }

        TextView textView = layout.findViewById(R.id.textView);
        textView.setText(message); // Set the message

        toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 100); // Adjust the position of the toast

        // Delay the start of the animation
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            // Start animation after the delay
            startAnimation(layout);
        }, TOAST_DURATION_MS - ANIMATION_DURATION_MS); // Start animation just before the toast disappears

        toast.show();
    }

    private static void startAnimation(View view) {
        // Move the view slightly upwards and fade it out
        view.animate()
                .translationYBy(-100) // Move the view upwards by 100 pixels
                .alpha(0) // Set the alpha (opacity) to 0 for fading out
                .setDuration(ANIMATION_DURATION_MS) // Set the duration of the animation
                .withEndAction(() -> {
                    if (toast != null) {
                        toast.cancel(); // Dismiss the toast after the animation ends
                    }
                })
                .start();
    }

}
