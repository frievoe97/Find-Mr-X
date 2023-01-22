package com.friedrichvoelkers.mobbs_layout.model;

public class Message {

    private String username;
    private int timestamp;
    private String message;

    public Message (String username, int timestamp, String message) {
        this.username = username;
        this.timestamp = timestamp;
        this.message = message;
    }

    public Message () {

    }

    public String getUsername () {
        return username;
    }

    public void setUsername (String username) {
        this.username = username;
    }

    public int getTimestamp () {
        return timestamp;
    }

    public void setTimestamp (int timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage () {
        return message;
    }

    public void setMessage (String message) {
        this.message = message;
    }

    @Override
    public String toString () {
        return "Message{" + "username='" + username + '\'' + ", timestamp=" + timestamp +
                ", message='" + message + '\'' + '}';
    }
}
