package com.example.strawfallescapebobadash.GlobalScores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.strawfallescapebobadash.R;

import java.util.Collections;
import java.util.List;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder> {

    private static List<PlayerScore> playerScores;
    private OnMapLocationClickListener listener;

    public ScoreAdapter(List<PlayerScore> playerScores, OnMapLocationClickListener listener) {
        this.playerScores = playerScores;
        this.listener = listener;
        // Sort the list by score in descending order
        Collections.sort(playerScores, (score1, score2) -> Integer.compare(score2.getScore(), score1.getScore()));
        // Assign ranks to each player
        assignRanks();
    }

    @NonNull
    @Override
    public ScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_score, parent, false);
        return new ScoreViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreViewHolder holder, int position) {
        PlayerScore playerScore = playerScores.get(position);
        holder.single_score_numerate.setText(String.valueOf(playerScore.getRank())); // Set rank value
        holder.single_score_player.setText(playerScore.getPlayerName());
        holder.single_score_result.setText(String.valueOf(playerScore.getScore()));
    }


    @Override
    public int getItemCount() {
        return Math.min(playerScores.size(), 10);
    }

    private void assignRanks() {
        for (int i = 0; i < playerScores.size(); i++) {
            playerScores.get(i).setRank(i + 1);
        }
    }


    static class ScoreViewHolder extends RecyclerView.ViewHolder {
        TextView single_score_numerate;
        TextView single_score_player;
        TextView single_score_result;
        AppCompatImageButton single_score_map;
        private OnMapLocationClickListener listener;

        public ScoreViewHolder(@NonNull View itemView, OnMapLocationClickListener listener) {
            super(itemView);
            single_score_numerate = itemView.findViewById(R.id.single_score_numerate);
            single_score_player = itemView.findViewById(R.id.single_score_player);
            single_score_result = itemView.findViewById(R.id.single_score_result);
            single_score_map = itemView.findViewById(R.id.single_score_map);

            this.listener = listener;
            single_score_map.setOnClickListener(v -> {
                PlayerScore playerScore = playerScores.get(getAdapterPosition());
                double latitude = playerScore.getLatitude();
                double longitude = playerScore.getLongitude();
                String name = playerScore.getPlayerName();
                // Trigger the interface callback using the listener instance
                listener.onMapLocationClick(latitude, longitude, name);
            });
        }
    }

    public interface OnMapLocationClickListener {
        void onMapLocationClick(double latitude, double longitude, String name);
    }

}