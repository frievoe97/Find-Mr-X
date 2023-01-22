package com.friedrichvoelkers.mobbs_layout.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.friedrichvoelkers.mobbs_layout.R;
import com.friedrichvoelkers.mobbs_layout.control.FireBaseController;
import com.friedrichvoelkers.mobbs_layout.model.GameStatus;
import com.friedrichvoelkers.mobbs_layout.model.GlobalState;
import com.friedrichvoelkers.mobbs_layout.model.User;

import java.util.Observable;
import java.util.Observer;

public class JoiningScreen extends AppCompatActivity implements Observer {

    private RecyclerView recyclerView;
    private JoiningScreenRecyclerViewAdapter joiningScreenRecyclerViewAdapter;
    private Button startGameButton;
    private int numberOfPlayers = 0;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joining_screen);

        recyclerView = findViewById(R.id.joining_screen_recycler_view);
        joiningScreenRecyclerViewAdapter = new JoiningScreenRecyclerViewAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(joiningScreenRecyclerViewAdapter);

        // Add Buttons
        startGameButton = findViewById(R.id.joining_screen_start_game);
        startGameButton.setOnClickListener(view -> {
            FireBaseController.joiningDone();
            Intent intent = new Intent(getApplicationContext(), SettingsScreen.class);
            startActivity(intent);
        });

        // Button should be only clickable if you are the gamemaster and you are not playing alone
        updateStartGameButton();

        // Set Listener for new players
        FireBaseController.loadOtherPlayers();

        // Set Listener for new GameStatus
        if (!GlobalState.getInstance().getMyUserData().isiAmTheGameMaster()) {
            FireBaseController.loadNewGameStatus();
        }

        // Add observer to receive new user
        GlobalState.getInstance().addObserver(this);
    }

    private void updateStartGameButton () {
        startGameButton.setEnabled(GlobalState.getInstance().getMyUserData().isiAmTheGameMaster() &&
                numberOfPlayers > 1);
    }

    /**
     * When the activity is changed, the Firebase listeners and the observers must be removed,
     * otherwise they would continue to run.
     */
    @Override
    protected void onPause () {
        super.onPause();
        GlobalState.getInstance().deleteObserver(this);
        FireBaseController.databaseReference.removeEventListener(
                FireBaseController.loadOtherPlayersListener);
        if (!GlobalState.getInstance().getMyUserData().isiAmTheGameMaster()) {
            FireBaseController.databaseReference.removeEventListener(
                    FireBaseController.gameStatusChangedListener);
        }
    }

    /**
     * The Observer observes two types of data. One is that a new player has joined the game and
     * that the GameStatus has changed, i.e. the GameMaster has started the game.
     */
    @Override
    public void update (Observable observable, Object o) {
        if (o.equals("new_user")) {
            joiningScreenRecyclerViewAdapter.clear();
            numberOfPlayers = GlobalState.getInstance().getAllUserData().size();
            for (User user : GlobalState.getInstance().getAllUserData()) {
                joiningScreenRecyclerViewAdapter.add(user.getUsername());
                Log.d("user", user.toString());
            }
            updateStartGameButton();
        } else if (o.equals("new_game_status")) {
            if (!GlobalState.getInstance().getMyUserData().isiAmTheGameMaster() &&
                    GlobalState.getInstance().getGameData().getGameStatus() ==
                            GameStatus.SETTINGS) {
                Intent intent = new Intent(getApplicationContext(), LoadingScreen.class);
                Bundle extras = new Bundle();
                extras.putString("header", "Wait for Settings");
                extras.putBoolean("show_time", false);
                extras.putBoolean("mr_x_won", false);
                extras.putInt("time", 0);
                extras.putSerializable("mode", LoadingMode.SETTINGS);
                intent.putExtras(extras);
                startActivity(intent);
            }
        }
    }
}