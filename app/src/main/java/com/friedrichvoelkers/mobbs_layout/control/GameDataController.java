package com.friedrichvoelkers.mobbs_layout.control;

import com.friedrichvoelkers.mobbs_layout.model.GameData;
import com.friedrichvoelkers.mobbs_layout.model.GameStatus;
import com.friedrichvoelkers.mobbs_layout.model.GlobalState;
import com.friedrichvoelkers.mobbs_layout.model.Message;
import com.friedrichvoelkers.mobbs_layout.model.User;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class GameDataController {

    /**
     * Checks if the Game ID already exists.
     *
     * @param gameId The Game ID to be checked
     * @return Returns true if the game ID already exists.
     */
    public static boolean gameIdExistsAlready (String gameId) {
        return GlobalState.getInstance().getAllExistingGames().containsKey(gameId);
    }

    /**
     * Creates a new game.
     *
     * @param username            Player username
     * @param gameId              Game-ID
     * @param bluetoothDeviceName Bluetooth device name
     * @param testMode            True when the method is tested. No transmission to the database
     *                            takes place.
     * @throws GameDataException Throws an exception if a game has already been created.
     */
    public static void createNewGame (String username, String gameId, String bluetoothDeviceName,
                                      boolean testMode) throws GameDataException {

        if (GlobalState.getInstance().getGameData() != null) {
            throw new GameDataException("Game already created.");
        }

        int userId = username.hashCode();
        User user = new User(true, userId, bluetoothDeviceName, username);
        GameData gameData = new GameData(Integer.parseInt(gameId));
        GlobalState.getInstance().setMyUserData(user);
        GlobalState.getInstance().setGameData(gameData);
        if (!testMode) {
            FireBaseController.createNewGame();
        }
    }

    /**
     * Join a game.
     *
     * @param username            Player username
     * @param gameId              Game-ID
     * @param bluetoothDeviceName Bluetooth device name
     * @param testMode            True when the method is tested. No transmission to the database
     *                            takes place.
     * @throws GameDataException Throws an exception if a game has already been joined.
     */
    public static void joinGame (String username, String gameId, String bluetoothDeviceName,
                                 boolean testMode) throws GameDataException {

        if (GlobalState.getInstance().getMyUserData() != null) {
            throw new GameDataException("Local user already exists.");
        }

        int userId = username.hashCode();
        User user = new User(false, userId, bluetoothDeviceName, username);
        GlobalState.getInstance().setMyUserData(user);
        if (!testMode) {
            FireBaseController.joinGame(gameId);
        }
    }

    /**
     * Checks if the game already has this Bluetooth device name.
     *
     * @param gameId              Game-ID
     * @param bluetoothDeviceName Bluetooth device name
     * @return Returns true if the Bluetooth device name is already assigned.
     */
    public static boolean bluetoothDeviceNameExistsAlready (String gameId,
                                                            String bluetoothDeviceName) {
        return Objects.requireNonNull(GlobalState.getInstance().getAllExistingGames().get(gameId))
                .containsValue(bluetoothDeviceName);
    }

    /**
     * Checks if the game already has this username.
     *
     * @param gameId   Game-ID
     * @param username Player username
     * @return Returns true if the username is already assigned.
     */
    public static boolean usernameExistsAlready (String gameId, String username) {
        return Objects.requireNonNull(GlobalState.getInstance().getAllExistingGames().get(gameId))
                .containsKey(username);
    }

    /**
     * Checks if the username and game ID are semantically correct.
     *
     * @param username Player username
     * @param gamId    Game-ID
     * @throws GameDataException Throw an exception if the input is not correct.
     */
    public static void usernameAndGameIdAreSetCorrect (String username, String gamId)
            throws GameDataException {

        if (username.equals("") || gamId.equals("")) {
            throw new GameDataException("Invalid username or gameId.");
        }

    }

    /**
     * Calculates all times when the location of Mr. X is updated.
     *
     * @throws GameDataException Throws an error if the start time of the game was not initialized.
     */
    public static void generateMrXStartTimes () throws GameDataException {

        if (GlobalState.getInstance().getStartTime() == null) {
            throw new GameDataException("Start time is not initialized");
        }

        long startTime = GlobalState.getInstance().getStartTime();
        long endTime = startTime +
                ((long) GlobalState.getInstance().getGameData().getGameDuration() * 60 * 1000);
        long interval =
                (long) GlobalState.getInstance().getGameData().getShowMrXInterval() * 60 * 1000;

        for (long i = startTime; i < endTime; i = i + interval) {
            GlobalState.getInstance().addMrXStartTime(i);
        }
    }

    /**
     * Returns the last time the location of Mr. X was updated. This is needed when the activity
     * is changed and the countdown is started again.
     *
     * @return The time when Mr. X was last seen.
     */
    public static long getLastMrXStartTime () {
        long currentTime = System.currentTimeMillis();

        for (int i = 0; i < GlobalState.getInstance().getMrXStartTimes().size() - 1; i++) {
            if (GlobalState.getInstance().getMrXStartTimes().get(i) < currentTime &&
                    GlobalState.getInstance().getMrXStartTimes().get(i + 1) > currentTime) {
                return GlobalState.getInstance().getMrXStartTimes().get(i);
            }
        }

        return GlobalState.getInstance().getMrXStartTimes()
                .get(GlobalState.getInstance().getMrXStartTimes().size() - 1);
    }

    /**
     * Updates the position of a player.
     *
     * @param value The player whose position is to be changed.
     * @throws GameDataException Throws an exception if the player was not found.
     */
    public static void updateSingleUserLocation (User value) throws GameDataException {
        for (User user : GlobalState.getInstance().getAllUserData()) {
            if (user.getUserID() == value.getUserID()) {
                user.setLatitude(value.getLatitude());
                user.setLongitude(value.getLongitude());

                if (user.getUserID() == GlobalState.getInstance().getMyUserData().getUserID()) {
                    GlobalState.getInstance().getMyUserData().setLatitude(value.getLatitude());
                    GlobalState.getInstance().getMyUserData().setLongitude(value.getLongitude());
                }
                return;
            }
        }
        throw new GameDataException("User not found.");
    }

    /**
     * Updates the position of all players on the map.
     *
     * @param allUserSymbols All symbols on the Mapbox map
     * @param symbolManager  Mapbox Map IconManager
     * @param updateMrX      Specifies whether the position of Mr. X should also be updated
     * @throws GameDataException Throws an error if the user ID of Mr. X is not found.
     */
    public static void updateSingleUserLocation (HashMap<User, Symbol> allUserSymbols,
                                                 SymbolManager symbolManager, boolean updateMrX)
            throws GameDataException {

        int userIdMrX = GameDataController.getMrXUserId();

        for (java.util.Map.Entry<User, Symbol> user : allUserSymbols.entrySet()) {

            if (updateMrX && user.getKey().getUserID() != userIdMrX) continue;

            if (!updateMrX && user.getKey().getUserID() == userIdMrX) continue;

            int id = user.getKey().getUserID();
            for (User user2 : GlobalState.getInstance().getAllUserData()) {
                if (user2.getUserID() == id) {
                    user.getValue()
                            .setLatLng(new LatLng(user2.getLatitude(), user2.getLongitude()));
                    break;
                }
            }
            symbolManager.update(user.getValue());
        }
    }

    /**
     * Returns user ID of Mr. X
     *
     * @return Mr. X User ID
     * @throws GameDataException Throws an exception if Mr. X was not found.
     */
    public static int getMrXUserId () throws GameDataException {

        String mrX = GlobalState.getInstance().getGameData().getMrX();

        for (User user : GlobalState.getInstance().getAllUserData()) {
            if (Objects.equals(user.getUsername(), mrX)) return user.getUserID();
        }

        throw new GameDataException("Game ID from Mr. X not found.");

    }

    /**
     * Checks via bluetooth device name if Mr. X was found
     *
     * @param name Bluetooth device name
     * @return Returns true if Mr. X was found
     */
    public static boolean didIFoundMrX (String name) {
        String mrX = GlobalState.getInstance().getGameData().getMrX();
        String mrXBluetoothName = null;

        for (User user : GlobalState.getInstance().getAllUserData()) {

            if (Objects.equals(user.getUsername(), mrX)) {
                mrXBluetoothName = user.getBluetoothDeviceName();
            }
        }
        return Objects.equals(name, mrXBluetoothName);
    }

    /**
     * Checks if you are Mr. X yourself
     *
     * @return Returns true if you are Mr. X yourself
     */
    public static boolean amIMrX () {
        return GlobalState.getInstance().getMyUserData().getUsername().trim()
                .equalsIgnoreCase(GlobalState.getInstance().getGameData().getMrX().trim());
    }

    /**
     * Returns all players
     *
     * @return ArrayList with all players
     */
    public static List<User> getAllUser () {
        return GlobalState.getInstance().getAllUserData();
    }

    /**
     * Returns the game duration
     *
     * @return Duration of the game in minutes
     */
    public static int getGameDuration () {
        return GlobalState.getInstance().getGameData().getGameDuration();
    }

    /**
     * Returns how often the location of Mr. X is updated
     *
     * @return The time interval in which the location of Mr. X is updated in minutes.
     */
    public static int getShowMrXInterval () {
        return GlobalState.getInstance().getGameData().getShowMrXInterval();
    }

    /**
     * Returns all messages
     *
     * @return ArrayList with all messages
     */
    public static ArrayList<Message> getAllMessages () {
        return GlobalState.getInstance().getAllMessages();
    }

    /**
     * Returns the GameStatus
     *
     * @return Current GameStatus
     */
    public static GameStatus getGameStatus () {
        return GlobalState.getInstance().getGameData().getGameStatus();
    }

    /**
     * Returns a player if you enter the username
     *
     * @param username Username of the player
     * @return User
     * @throws GameDataException Throws an exception if the player is not found
     */
    public static User getUserByName (String username) throws GameDataException {
        for (User user : GlobalState.getInstance().getAllUserData()) {
            if (user.getUsername().equals(username)) return user;
        }
        throw new GameDataException("User not found.");
    }
}
