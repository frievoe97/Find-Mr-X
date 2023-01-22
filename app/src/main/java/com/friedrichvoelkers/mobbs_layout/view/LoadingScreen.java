package com.friedrichvoelkers.mobbs_layout.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.friedrichvoelkers.mobbs_layout.R;
import com.friedrichvoelkers.mobbs_layout.control.FireBaseController;
import com.friedrichvoelkers.mobbs_layout.control.GameDataController;
import com.friedrichvoelkers.mobbs_layout.control.GameDataException;
import com.friedrichvoelkers.mobbs_layout.control.LocationListener;
import com.friedrichvoelkers.mobbs_layout.model.GameStatus;
import com.friedrichvoelkers.mobbs_layout.model.GlobalState;
import com.friedrichvoelkers.mobbs_layout.util.Utilities;

import java.util.Observable;
import java.util.Observer;

public class LoadingScreen extends AppCompatActivity implements Observer {

    private static final int SECONDS_PER_MINUTE = 60;
    private static final int MILLISECONDS_PER_SECOND = 1000;

    private final LocationListener locationListener = new LocationListener(this);

    private String header;
    private boolean mrXWon;
    private int time;

    private TextView headerTextView;
    private TextView timeTextView;
    private LoadingMode loadingMode;
    private ProgressBar progressBar;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loadingscreen);

        Bundle extras = getIntent().getExtras();
        this.header = extras.getString("header");
        this.mrXWon = extras.getBoolean("mr_x_won");
        this.time = extras.getInt("time");
        this.loadingMode = (LoadingMode) extras.getSerializable("mode");

        this.headerTextView = findViewById(R.id.text_loading_view_header);
        this.timeTextView = findViewById(R.id.text_Loading_view_progress_bar);
        this.progressBar = findViewById(R.id.progress_bar_loading_view);

        this.setLoadingscreenForCurrentMode(this.loadingMode);
    }

    @SuppressLint ("SetTextI18n")
    private void setLoadingscreenForCurrentMode (LoadingMode loadingMode) {
        switch (loadingMode) {
            case SETTINGS: {
                this.headerTextView.setText(this.header);
                this.timeTextView.setText("");
                this.timeTextView.setVisibility(View.GONE);
                FireBaseController.loadNewGameStatus();
                GlobalState.getInstance().addObserver(this);
                break;
            }
            case WAIT_FOR_MR_X: {
                this.headerTextView.setText(this.header);
                this.timeTextView.setVisibility(View.VISIBLE);
                this.startCountdown(time);
                locationListener.startLocationUpdates();
                GlobalState.getInstance().addObserver(this);
                break;

            }
            case GAME_DONE: {
                this.progressBar.setVisibility(View.GONE);
                this.timeTextView.setVisibility(View.GONE);
                if (GameDataController.amIMrX()) {
                    if (this.mrXWon) this.headerTextView.setText("Congratulations! You have won!");
                    else this.headerTextView.setText("Unfortunately, you didn't make it.");
                } else {
                    if (this.mrXWon)
                        this.headerTextView.setText("Unfortunately, you didn't make it.");
                    else this.headerTextView.setText("Congratulations! You have won!");
                }
                break;
            }
        }
    }

    private void startCountdown (int time) {
        new CountDownTimer((long) time * SECONDS_PER_MINUTE * MILLISECONDS_PER_SECOND, MILLISECONDS_PER_SECOND) {
            boolean showCountdownDots = true;

            @Override
            public void onTick (long millisUntilFinished) {
                timeTextView.setText(
                        Utilities.convertTimeInMillisecondsToCountdownString(millisUntilFinished,
                                showCountdownDots));

                showCountdownDots = !showCountdownDots;

                if (millisUntilFinished < MILLISECONDS_PER_SECOND) {
                    GlobalState.getInstance().setStartTime(System.currentTimeMillis());
                    try {
                        GameDataController.generateMrXStartTimes();
                    } catch (GameDataException e) {
                        Toast.makeText(LoadingScreen.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                    startGame();
                }
            }

            @Override
            public void onFinish () {}
        }.start();
    }

    private void startGame () {
        Intent intent = new Intent(getApplicationContext(), MapScreen.class);
        startActivity(intent);
    }

    @Override
    protected void onPause () {
        super.onPause();

        if (this.loadingMode != LoadingMode.GAME_DONE) {
            GlobalState.getInstance().deleteObserver(this);
        }

        if (this.loadingMode == LoadingMode.SETTINGS) {
            FireBaseController.databaseReference.removeEventListener(
                    FireBaseController.gameStatusChangedListener);
        }
    }

    @Override
    public void update (Observable observable, Object o) {

        if (this.loadingMode == LoadingMode.SETTINGS && o.equals("new_game_status") &&
                GlobalState.getInstance().getGameData().getGameStatus() == GameStatus.HIDING) {

            String mrX = GlobalState.getInstance().getGameData().getMrX();

            GlobalState.getInstance().getMyUserData().setiAmMrX(mrX.trim().equalsIgnoreCase(
                    GlobalState.getInstance().getMyUserData().getUsername().trim()));

            if (GlobalState.getInstance().getMyUserData().isiAmMrX()) {
                Intent intent = new Intent(getApplicationContext(), LoadingScreen.class);
                Bundle extras = new Bundle();
                extras.putString("header", "You are Mr. X. Please hide!");
                extras.putBoolean("show_time", true);
                extras.putBoolean("mr_x_won", false);
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
                extras.putBoolean("mr_x_won", false);
                extras.putInt("time", GlobalState.getInstance().getGameData().getShowMrXInterval());
                extras.putSerializable("mode", LoadingMode.WAIT_FOR_MR_X);
                intent.putExtras(extras);
                startActivity(intent);
            }
        }
    }
}