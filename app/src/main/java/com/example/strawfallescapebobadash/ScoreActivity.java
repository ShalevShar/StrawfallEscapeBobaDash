package com.example.strawfallescapebobadash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import com.example.strawfallescapebobadash.Fragments.fragment_google_map;
import com.example.strawfallescapebobadash.Fragments.fragment_scores;

public class ScoreActivity extends AppCompatActivity implements ScoreAdapter.OnMapLocationClickListener {

    private AppCompatButton score_BTN_exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        findViews();
        setFrames();

        score_BTN_exit.setOnClickListener(v -> mainActivity());
    }

    private void setFrames() {
        // Instantiate the fragment_scores fragment
        fragment_scores scoresFragment = new fragment_scores();

        // Begin the fragment transaction for scoresFragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction scoreTransaction = fragmentManager.beginTransaction();
        scoreTransaction.replace(R.id.score_fragment_container, scoresFragment); // Replace the container with the scoresFragment
        scoreTransaction.addToBackStack(null); // Add transaction to back stack
        scoreTransaction.commit(); // Commit the transaction

        // Instantiate the google_map_fragment
        fragment_google_map mapFragment = new fragment_google_map();

        // Begin the fragment transaction for mapFragment
        FragmentTransaction mapTransaction = fragmentManager.beginTransaction();
        mapTransaction.replace(R.id.map_fragment_container, mapFragment); // Replace the container with the mapFragment
        mapTransaction.addToBackStack(null); // Add transaction to back stack
        mapTransaction.commit(); // Commit the transaction
    }

    public void findViews(){
        score_BTN_exit = findViewById(R.id.score_BTN_exit);
    }

    private void mainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onMapLocationClick(double latitude, double longitude, String name) {
        // Get reference to the map fragment
        fragment_google_map mapFragment = (fragment_google_map) getSupportFragmentManager().findFragmentById(R.id.map_fragment_container);
        // Call setMapLocation method
        if (mapFragment != null) {
            mapFragment.setMapLocation(latitude, longitude, name);
        }
    }
}
