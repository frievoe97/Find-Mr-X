package com.friedrichvoelkers.mobbs_layout.control;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.friedrichvoelkers.mobbs_layout.model.GameData;
import com.friedrichvoelkers.mobbs_layout.model.GameStatus;
import com.friedrichvoelkers.mobbs_layout.model.GlobalState;
import com.friedrichvoelkers.mobbs_layout.model.Message;
import com.friedrichvoelkers.mobbs_layout.model.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class FireBaseController {

    public static DatabaseReference databaseReference;
    public static ChildEventListener loadOtherPlayersListener;
    public static ChildEventListener loadNewMessagesListener;
    public static ValueEventListener gameStatusChangedListener;
    private static boolean loadingMessages = false;

    // Startscreen
    public static void createNewGame () {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("games")
                .child(String.valueOf(GlobalState.getInstance().getGameData().getGameId()))
                .child("game").setValue(GlobalState.getInstance().getGameData());
        databaseReference.child("games")
                .child(String.valueOf(GlobalState.getInstance().getGameData().getGameId()))
                .child("user")
                .child(String.valueOf(GlobalState.getInstance().getMyUserData().getUserID()))
                .setValue(GlobalState.getInstance().getMyUserData());
    }

    public static void joinGame (String gameId) {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        //databaseReference.child("games").child(gameId).child("user").push().setValue
        // (GlobalState.getInstance().getMyUserData());
        databaseReference.child("games").child(gameId).child("user")
                .child(String.valueOf(GlobalState.getInstance().getMyUserData().getUserID()))
                .setValue(GlobalState.getInstance().getMyUserData());
        databaseReference.child("games").child(gameId).child("game")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange (@NonNull DataSnapshot snapshot) {
                        GameData gameData = snapshot.getValue(GameData.class);
                        GlobalState.getInstance().setGameData(gameData);
                    }

                    @Override
                    public void onCancelled (@NonNull DatabaseError error) {

                    }
                });
    }

    public static void getAllExistingGames () {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("games").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange (@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    String gameId = child.getKey();
                    GlobalState.getInstance().addExistingGameId(gameId);
                    for (DataSnapshot child2 : child.getChildren()) {
                        if (Objects.equals(child2.getKey(), "user")) {
                            for (DataSnapshot child3 : child2.getChildren()) {
                                User user = child3.getValue(User.class);
                                try {
                                    GlobalState.getInstance()
                                            .addExistingUsernameAndBluetoothName(gameId,
                                                    user.getUsername(), user.getBluetoothDeviceName());
                                } catch (GameDataException e) {
                                    Log.d("Error: ", e.getLocalizedMessage());
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled (@NonNull DatabaseError error) {
            }
        });
    }

    public static void loadOtherPlayers () {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        loadOtherPlayersListener = databaseReference.child("games")
                .child(String.valueOf(GlobalState.getInstance().getGameData().getGameId()))
                .child("user").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded (@NonNull DataSnapshot snapshot,
                                              @Nullable String previousChildName) {
                        User user = snapshot.getValue(User.class);
                        GlobalState.getInstance().addUser(user);
                    }

                    @Override
                    public void onChildChanged (@NonNull DataSnapshot snapshot,
                                                @Nullable String previousChildName) {
                        try {
                            GameDataController.updateUserLocation(snapshot.getValue(User.class));
                        } catch (GameDataException ignored) {

                        }
                    }

                    @Override
                    public void onChildRemoved (@NonNull DataSnapshot snapshot) {
                    }

                    @Override
                    public void onChildMoved (@NonNull DataSnapshot snapshot,
                                              @Nullable String previousChildName) {
                    }

                    @Override
                    public void onCancelled (@NonNull DatabaseError error) {
                    }
                });
    }

    public static void settingDone () {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("games")
                .child(String.valueOf(GlobalState.getInstance().getGameData().getGameId()))
                .child("game").setValue(GlobalState.getInstance().getGameData());
    }

    public static void joiningDone () {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("games")
                .child(String.valueOf(GlobalState.getInstance().getGameData().getGameId()))
                .child("game").child("gameStatus").setValue(GameStatus.SETTINGS);
    }

    public static void loadNewGameStatus () {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        gameStatusChangedListener = databaseReference.child("games")
                .child(String.valueOf(GlobalState.getInstance().getGameData().getGameId()))
                .child("game").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange (@NonNull DataSnapshot snapshot) {
                        GameData gameData = snapshot.getValue(GameData.class);
                        GlobalState.getInstance().setGameData(gameData);
                    }

                    @Override
                    public void onCancelled (@NonNull DatabaseError error) {

                    }
                });
    }

    public static void sendMessage (Message message) {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("games")
                .child(String.valueOf(GlobalState.getInstance().getGameData().getGameId()))
                .child("messages").push().setValue(message);
    }

    public static void loadMessages () {
        if (!loadingMessages) {
            databaseReference = FirebaseDatabase.getInstance().getReference();
            loadNewMessagesListener = databaseReference.child("games")
                    .child(String.valueOf(GlobalState.getInstance().getGameData().getGameId()))
                    .child("messages").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded (@NonNull DataSnapshot snapshot,
                                                  @Nullable String previousChildName) {
                            Message message = snapshot.getValue(Message.class);
                            GlobalState.getInstance().addMessage(message);
                        }

                        @Override
                        public void onChildChanged (@NonNull DataSnapshot snapshot,
                                                    @Nullable String previousChildName) {
                        }

                        @Override
                        public void onChildRemoved (@NonNull DataSnapshot snapshot) {
                        }

                        @Override
                        public void onChildMoved (@NonNull DataSnapshot snapshot,
                                                  @Nullable String previousChildName) {
                        }

                        @Override
                        public void onCancelled (@NonNull DatabaseError error) {
                        }
                    });
            loadingMessages = true;
        }
    }

    public static void updateMyLocation (double latitude, double longitude) {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("games")
                .child(String.valueOf(GlobalState.getInstance().getGameData().getGameId()))
                .child("user")
                .child(String.valueOf(GlobalState.getInstance().getMyUserData().getUserID()))
                .child("latitude").setValue(latitude);
        databaseReference.child("games")
                .child(String.valueOf(GlobalState.getInstance().getGameData().getGameId()))
                .child("user")
                .child(String.valueOf(GlobalState.getInstance().getMyUserData().getUserID()))
                .child("longitude").setValue(longitude);
    }
}
