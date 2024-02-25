package com.example.strawfallescapebobadash;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SharedPreferencesManager {
    private static final String PREF_NAME = "PlayerScores";
    private static final String KEY_ALL_PLAYER_SCORES = "allPlayerScores";

    public static void savePlayerScore(Context context, PlayerScore playerScore) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Retrieve the existing list of PlayerScores
        List<PlayerScore> allPlayerScores = getAllPlayerScores(context);

        // Append the new PlayerScore to the existing list
        allPlayerScores.add(playerScore);

        // Save the updated list back to SharedPreferences
        saveAllPlayerScores(context, allPlayerScores);
    }

    public static PlayerScore getPlayerRecord(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        List<PlayerScore> allPlayerScores = getAllPlayerScores(context);
        // Assuming the latest player score is at the end of the list
        return allPlayerScores.isEmpty() ? null : allPlayerScores.get(allPlayerScores.size() - 1);
    }

    public static List<PlayerScore> getAllPlayerScores(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(KEY_ALL_PLAYER_SCORES, "");
        if (!json.isEmpty()) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<PlayerScore>>(){}.getType();
            return gson.fromJson(json, type);
        }
        return new ArrayList<>();
    }

    public static void saveAllPlayerScores(Context context, List<PlayerScore> playerScores) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(playerScores);
        editor.putString(KEY_ALL_PLAYER_SCORES, json);
        editor.apply();
    }

    public static boolean isPlayerNameExists(Context context, String playerName) {
        List<PlayerScore> playerScores = getAllPlayerScores(context);
        for (PlayerScore playerScore : playerScores) {
            if (playerScore.getPlayerName().equals(playerName)) {
                return true; // Player name exists
            }
        }
        return false; // Player name does not exist
    }
}
