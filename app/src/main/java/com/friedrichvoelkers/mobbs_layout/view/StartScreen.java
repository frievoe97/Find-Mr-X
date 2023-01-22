package com.friedrichvoelkers.mobbs_layout.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.friedrichvoelkers.mobbs_layout.R;
import com.friedrichvoelkers.mobbs_layout.control.FireBaseController;
import com.friedrichvoelkers.mobbs_layout.control.GameDataController;
import com.friedrichvoelkers.mobbs_layout.control.GameDataException;

/**
 * This activity is started when the app is launched. Here you can create a new game or join an
 * existing game. As soon as a game has been created or you have joined a game, the activity is
 * switched and you get to the JoiningScreen.
 */
public class StartScreen extends AppCompatActivity {

    private static final String LOG = "Startscreen";

    private Button createGameButton;
    private Button joinGameButton;
    private EditText usernameEditText;
    private EditText gameIdEditText;
    private String bluetoothDeviceName;
    private BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startscreen);

        // Declare all UI Objects
        createGameButton = findViewById(R.id.button_startscreen_create_game);
        joinGameButton = findViewById(R.id.button_startscreen_join_game);
        usernameEditText = findViewById(R.id.edit_text_startscreen_username_id);
        gameIdEditText = findViewById(R.id.edit_text_startscreen_game_id);

        // Permissions Checks
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            this.checkAllPermissions();
        }

        // Get all existing games
        FireBaseController.getAllExistingGames();

        // Get Bluetoothdevice name
        bluetoothDeviceName = getbluetoothDeviceName();

        // Button methods
        createGameButton.setOnClickListener(view -> {
            try {
                GameDataController.usernameAndGameIdAreSetCorrect(
                        String.valueOf(usernameEditText.getText()),
                        String.valueOf(gameIdEditText.getText()));
            } catch (GameDataException e) {
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }

            if (GameDataController.gameIdExistsAlready(String.valueOf(gameIdEditText.getText()))) {
                sendToastMessage("The Game-ID already exists.");
                return;
            }

            if (bluetoothDeviceName == null) {
                sendToastMessage("Your bluetooth device name is null.");
                return;
            }

            try {
                GameDataController.createNewGame(String.valueOf(usernameEditText.getText()),
                        String.valueOf(gameIdEditText.getText()), bluetoothDeviceName, false);
            } catch (GameDataException e) {
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                return;
            }

            Intent intent = new Intent(getApplicationContext(), JoiningScreen.class);
            startActivity(intent);
        });

        joinGameButton.setOnClickListener(view -> {
            try {
                GameDataController.usernameAndGameIdAreSetCorrect(
                        String.valueOf(usernameEditText.getText()),
                        String.valueOf(gameIdEditText.getText()));
            } catch (GameDataException e) {
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }

            if (!GameDataController.gameIdExistsAlready(String.valueOf(gameIdEditText.getText()))) {
                sendToastMessage("The Game-ID does not exists.");
                return;
            }

            if (bluetoothDeviceName == null) {
                sendToastMessage("Your bluetooth device name is null.");
                return;
            }

            if (GameDataController.bluetoothDeviceNameExistsAlready(
                    String.valueOf(gameIdEditText.getText()), bluetoothDeviceName)) {
                sendToastMessage("Please change your bluetooth name. This name already exists.");
                return;
            }

            if (GameDataController.usernameExistsAlready(String.valueOf(gameIdEditText.getText()),
                    String.valueOf(usernameEditText.getText()))) {
                sendToastMessage("Please change your bluetooth name. This name already exists.");
                return;
            }

            try {
                GameDataController.joinGame(String.valueOf(usernameEditText.getText()),
                        String.valueOf(gameIdEditText.getText()), bluetoothDeviceName, false);
            } catch (GameDataException e) {
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                return;
            }

            Intent intent = new Intent(getApplicationContext(), JoiningScreen.class);
            startActivity(intent);
        });
    }

    /**
     * In this method, all permissions are requested that are necessary for the entire app. This
     * means that this no longer needs to be checked in later activities.
     */
    @RequiresApi (api = Build.VERSION_CODES.S)
    private void checkAllPermissions () {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            Log.d(LOG, "ACCESS_COARSE_LOCATION is not enabled!");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        } else {
            Log.d(LOG, "ACCESS_COARSE_LOCATION is enabled!");
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            Log.d(LOG, "ACCESS_FINE_LOCATION is not enabled!");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        } else {
            Log.d(LOG, "ACCESS_FINE_LOCATION is enabled!");
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) !=
                PackageManager.PERMISSION_GRANTED) {
            Log.d(LOG, "The Permission: BLUETOOTH is not enabled!");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH},
                    100);
        } else {
            Log.d(LOG, "The Permission: BLUETOOTH is enabled!");
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) !=
                PackageManager.PERMISSION_GRANTED) {
            Log.d(LOG, "BLUETOOTH_ADMIN is not enabled!");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH_ADMIN}, 100);
        } else {
            Log.d(LOG, "BLUETOOTH_ADMIN is enabled!");
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) !=
                PackageManager.PERMISSION_GRANTED) {
            Log.d(LOG, "BLUETOOTH_SCAN is not enabled!");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH_SCAN}, 100);
        } else {
            Log.d(LOG, "BLUETOOTH_SCAN is enabled!");
        }
    }

    private void sendToastMessage (String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @SuppressLint ("MissingPermission")
    public String getbluetoothDeviceName () {
        if (bluetoothAdapter == null) {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        Log.d(LOG, "The Bluetooth name is: " + bluetoothAdapter.getName());
        return bluetoothAdapter.getName();
    }
}