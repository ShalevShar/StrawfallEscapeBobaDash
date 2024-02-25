package com.example.strawfallescapebobadash.Logic;
import android.os.Handler;
import android.view.View;
import android.widget.GridLayout;
import com.google.android.material.imageview.ShapeableImageView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class FallingElementsHandler {
    private List<ShapeableImageView> strawViewsAll;
    private static List<ShapeableImageView> strawView1;
    private static List<ShapeableImageView> strawView2;
    private static List<ShapeableImageView> strawView3;
    private static List<ShapeableImageView> strawView4;
    private static List<ShapeableImageView> strawView5;
    private List<ShapeableImageView> coinViewsAll;
    private static List<ShapeableImageView> coinView1;
    private static List<ShapeableImageView> coinView2;
    private static List<ShapeableImageView> coinView3;
    private static List<ShapeableImageView> coinView4;
    private static List<ShapeableImageView> coinView5;
    private static int lastRandomRow;
    private static int maxColumns = 5;
    private static List<Boolean> lastRowStraws;
    private static List<Boolean> lastRowCoins;
    private static boolean shouldContinue;
    private List<Integer> lastVisiblePositions = new ArrayList<>(Collections.nCopies(maxColumns, -1));
    private Handler handler = new Handler();
    private Runnable runnable;
    private Handler mainHandler = new Handler();
    private static final int ELEMENT_SPEED_MS = 700;

    public FallingElementsHandler(GridLayout strawGridLayout, GridLayout coinGridLayout) {
        manageStraws(strawGridLayout);
        manageCoins(coinGridLayout);
        lastRandomRow = -1;
        lastRowStraws = new ArrayList<>();
        IntStream.range(0, 5).forEach(i -> lastRowStraws.add(false));
        lastRowCoins = new ArrayList<>();
        IntStream.range(0, 5).forEach(i -> lastRowCoins.add(false));
        shouldContinue = true;
        initializeStrawViews();
        initializeCoinViews();
        startFlowingElements();
    }
    public void manageStraws(GridLayout strawGridLayout) {
        this.strawViewsAll = extractStrawViews(strawGridLayout);
        this.strawView1 = extractViewsForColumn(1, strawViewsAll);
        this.strawView2 = extractViewsForColumn(2, strawViewsAll);
        this.strawView3 = extractViewsForColumn(3, strawViewsAll);
        this.strawView4 = extractViewsForColumn(4, strawViewsAll);
        this.strawView5 = extractViewsForColumn(5, strawViewsAll);
    }
    public void manageCoins(GridLayout coinGridLayout) {
        this.coinViewsAll = extractStrawViews(coinGridLayout);
        this.coinView1 = extractViewsForColumn(1, coinViewsAll);
        this.coinView2 = extractViewsForColumn(2, coinViewsAll);
        this.coinView3 = extractViewsForColumn(3, coinViewsAll);
        this.coinView4 = extractViewsForColumn(4, coinViewsAll);
        this.coinView5 = extractViewsForColumn(5, coinViewsAll);
    }
    public void startFlowingElements() {
        runnable = new Runnable()  {
            @Override
            public void run() {
                if (shouldContinue) {
                    flowElementsToScreen();
                    handler.postDelayed(this, ELEMENT_SPEED_MS *2); // Adjust the delay time (in milliseconds) as needed
                }
            }
        };
        handler.postDelayed(runnable, ELEMENT_SPEED_MS); // Initial delay before starting the loop
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

    private List<ShapeableImageView> extractViewsForColumn(int column, List<ShapeableImageView> generalViews) {
        List<ShapeableImageView> strawViews = new ArrayList<>();
        int size = generalViews.size();
        int startIndex = column - 1;
        for (int i = startIndex; i < size; i += maxColumns) {
            strawViews.add(generalViews.get(i));
        }
        return strawViews;
    }

    private void initializeStrawViews() {
        for (ShapeableImageView strawView : strawViewsAll) {
            strawView.setVisibility(View.INVISIBLE);
        }
    }
    private void initializeCoinViews() {
        for (ShapeableImageView coinView : coinViewsAll) {
            coinView.setVisibility(View.INVISIBLE);
        }
    }

    private void flowElementsToScreen() {
        int randomElement = getRandomElement();
        if (randomElement == 0) {
            moveElemetsSequentially(false, 0, strawView1);
        } else if (randomElement == 1) {
            moveElemetsSequentially(false,1, strawView2);
        } else if (randomElement == 2){
            moveElemetsSequentially(false,2, strawView3);
        } else if (randomElement == 3){
            moveElemetsSequentially(false,3, strawView4);
        } else if (randomElement == 4) {
            moveElemetsSequentially(false,4, strawView5);
        } else if (randomElement == 5) {
            moveElemetsSequentially(true,0, coinView1);
        } else if (randomElement == 6) {
            moveElemetsSequentially(true,1, coinView2);
        } else if (randomElement == 7){
            moveElemetsSequentially(true,2, coinView3);
        } else if (randomElement == 8){
            moveElemetsSequentially(true,3, coinView4);
        } else
            moveElemetsSequentially(true,4, coinView5);
    }

    public static int getRandomElement() {
        Random random = new Random();
        int rand = random.nextInt((maxColumns * 2) -1); // Generate a random number between 0 and maxColumns*2
        if(lastRandomRow != -1){
            while(rand == lastRandomRow){
                rand = random.nextInt((maxColumns * 2) -1); // Ensure the new random number is different from the last one
            }
        }
        setLastRandomRow(rand);
        return rand;
    }

    private void moveElemetsSequentially(boolean isCoin, int column, final List<ShapeableImageView> elementView) {
        mainHandler.post(() -> {
            for (int i = 0; i < elementView.size(); i++) {
                final int index = i;
                mainHandler.postDelayed(() -> {
                    if (index > 0) {
                        elementView.get(index - 1).setVisibility(View.INVISIBLE);
                    }
                    elementView.get(index).setVisibility(View.VISIBLE);

                    if (index == elementView.size() - 1) {
                        if(isCoin){
                            lastRowCoins.set(column, true);
                        } else {
                            lastRowStraws.set(column, true);
                        }
                        mainHandler.postDelayed(() -> {
                            elementView.get(index).setVisibility(View.INVISIBLE);
                            if(isCoin){
                                lastRowCoins.set(column, false);
                            } else {
                                lastRowStraws.set(column, false);
                            }
                        }, ELEMENT_SPEED_MS);
                    }
                }, i * ELEMENT_SPEED_MS);
            }
        });
    }

    public List<Boolean> getLastRowStraws() {
        return lastRowStraws;
    }
    public List<Boolean> getLastRowCoins() {
        return lastRowCoins;
    }

    public static void setLastRandomRow(int lastRandomRow) {
        FallingElementsHandler.lastRandomRow = lastRandomRow;
    }
    public void pauseFlowingElements() {
        shouldContinue = false;
        handler.removeCallbacksAndMessages(null);
        mainHandler.removeCallbacksAndMessages(null);
    }
    public void resumeFlowingElements() {
        shouldContinue = true;
        updateLastVisiblePositions(); // Update last visible positions first
        startFlowingStrawsFromLastPosition();
    }
    public void stopFlowingElements() {
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
            startFlowingElements();
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
                        }, ELEMENT_SPEED_MS);
                    }
                }, (i - position) * ELEMENT_SPEED_MS);
            }
        });
    }

    private static List<ShapeableImageView> getStrawViewForColumn(int column) {
        switch (column) {
            case 0:
                return strawView1;
            case 1:
                return strawView2;
            case 2:
                return strawView3;
            case 3:
                return strawView4;
            case 4:
                return strawView5;
            default:
                return new ArrayList<>();
        }
    }

    private static List<ShapeableImageView> getCoinViewForColumn(int column) {
        switch (column) {
            case 0:
                return coinView1;
            case 1:
                return coinView2;
            case 2:
                return coinView3;
            case 3:
                return coinView4;
            case 4:
                return coinView5;
            default:
                return new ArrayList<>();
        }
    }

}
