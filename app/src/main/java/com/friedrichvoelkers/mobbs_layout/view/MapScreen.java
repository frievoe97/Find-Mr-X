package com.friedrichvoelkers.mobbs_layout.view;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.friedrichvoelkers.mobbs_layout.R;
import com.friedrichvoelkers.mobbs_layout.control.FireBaseController;
import com.friedrichvoelkers.mobbs_layout.control.GameDataController;
import com.friedrichvoelkers.mobbs_layout.control.GameDataException;
import com.friedrichvoelkers.mobbs_layout.model.GameStatus;
import com.friedrichvoelkers.mobbs_layout.model.GlobalState;
import com.friedrichvoelkers.mobbs_layout.model.User;
import com.friedrichvoelkers.mobbs_layout.util.Utilities;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.utils.BitmapUtils;
import com.ncorti.slidetoact.SlideToActView;

import java.util.HashMap;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

public class MapScreen extends AppCompatActivity implements Observer {

    private static final String LOCATION_MARKER = "map_marker_black";
    private static final int SECONDS_PER_MINUTE = 60;
    private static final int MILLISECONDS_PER_SECOND = 1000;

    private final HashMap<User, Symbol> allUserSymbols = new HashMap<>();
    private final boolean gameIsStillGoing = true;

    private MapView mapView;
    private SymbolManager symbolManager;
    private ImageButton chatButton;
    private TextView gameDurationTextView;
    private TextView mrXIntervalTextView;
    private ProgressBar gameDurationProgressBar;
    private ProgressBar mrXIntervalProgressBar;
    private SlideToActView foundMrXSlider;

    private BluetoothManager bluetoothManager = null;
    private BluetoothAdapter bluetoothAdapter = null;

    @SuppressLint ("MissingPermission")
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_map);

        gameDurationTextView = findViewById(R.id.progress_bar_text_up);
        mrXIntervalTextView = findViewById(R.id.progress_bar_text_down);
        gameDurationProgressBar = findViewById(R.id.progress_bar_up);
        mrXIntervalProgressBar = findViewById(R.id.progress_bar_down);

        foundMrXSlider = (SlideToActView) findViewById(R.id.slider_map_view_found_mr_x);
        foundMrXSlider.setCompleteIcon(R.drawable.circle_arrow_icon);
        foundMrXSlider.setRotateIcon(true);
        foundMrXSlider.setOnSlideCompleteListener(slideToActView -> checkIfMrXIsAround());

        chatButton = findViewById(R.id.button_map_view_chat);
        chatButton.setImageResource(R.drawable.chat_icon_white);
        chatButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), ChatScreen.class);
            startActivity(intent);
        });

        if (GameDataController.amIMrX()) {
            chatButton.setVisibility(View.GONE);
            foundMrXSlider.setVisibility(View.GONE);
        }

        CameraPosition startCameraPosition = new CameraPosition.Builder().target(
                new LatLng(GlobalState.getInstance().getMyUserData().getLatitude(),
                        GlobalState.getInstance().getMyUserData().getLongitude())).zoom(14).build();

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(mapboxMap -> {

            // Disable mapbox logo and info button
            mapboxMap.getUiSettings().setAttributionEnabled(false);
            mapboxMap.getUiSettings().setLogoEnabled(false);
            mapboxMap.setStyle(Style.LIGHT, style -> {
                mapboxMap.setCameraPosition(startCameraPosition);

                // Initialize SymbolManager
                symbolManager = new SymbolManager(mapView, mapboxMap, style);

                // Add location marker (map marker in blue and red)
                this.addLocationMarkerToStyle(style);
                this.createSymbolsForAllUser(style);
            });
        });

        // Add Listeners
        FireBaseController.loadMessages();
        FireBaseController.loadNewGameStatus();
        GlobalState.getInstance().addObserver(this);

        // Start the countdowns
        try {
            updateUserLocations(false);
        } catch (GameDataException e) {
            Toast.makeText(MapScreen.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }

        this.startGameDurationCountdown(
                ((long) GameDataController.getGameDuration() * SECONDS_PER_MINUTE *
                        MILLISECONDS_PER_SECOND) -
                        (System.currentTimeMillis() - GlobalState.getInstance().getStartTime()));
        this.startMrXCountdown(
                ((long) GameDataController.getShowMrXInterval() * SECONDS_PER_MINUTE *
                        MILLISECONDS_PER_SECOND) -
                        (System.currentTimeMillis() - GameDataController.getLastMrXStartTime()));
    }

    @SuppressLint ("MissingPermission")
    private void checkIfMrXIsAround () {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            bluetoothManager = getSystemService(BluetoothManager.class);
        }

        if (bluetoothManager != null) {
            bluetoothAdapter = bluetoothManager.getAdapter();
        }

        bluetoothAdapter.startDiscovery();

        //do something else
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @SuppressLint ("MissingPermission")
            @Override
            public void onReceive (Context context, Intent intent) {
                String action = intent.getAction();

                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice bluetoothDevice =
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    assert bluetoothDevice != null;

                    if (bluetoothDevice.getName() != null &&
                            GameDataController.didIFoundMrX(bluetoothDevice.getName())) {

                        unregisterReceiver(this);
                        GlobalState.getInstance().getGameData().setGameStatus(GameStatus.END);
                        FireBaseController.settingDone();
                        gameIsDone(false);
                    }
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    //do something else
                    Toast.makeText(MapScreen.this, "Mr. X could not be found.", Toast.LENGTH_LONG)
                            .show();
                    foundMrXSlider.resetSlider();
                }
            }
        };

        IntentFilter intFilter = new IntentFilter();
        intFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        registerReceiver(broadcastReceiver, intFilter);
    }

    private void gameIsDone (boolean mrXWon) {
        Intent intent = new Intent(getApplicationContext(), LoadingScreen.class);
        Bundle extras = new Bundle();
        extras.putString("header", "");
        extras.putBoolean("show_time", false);
        extras.putBoolean("mr_x_won", mrXWon);
        extras.putInt("time", 0);
        extras.putSerializable("mode", LoadingMode.GAME_DONE);
        intent.putExtras(extras);
        startActivity(intent);
    }

    private void startMrXCountdown (long time) {
        new CountDownTimer(time, MILLISECONDS_PER_SECOND) {
            boolean showCountdownDots = true;

            @Override
            public void onTick (long millisUntilFinished) {

                mrXIntervalTextView.setText(
                        Utilities.convertTimeInMillisecondsToCountdownString(millisUntilFinished,
                                showCountdownDots));

                showCountdownDots = !showCountdownDots;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    mrXIntervalProgressBar.setProgress(
                            Utilities.concertTimeLeftToProgressBar(millisUntilFinished,
                                    GlobalState.getInstance().getGameData().getShowMrXInterval() *
                                            SECONDS_PER_MINUTE * MILLISECONDS_PER_SECOND));
                }
            }

            @Override
            public void onFinish () {
                if (gameIsStillGoing) startMrXCountdown(
                        ((long) GlobalState.getInstance().getGameData().getShowMrXInterval() *
                                SECONDS_PER_MINUTE * MILLISECONDS_PER_SECOND) -
                                (System.currentTimeMillis() -
                                        GameDataController.getLastMrXStartTime()));
            }
        }.start();
    }

    private void startGameDurationCountdown (long time) {
        new CountDownTimer(time, MILLISECONDS_PER_SECOND) {
            boolean showCountdownDots = true;

            @Override
            public void onTick (long millisUntilFinished) {

                gameDurationTextView.setText(
                        Utilities.convertTimeInMillisecondsToCountdownString(millisUntilFinished,
                                showCountdownDots));

                try {
                    if (GameDataController.amIMrX()) updateUserLocations(true);
                    updateUserLocations(false);
                } catch (GameDataException e) {
                    Toast.makeText(MapScreen.this, e.getLocalizedMessage(), Toast.LENGTH_LONG)
                            .show();
                }

                showCountdownDots = !showCountdownDots;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    gameDurationProgressBar.setProgress(
                            Utilities.concertTimeLeftToProgressBar(millisUntilFinished,
                                    GlobalState.getInstance().getGameData().getGameDuration() *
                                            SECONDS_PER_MINUTE * MILLISECONDS_PER_SECOND));
                }

            }

            @Override
            public void onFinish () {
                gameIsDone(true);
            }
        }.start();
    }

    private void addLocationMarkerToStyle (Style style) {
        style.addImage(LOCATION_MARKER, Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                getResources().getDrawable(R.drawable.map_marker))), true);
    }

    private void createSymbolsForAllUser (Style style) {

        this.mapView.getMapAsync(mapboxMap -> {

            if (symbolManager == null) {
                symbolManager = new SymbolManager(mapView, mapboxMap, style);
            }

            for (User user : GlobalState.getInstance().getAllUserData()) {
                if (user.getUsername().trim().equalsIgnoreCase(
                        GlobalState.getInstance().getGameData().getMrX().trim())) {
                    allUserSymbols.put(user, symbolManager.create(new SymbolOptions().withLatLng(
                                    new LatLng(user.getLatitude(), user.getLongitude()))
                            .withIconImage(LOCATION_MARKER).withIconSize(1.3f)
                            .withIconColor("#FF5050").withTextField(user.getUsername())
                            .withTextOffset(new Float[]{0f, -2f})));
                } else {
                    allUserSymbols.put(user, symbolManager.create(new SymbolOptions().withLatLng(
                                    new LatLng(user.getLatitude(), user.getLongitude()))
                            .withIconImage(LOCATION_MARKER).withIconSize(1.3f)
                            .withIconColor("#00AEEF").withTextField(user.getUsername())
                            .withTextOffset(new Float[]{0f, -2f})));
                }
            }
        });
    }

    private void updateUserLocations (boolean updateMrX) throws GameDataException {
        GameDataController.updateSingleUserLocation(allUserSymbols, symbolManager, updateMrX);
    }

    @Override
    public void update (Observable observable, Object o) {
        if (o.equals("new_message") && GameDataController.getAllMessages().size() > 0) {
            chatButton.setImageResource(R.drawable.chat_icon_red);
        } else if (o.equals("new_game_status") &&
                GameDataController.getGameStatus() == GameStatus.END) {
            this.gameIsDone(false);
        }
    }
}