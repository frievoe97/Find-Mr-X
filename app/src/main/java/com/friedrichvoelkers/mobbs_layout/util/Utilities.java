package com.friedrichvoelkers.mobbs_layout.util;

import com.friedrichvoelkers.mobbs_layout.model.User;

import java.util.ArrayList;
import java.util.List;

final public class Utilities {

    public static String convertTimeInMillisecondsToCountdownString (long time, boolean useDots) {
        String minutes = String.valueOf((time / 1000) / 60);
        String seconds = String.valueOf((time / 1000) % 60);

        if (seconds.length() == 1) {
            seconds = "0" + seconds;
        }

        if (useDots) {
            return minutes + ":" + seconds;
        } else {
            return minutes + " " + seconds;
        }
    }

    public static int concertTimeLeftToProgressBar (long remainingTime, int completeTime) {

        double result = (100.0 / completeTime) * remainingTime;

        return (int) result;

    }

    public static ArrayList<String> createNewArrayListFromInteger (int start, int end,
                                                                   int interval) {
        ArrayList<String> returnArrayList = new ArrayList<>();
        for (int i = start; i <= end; i = i + interval) {
            returnArrayList.add(String.valueOf(i));
        }
        return returnArrayList;
    }

    public static ArrayList<String> createNewArrayListFromUser (List<User> user) {
        ArrayList<String> returnArrayList = new ArrayList<>();
        for (User userElement : user) {
            returnArrayList.add(userElement.getUsername());
        }
        return returnArrayList;
    }

}
