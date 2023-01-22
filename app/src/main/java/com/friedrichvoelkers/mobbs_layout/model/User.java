package com.friedrichvoelkers.mobbs_layout.model;

public class User {

    private double longitude;
    private double latitude;
    private boolean iAmMrX = false;
    private boolean iAmTheGameMaster;
    private int userID;
    private String bluetoothDeviceName;
    private String username;

    public User (boolean iAmTheGameMaster, int userID, String bluetoothDeviceName,
                 String username) {
        this.iAmTheGameMaster = iAmTheGameMaster;
        this.userID = userID;
        this.bluetoothDeviceName = bluetoothDeviceName;
        this.username = username;
    }

    public User () {
    }

    public int getUserID () {
        return userID;
    }

    public void setUserID (int userID) {
        this.userID = userID;
    }

    public double getLongitude () {
        return longitude;
    }

    public void setLongitude (double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude () {
        return latitude;
    }

    public void setLatitude (double latitude) {
        this.latitude = latitude;
    }

    public boolean isiAmMrX () {
        return iAmMrX;
    }

    public void setiAmMrX (boolean iAmMrX) {
        this.iAmMrX = iAmMrX;
    }

    public boolean isiAmTheGameMaster () {
        return iAmTheGameMaster;
    }

    public void setiAmTheGameMaster (boolean iAmTheGameMaster) {
        this.iAmTheGameMaster = iAmTheGameMaster;
    }

    public String getBluetoothDeviceName () {
        return bluetoothDeviceName;
    }

    public void setBluetoothDeviceName (String bluetoothDeviceName) {
        this.bluetoothDeviceName = bluetoothDeviceName;
    }

    public String getUsername () {
        return username;
    }

    public void setUsername (String username) {
        this.username = username;
    }

    @Override
    public String toString () {
        return "User{" + "longitude=" + longitude + ", latitude=" + latitude + ", iAmMrX=" +
                iAmMrX + ", iAmTheGameMaster=" + iAmTheGameMaster + ", userID=" + userID +
                ", bluetoothDeviceName='" + bluetoothDeviceName + '\'' + ", username='" + username +
                '\'' + '}';
    }
}
