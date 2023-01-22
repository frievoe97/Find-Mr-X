package com.friedrichvoelkers.mobbs_layout.model;

import com.friedrichvoelkers.mobbs_layout.control.GameDataException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Observable;

public class GlobalState extends Observable {

    private static GlobalState globalState;
    private final ArrayList<Message> allMessages = new ArrayList<>();
    private final List<User> allUserData = new ArrayList<>();
    private final HashMap<String, HashMap<String, String>> allExistingGames = new HashMap<>();
    private GameData gameData;
    private User myUserData;
    private Long startTime;
    private final ArrayList<Long> mrXStartTimes = new ArrayList<>();

    public static GlobalState getInstance () {
        if (globalState == null) {
            globalState = new GlobalState();
        }
        return globalState;
    }

    public void addMrXStartTime (long time) {
        globalState.mrXStartTimes.add(time);
    }

    public ArrayList<Long> getMrXStartTimes () {
        return globalState.mrXStartTimes;
    }

    public Long getStartTime () {
        return globalState.startTime;
    }

    public void setStartTime (long startTime) {
        globalState.startTime = startTime;
    }

    public ArrayList<Message> getAllMessages () {
        return globalState.allMessages;
    }

    public List<User> getAllUserData () {
        return allUserData;
    }

    public User getMyUserData () {
        return globalState.myUserData;
    }

    public void setMyUserData (User user) {
        globalState.myUserData = user;
    }

    public GameData getGameData () {
        return globalState.gameData;
    }

    public void setGameData (GameData gameData) {
        globalState.gameData = gameData;
        setChanged();
        notifyObservers("new_game_status");
    }

    public void addExistingGameId (String gameId) {
        globalState.allExistingGames.put(gameId, new HashMap<>());
    }

    public void addExistingUsernameAndBluetoothName (String gameId, String username,
                                                     String bluetoothName)
            throws GameDataException {

        if (!globalState.getAllExistingGames().containsKey(gameId)) {
            throw new GameDataException("Add the game id before you add the users.");
        }

        Objects.requireNonNull(globalState.allExistingGames.get(gameId))
                .put(username, bluetoothName);
    }

    public HashMap<String, HashMap<String, String>> getAllExistingGames () {
        return globalState.allExistingGames;
    }

    public void addUser (User user) {
        globalState.allUserData.add(user);
        setChanged();
        notifyObservers("new_user");
    }

    public void addMessage (Message message) {
        globalState.allMessages.add(message);
        setChanged();
        notifyObservers("new_message");
    }

    public void delete() {
        globalState = null;
    }
}
