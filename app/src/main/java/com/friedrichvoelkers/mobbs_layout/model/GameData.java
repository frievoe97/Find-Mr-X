package com.friedrichvoelkers.mobbs_layout.model;

public class GameData {

    private int gameId;
    private Integer gameDuration = 30;
    private Integer showMrXInterval = 5;
    private GameStatus gameStatus = GameStatus.JOINING;
    private String mrX;

    public GameData (int gameID) {
        this.gameId = gameID;
    }

    public GameData () {
    }

    public int getGameId () {
        return this.gameId;
    }

    public void setGameId (int gameID) {
        this.gameId = gameID;
    }

    public Integer getGameDuration () {
        return gameDuration;
    }

    public void setGameDuration (Integer gameDuration) {
        this.gameDuration = gameDuration;
    }

    public Integer getShowMrXInterval () {
        return showMrXInterval;
    }

    public void setShowMrXInterval (Integer showMrXInterval) {
        this.showMrXInterval = showMrXInterval;
    }

    public GameStatus getGameStatus () {
        return gameStatus;
    }

    public void setGameStatus (GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public String getMrX () {
        return mrX;
    }

    public void setMrX (String mrX) {
        this.mrX = mrX;
    }
}
