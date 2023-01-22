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
     *
     * @param text
     * @return
     */
    public static boolean gameIdExistsAlready (String text) {
        return GlobalState.getInstance().getAllExistingGames().containsKey(text);
    }

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

    public static boolean bluetoothDeviceNameExistsAlready (String gameId,
                                                            String bluetoothDeviceName) {
        return Objects.requireNonNull(GlobalState.getInstance().getAllExistingGames().get(gameId))
                .containsValue(bluetoothDeviceName);
    }

    public static boolean usernameExistsAlready (String gameId, String bluetoothDeviceName) {
        return Objects.requireNonNull(GlobalState.getInstance().getAllExistingGames().get(gameId))
                .containsKey(bluetoothDeviceName);
    }

    public static boolean usernameAndGameIdAreSetCorrect (String username, String gamId)
            throws GameDataException {

        if (username.equals("") || gamId.equals("")) {
            throw new GameDataException("Invalid username or gameId.");
        }

        return true;
    }

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

    public static void updateUserLocation (User value) throws GameDataException {
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

    public static void updateUserLocation (HashMap<User, Symbol> allUserSymbols,
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

    public static int getMrXUserId () throws GameDataException {

        String mrX = GlobalState.getInstance().getGameData().getMrX();

        for (User user : GlobalState.getInstance().getAllUserData()) {
            if (Objects.equals(user.getUsername(), mrX)) return user.getUserID();
        }

        throw new GameDataException("Game ID from Mr. X not found.");

    }

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

    public static boolean amIMrX () {
        return GlobalState.getInstance().getMyUserData().getUsername().trim()
                .equalsIgnoreCase(GlobalState.getInstance().getGameData().getMrX().trim());
    }

    public static List<User> getAllUser () {
        return GlobalState.getInstance().getAllUserData();
    }

    public static int getGameDuration () {
        return GlobalState.getInstance().getGameData().getGameDuration();
    }

    public static int getShowMrXInterval () {
        return GlobalState.getInstance().getGameData().getShowMrXInterval();
    }

    public static ArrayList<Message> getAllMessages () {
        return GlobalState.getInstance().getAllMessages();
    }

    public static GameStatus getGameStatus () {
        return GlobalState.getInstance().getGameData().getGameStatus();
    }

    public static User getUserByName (String username) throws GameDataException {
        for (User user : GlobalState.getInstance().getAllUserData()) {
            if (user.getUsername().equals(username)) return user;
        }
        throw new GameDataException("User not found.");
    }
}
