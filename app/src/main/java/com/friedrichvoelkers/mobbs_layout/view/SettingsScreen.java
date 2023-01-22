package com.friedrichvoelkers.mobbs_layout.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.friedrichvoelkers.mobbs_layout.R;
import com.friedrichvoelkers.mobbs_layout.control.FireBaseController;
import com.friedrichvoelkers.mobbs_layout.control.GameDataController;
import com.friedrichvoelkers.mobbs_layout.model.GameStatus;
import com.friedrichvoelkers.mobbs_layout.model.GlobalState;
import com.friedrichvoelkers.mobbs_layout.util.Utilities;

import java.util.ArrayList;

public class SettingsScreen extends AppCompatActivity {

    private static final int INTERVAL_GAME_DURATION = 10;
    private static final int MAX_GAME_DURATION = 60;
    private static final int MIN_MR_X_INTERVAL = 1;
    private static final int INTERVAL_MR_X_INTERVAL = 1;
    private static final int MAX_MR_X_INTERVAL = 10;
    private static final int DEFAULT_GAME_DURATION = 30;
    private static final int DEFAULT_MR_X_INTERVAL = 5;

    private TextView gameDuarationTextview;
    private TextView mrXIntervalTextView;
    private TextView mrXTextView;
    private NumberPicker numberPicker;
    private Button startGameButton;

    private SettingsOptions lastChoosenOption;
    private ArrayList<String> currentNumberPickerValues = new ArrayList<>();
    private int currentGameDuration;
    private int currentMrXInterval;
    private String currentMrX;

    @SuppressLint ("SetTextI18n")
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        gameDuarationTextview = findViewById(R.id.text_setting_view_game_duration);
        mrXIntervalTextView = findViewById(R.id.text_setting_view_mr_x_interval);
        mrXTextView = findViewById(R.id.text_setting_mr_x);
        numberPicker = findViewById(R.id.number_picker_setting_view);
        startGameButton = findViewById(R.id.button_start_setting_view);

        gameDuarationTextview.setOnClickListener(view -> numberPicker.setDisplayedValues(
                updateNumberPicker(SettingsOptions.GAME_DURATION)));
        mrXIntervalTextView.setOnClickListener(view -> numberPicker.setDisplayedValues(
                this.updateNumberPicker(SettingsOptions.MR_X_INTERVAL)));
        mrXTextView.setOnClickListener(view -> numberPicker.setDisplayedValues(
                this.updateNumberPicker(SettingsOptions.MR_X)));

        numberPicker.setOnValueChangedListener((numberPicker, i, i1) -> {
            switch (lastChoosenOption) {
                case GAME_DURATION: {
                    this.currentGameDuration = Integer.parseInt(
                            currentNumberPickerValues.get(numberPicker.getValue()));
                    this.gameDuarationTextview.setText(this.currentGameDuration + " minutes");
                    break;
                }
                case MR_X_INTERVAL: {
                    this.currentMrXInterval = Integer.parseInt(
                            currentNumberPickerValues.get(numberPicker.getValue()));
                    this.mrXIntervalTextView.setText(this.currentMrXInterval + " minutes");
                    break;
                }
                case MR_X: {
                    this.currentMrX = currentNumberPickerValues.get(numberPicker.getValue());
                    this.mrXTextView.setText(
                            currentNumberPickerValues.get(numberPicker.getValue()));
                    break;
                }
            }
        });

        startGameButton.setOnClickListener(view -> {
            GlobalState.getInstance().getGameData().setGameDuration(currentGameDuration);
            GlobalState.getInstance().getGameData().setShowMrXInterval(currentMrXInterval);
            GlobalState.getInstance().getGameData().setGameStatus(GameStatus.HIDING);
            GlobalState.getInstance().getGameData().setMrX(currentMrX);
            GlobalState.getInstance().getMyUserData().setiAmMrX(currentMrX.trim().equalsIgnoreCase(
                    GlobalState.getInstance().getMyUserData().getUsername().trim()));
            FireBaseController.settingDone();

            if (GlobalState.getInstance().getMyUserData().isiAmMrX()) {
                Intent intent = new Intent(getApplicationContext(), LoadingScreen.class);
                Bundle extras = new Bundle();
                extras.putString("header", "You are Mr. X. Please hide!");
                extras.putBoolean("show_time", true);
                extras.putInt("time", GlobalState.getInstance().getGameData().getShowMrXInterval());
                extras.putSerializable("mode", LoadingMode.WAIT_FOR_MR_X);
                intent.putExtras(extras);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getApplicationContext(), LoadingScreen.class);
                Bundle extras = new Bundle();
                extras.putString("header", GlobalState.getInstance().getGameData().getMrX() +
                        " is Mr. X. Wait until Mr. X has hidden.");
                extras.putBoolean("show_time", true);
                extras.putInt("time", GlobalState.getInstance().getGameData().getShowMrXInterval());
                extras.putSerializable("mode", LoadingMode.WAIT_FOR_MR_X);
                intent.putExtras(extras);
                startActivity(intent);
            }

        });

        this.setDefaultValues();
    }

    @SuppressLint ("SetTextI18n")
    private void setDefaultValues () {
        this.currentGameDuration = DEFAULT_GAME_DURATION;
        this.currentMrXInterval = DEFAULT_MR_X_INTERVAL;
        this.currentMrX = GlobalState.getInstance().getMyUserData().getUsername();

        this.gameDuarationTextview.setText(currentGameDuration + " minutes");
        this.mrXIntervalTextView.setText(currentMrXInterval + " minutes");
        this.mrXTextView.setText(currentMrX);
    }

    private String[] updateNumberPicker (SettingsOptions settingOptions) {
        this.lastChoosenOption = settingOptions;
        int MIN_GAME_DURATION = 10;
        switch (settingOptions) {
            case GAME_DURATION:
                return convertArrayListToStringArray(
                        Utilities.createNewArrayListFromInteger(MIN_GAME_DURATION,
                                MAX_GAME_DURATION, INTERVAL_GAME_DURATION));
            case MR_X_INTERVAL:
                return convertArrayListToStringArray(
                        Utilities.createNewArrayListFromInteger(MIN_MR_X_INTERVAL,
                                MAX_MR_X_INTERVAL, INTERVAL_MR_X_INTERVAL));
            case MR_X:
                return convertArrayListToStringArray(Utilities.createNewArrayListFromUser(
                        GameDataController.getAllUser()));
            default:
                return new String[0];
        }
    }

    private String[] convertArrayListToStringArray (ArrayList<String> arrayList) {
        String[] returnStringArray = new String[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            returnStringArray[i] = arrayList.get(i);
        }
        currentNumberPickerValues = arrayList;
        numberPicker.setDisplayedValues(null);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(arrayList.size() - 1);
        return returnStringArray;
    }
}