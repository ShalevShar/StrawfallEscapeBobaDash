package com.example.strawfallescapebobadash.Fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.strawfallescapebobadash.GlobalScores.PlayerScore;
import com.example.strawfallescapebobadash.R;
import com.example.strawfallescapebobadash.GlobalScores.ScoreAdapter;
import com.example.strawfallescapebobadash.Utils.SharedPreferencesManager;
import java.util.List;

public class fragment_scores extends Fragment implements ScoreAdapter.OnMapLocationClickListener{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scores, container, false);

        // Retrieve all player scores
        List<PlayerScore> playerScores = SharedPreferencesManager.getAllPlayerScores(requireContext());

        // Initialize RecyclerView and set up adapter
        RecyclerView recyclerView = view.findViewById(R.id.score_RCV_scores);
        ScoreAdapter adapter = new ScoreAdapter(playerScores, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        return view;
    }

    @Override
    public void onMapLocationClick(double latitude, double longitude, String name) {
        // Get reference to the map fragment
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragment_google_map mapFragment = (fragment_google_map) fragmentManager.findFragmentById(R.id.map_fragment_container);

        // Call setMapLocation method
        if (mapFragment != null) {
            mapFragment.setMapLocation(latitude, longitude, name);
        }
    }
}