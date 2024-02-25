package com.example.strawfallescapebobadash.GlobalScores;

public class PlayerScore {
    private String playerName;
    private int score;
    private double latitude;
    private double longitude;
    private int rank;



    public PlayerScore(String playerName, int score, double latitude, double longitude) {
        this.playerName = playerName;
        this.score = score;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getRank() {
        return rank;
    }

    public PlayerScore setRank(int rank) {
        this.rank = rank;
        return this;
    }

    public PlayerScore setPlayerName(String playerName) {
        this.playerName = playerName;
        return this;
    }

    public PlayerScore setScore(int score) {
        this.score = score;
        return this;
    }

    public PlayerScore setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public PlayerScore setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
