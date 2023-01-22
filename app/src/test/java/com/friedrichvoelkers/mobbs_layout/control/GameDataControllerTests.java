package com.friedrichvoelkers.mobbs_layout.control;

import com.friedrichvoelkers.mobbs_layout.model.GameData;
import com.friedrichvoelkers.mobbs_layout.model.GlobalState;
import com.friedrichvoelkers.mobbs_layout.model.User;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GameDataControllerTests {

    private static final int START_TIME = 10000000;
    private static final int SECONDS_PER_MINUTE = 60;
    private static final int MILLISECONDS_PER_SECOND = 1000;

    @Before
    public void clear () {
        GlobalState.getInstance().delete();
    }

    @Test
    public void createNewGameTest1 () throws GameDataException {
        GameDataController.createNewGame("friedrich", "1234", "samsung-s9", true);
    }

    @Test (expected = GameDataException.class)
    public void createNewGameTest2 () throws GameDataException {
        GameDataController.createNewGame("friedrich", "1234", "samsung-s9", true);
        GameDataController.createNewGame("friedrich", "1234", "samsung-s9", true);
    }

    @Test
    public void createNewGameTest3() {
        Assert.assertNull(GlobalState.getInstance().getGameData());
    }

    @Test
    public void createNewGameTest4 () throws GameDataException {
        Assert.assertNull(GlobalState.getInstance().getGameData());
        GameDataController.createNewGame("friedrich", "1234", "samsung-s9", true);
        Assert.assertNotNull(GlobalState.getInstance().getGameData());
    }

    @Test
    public void addUserTest1 () throws GameDataException {
        GameDataController.joinGame("friedrich", "1234", "samsung-s9", true);
    }

    @Test (expected = GameDataException.class)
    public void addUserTest2 () throws GameDataException {
        GameDataController.joinGame("friedrich", "1234", "samsung-s9", true);
        GameDataController.joinGame("friedrich", "1234", "samsung-s9", true);
    }

    @Test
    public void addUserTest3 () throws GameDataException {
        GlobalState.getInstance().addExistingGameId("1234");
        GlobalState.getInstance()
                .addExistingUsernameAndBluetoothName("1234", "friedrich", "samsung-s9");
        GlobalState.getInstance().addExistingUsernameAndBluetoothName("1234", "daniel", "iphone-4");
        GlobalState.getInstance()
                .addExistingUsernameAndBluetoothName("1234", "sabine", "nokiaPhone");
        GlobalState.getInstance().addExistingUsernameAndBluetoothName("1234", "Moritz", "blauzahn");
    }

    @Test
    public void usernameAndGameIdAreSetCorrectTest1 () throws GameDataException {
        GameDataController.usernameAndGameIdAreSetCorrect("friedrich", "1234");
    }

    @Test (expected = GameDataException.class)
    public void usernameAndGameIdAreSetCorrectTest2 () throws GameDataException {
        GameDataController.usernameAndGameIdAreSetCorrect("", "1234");
    }

    @Test (expected = GameDataException.class)
    public void usernameAndGameIdAreSetCorrectTest3 () throws GameDataException {
        GameDataController.usernameAndGameIdAreSetCorrect("friedrich", "");
    }

    @Test (expected = GameDataException.class)
    public void usernameAndGameIdAreSetCorrectTest4 () throws GameDataException {
        GameDataController.usernameAndGameIdAreSetCorrect("", "");
    }

    @Test
    public void generateMrXStartTimesTest1 () throws GameDataException {
        GlobalState.getInstance().setStartTime(START_TIME);
        GlobalState.getInstance().setGameData(new GameData());
        GlobalState.getInstance().getGameData().setGameDuration(10);
        GlobalState.getInstance().getGameData().setShowMrXInterval(2);
        GameDataController.generateMrXStartTimes();
    }

    @Test (expected = GameDataException.class)
    public void generateMrXStartTimesTest2 () throws GameDataException {
        GameDataController.generateMrXStartTimes();
    }

    @Test
    public void generateMrXStartTimesTest3 () throws GameDataException {
        GlobalState.getInstance().setStartTime(START_TIME);
        GlobalState.getInstance().setGameData(new GameData());
        GlobalState.getInstance().getGameData().setGameDuration(10);
        GlobalState.getInstance().getGameData().setShowMrXInterval(2);
        GameDataController.generateMrXStartTimes();
        Assert.assertEquals(GlobalState.getInstance().getMrXStartTimes().size(), 5);
    }

    @Test
    public void generateMrXStartTimesTest4 () throws GameDataException {
        GlobalState.getInstance().setStartTime(START_TIME);
        GlobalState.getInstance().setGameData(new GameData());
        GlobalState.getInstance().getGameData().setGameDuration(10);
        GlobalState.getInstance().getGameData().setShowMrXInterval(11);
        GameDataController.generateMrXStartTimes();
        Assert.assertEquals(GlobalState.getInstance().getMrXStartTimes().size(), 1);
    }

    @Test
    public void generateMrXStartTimesTest5 () throws GameDataException {
        GlobalState.getInstance().setStartTime(START_TIME);
        GlobalState.getInstance().setGameData(new GameData());
        GlobalState.getInstance().getGameData().setGameDuration(10);
        GlobalState.getInstance().getGameData().setShowMrXInterval(3);
        GameDataController.generateMrXStartTimes();
        Assert.assertEquals(GlobalState.getInstance().getMrXStartTimes().size(), 4);
    }

    @Test
    public void getLastMrXStartTimeTest1 () throws GameDataException {
        GlobalState.getInstance().setStartTime(START_TIME);
        GlobalState.getInstance().setGameData(new GameData());
        GlobalState.getInstance().getGameData().setGameDuration(10);
        GlobalState.getInstance().getGameData().setShowMrXInterval(3);
        GameDataController.generateMrXStartTimes();
        System.out.println(GlobalState.getInstance().getMrXStartTimes());
        Assert.assertEquals(GameDataController.getLastMrXStartTime(),
                START_TIME + (3 * 3 * SECONDS_PER_MINUTE * MILLISECONDS_PER_SECOND));
    }

    private void setUser () {
        GlobalState.getInstance().setMyUserData(new User(true, 10000, "galaxy4a", "Friedrich"));
        GlobalState.getInstance().addUser(new User(true, 10000, "galaxy4a", "Friedrich"));
        GlobalState.getInstance().addUser(new User(false, 10001, "iphone12", "Martin"));
        GlobalState.getInstance().addUser(new User(false, 10002, "nokia1234", "Tobias"));
        GlobalState.getInstance().addUser(new User(false, 10003, "samsung-a3", "Sabine"));
        GlobalState.getInstance().setGameData(new GameData());
        GlobalState.getInstance().getGameData().setMrX("Sabine");
        GlobalState.getInstance().getGameData().setGameId(676732);
    }

    @Test
    public void getUserByNameTest1 () throws GameDataException {
        this.setUser();
        GameDataController.getUserByName("Friedrich");
    }

    @Test (expected = GameDataException.class)
    public void getUserByNameTest2 () throws GameDataException {
        this.setUser();
        GameDataController.getUserByName("Carlo");
    }

    @Test
    public void updateUserLocationTest1 () throws GameDataException {
        this.setUser();
        User correctUser = new User(true, 10000, "friedrich", "Friedrich");
        correctUser.setLatitude(100);
        correctUser.setLongitude(200);
        GameDataController.updateUserLocation(correctUser);
    }

    @Test (expected = GameDataException.class)
    public void updateUserLocationTest2 () throws GameDataException {
        this.setUser();
        User incorrectUser = new User(true, 10004, "thomas", "Tommi");
        incorrectUser.setLatitude(100);
        incorrectUser.setLongitude(200);
        GameDataController.updateUserLocation(incorrectUser);
    }

    @Test
    public void getMrXGameIdTest1 () throws GameDataException {
        this.setUser();
        Assert.assertEquals(GameDataController.getMrXUserId(), 10003);
    }

    @Test
    public void getMrXGameIdTest2 () throws GameDataException {
        this.setUser();
        Assert.assertEquals(GameDataController.getMrXUserId(), 10003);
        GlobalState.getInstance().getGameData().setMrX("Friedrich");
        Assert.assertEquals(GameDataController.getMrXUserId(), 10000);
    }

    @Test
    public void didIFoundMrXTest1() {
        this.setUser();
        Assert.assertTrue(GameDataController.didIFoundMrX("samsung-a3"));
    }

    @Test
    public void didIFoundMrXTest2() {
        this.setUser();
        Assert.assertFalse(GameDataController.didIFoundMrX("galaxy4a"));
    }

    @Test
    public void didIFoundMrXTest3() {
        this.setUser();
        Assert.assertFalse(GameDataController.didIFoundMrX("iphone12"));
    }

    @Test
    public void didIFoundMrXTest4() {
        this.setUser();
        Assert.assertFalse(GameDataController.didIFoundMrX("nokia1234"));
    }

    @Test
    public void amIMrXTest1() {
        this.setUser();
        Assert.assertFalse(GameDataController.amIMrX());
    }

    @Test
    public void amIMrXTest2() {
        this.setUser();
        Assert.assertFalse(GameDataController.amIMrX());
        GlobalState.getInstance().getGameData().setMrX("Friedrich");
        Assert.assertTrue(GameDataController.amIMrX());
    }



}