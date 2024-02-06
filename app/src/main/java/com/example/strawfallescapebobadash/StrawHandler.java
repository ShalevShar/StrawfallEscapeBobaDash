package com.example.strawfallescapebobadash;

import static android.content.ContentValues.TAG;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class StrawHandler {
    private List<ShapeableImageView> strawViewsAll;
    private List<ShapeableImageView> strawView1;
    private List<ShapeableImageView> strawView2;
    private List<ShapeableImageView> strawView3;
    private static int lastRandomRow;
    private static int maxColumns = 3;
    private static List<Boolean> lastRowStraws;
    private static boolean shouldContinue;
    private List<Integer> lastVisiblePositions = new ArrayList<>(Collections.nCopies(maxColumns, -1));
    private Handler handler = new Handler();
    private Runnable runnable;
    private Handler mainHandler = new Handler();

    public StrawHandler(GridLayout strawGridLayout) {
        this.strawViewsAll = extractStrawViews(strawGridLayout);
        this.strawView1 = extractStrawsForColumn(1, strawViewsAll);
        this.strawView2 = extractStrawsForColumn(2, strawViewsAll);
        this.strawView3 = extractStrawsForColumn(3, strawViewsAll);
        lastRandomRow = -1;
        lastRowStraws = new ArrayList<>();
        IntStream.range(0, 3).forEach(i -> lastRowStraws.add(false));
        shouldContinue = true;
        initializeStrawViews();
        startFlowingStraws();
    }
    public void startFlowingStraws() {
        runnable = new Runnable()  {
            @Override
            public void run() {
                if (shouldContinue) {
                    flowStrawsToScreen();
                    handler.postDelayed(this, 2000); // Adjust the delay time (in milliseconds) as needed
                }
            }
        };
        handler.postDelayed(runnable, 1000); // Initial delay before starting the loop
    }

    private List<ShapeableImageView> extractStrawViews(GridLayout strawGridLayout) {
        List<ShapeableImageView> strawViews = new ArrayList<>();
        for (int i = 0; i < strawGridLayout.getChildCount(); i++) {
            View child = strawGridLayout.getChildAt(i);
            if (child instanceof ShapeableImageView) {
                strawViews.add((ShapeableImageView) child);
            }
        }
        return strawViews;
    }

    private List<ShapeableImageView> extractStrawsForColumn(int column, List<ShapeableImageView> generalStraws) {
        List<ShapeableImageView> strawViews = new ArrayList<>();
        int size = generalStraws.size();
        int startIndex = column - 1;
        for (int i = startIndex; i < size; i += maxColumns) {
            strawViews.add(generalStraws.get(i));
        }
        return strawViews;
    }

    private void initializeStrawViews() {
        for (ShapeableImageView strawView : strawViewsAll) {
            strawView.setVisibility(View.INVISIBLE);
        }
    }

    private void flowStrawsToScreen() {
        int randomStraw = getRandomStraw();
        if (randomStraw == 0) {
            moveStrawsSequentially(0, strawView1);
        } else if (randomStraw == 1) {
            moveStrawsSequentially(1, strawView2);
        } else {
            moveStrawsSequentially(2, strawView3);
        }
    }

    public static int getRandomStraw() {
        Random random = new Random();
        int rand = random.nextInt(maxColumns);
        if(lastRandomRow != -1){
            while(rand == lastRandomRow){
                rand = random.nextInt(maxColumns);
            }
        }
        setLastRandomRow(rand);
        return rand;
    }

    private void moveStrawsSequentially(int column, final List<ShapeableImageView> strawView) {
        mainHandler.post(() -> {
            for (int i = 0; i < strawView.size(); i++) {
                final int index = i;
                mainHandler.postDelayed(() -> {
                    if (index > 0) {
                        strawView.get(index - 1).setVisibility(View.INVISIBLE);
                    }
                    strawView.get(index).setVisibility(View.VISIBLE);

                    if (index == strawView.size() - 1) {
                        lastRowStraws.set(column, true);
                        mainHandler.postDelayed(() -> {
                            strawView.get(index).setVisibility(View.INVISIBLE);
                            lastRowStraws.set(column, false);
                        }, 1000);
                    }
                }, i * 1000);
            }
        });
    }

    public List<Boolean> getLastRowStraws() {
        return lastRowStraws;
    }

    public static void setLastRandomRow(int lastRandomRow) {
        StrawHandler.lastRandomRow = lastRandomRow;
    }
    public void pauseFlowingStraws() {
        shouldContinue = false;
        handler.removeCallbacksAndMessages(null);
        mainHandler.removeCallbacksAndMessages(null);
    }
    public void resumeFlowingStraws() {
        shouldContinue = true;
        updateLastVisiblePositions(); // Update last visible positions first
        startFlowingStrawsFromLastPosition();
    }
    public void stopFlowingStraws() {
        shouldContinue = false;
        handler.removeCallbacksAndMessages(null);
        mainHandler.removeCallbacksAndMessages(null);
    }
    private void updateLastVisiblePositions() {
        for (int i = 0; i < maxColumns; i++) {
            List<ShapeableImageView> strawView = getStrawViewForColumn(i);
            for (int j = strawView.size() - 1; j >= 0; j--) {
                if (strawView.get(j).getVisibility() == View.VISIBLE) {
                    lastVisiblePositions.set(i, j);
                    break;
                }
            }
        }
    }

    private void startFlowingStrawsFromLastPosition() {
        mainHandler.post(() -> {
            for (int i = 0; i < maxColumns; i++) {
                List<ShapeableImageView> strawView = getStrawViewForColumn(i);
                int lastVisiblePosition = lastVisiblePositions.get(i);
                if (lastVisiblePosition >= 0 && lastVisiblePosition < strawView.size()) {
                    moveStrawsSequentiallyFromPosition(i, lastVisiblePosition, strawView);
                }
            }
        mainHandler.postDelayed(() -> {
            handler.removeCallbacksAndMessages(null);
            startFlowingStraws();
        }, 50); // Adjust the delay time as needed
    });
   }

    private void moveStrawsSequentiallyFromPosition(int column, int position, List<ShapeableImageView> strawView) {
        mainHandler.post(() -> {
            for (int i = position; i < strawView.size(); i++) {
                final int index = i;
                mainHandler.postDelayed(() -> {
                    if (index > position) {
                        strawView.get(index - 1).setVisibility(View.INVISIBLE);
                    }
                    strawView.get(index).setVisibility(View.VISIBLE);
                    if (index == strawView.size() - 1) {
                        lastRowStraws.set(column, true);
                        mainHandler.postDelayed(() -> {
                            strawView.get(index).setVisibility(View.INVISIBLE);
                            lastRowStraws.set(column, false);
                        }, 1000);
                    }
                }, (i - position) * 1000);
            }
        });
    }

    private List<ShapeableImageView> getStrawViewForColumn(int column) {
        switch (column) {
            case 0:
                return strawView1;
            case 1:
                return strawView2;
            case 2:
                return strawView3;
            default:
                return new ArrayList<>();
        }
    }

}
