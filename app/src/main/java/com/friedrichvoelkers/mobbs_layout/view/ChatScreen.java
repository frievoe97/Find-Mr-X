package com.friedrichvoelkers.mobbs_layout.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.friedrichvoelkers.mobbs_layout.R;
import com.friedrichvoelkers.mobbs_layout.control.FireBaseController;
import com.friedrichvoelkers.mobbs_layout.model.GlobalState;
import com.friedrichvoelkers.mobbs_layout.model.Message;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class ChatScreen extends AppCompatActivity implements Observer {

    private final ArrayList<Message> allMessages = GlobalState.getInstance().getAllMessages();

    private RecyclerView recyclerView;
    private ChatScreenRecyclerViewAdapter chatActivityRecyclerViewAdapter;
    private Button sendMessageButton;
    private ImageButton goBackButton;
    private EditText editText;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sendMessageButton = findViewById(R.id.button_chat_view_send);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_gchat);
        chatActivityRecyclerViewAdapter = new ChatScreenRecyclerViewAdapter(this, allMessages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatActivityRecyclerViewAdapter);

        if (chatActivityRecyclerViewAdapter.getItemCount() > 0) {
            this.recyclerView.smoothScrollToPosition(
                    chatActivityRecyclerViewAdapter.getItemCount() - 1);
        }

        editText = findViewById(R.id.edittext_chat_view_message);
        goBackButton = findViewById(R.id.button_chat_view_send_back);

        goBackButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MapScreen.class);
            startActivity(intent);
        });

        sendMessageButton.setOnClickListener(view -> {
            String message = editText.getText().toString().trim();
            if (!message.equals("")) {
                FireBaseController.sendMessage(
                        new Message(GlobalState.getInstance().getMyUserData().getUsername(),
                                (int) (System.currentTimeMillis() / 1000), message));
                editText.setText("");
            }
        });

        GlobalState.getInstance().addObserver(this);
    }

    @Override
    public void update (Observable observable, Object o) {
        if (o.equals("new_message")) {
            Log.d("Chat", "New Message :)");
            Log.d("Message", GlobalState.getInstance().getAllMessages()
                    .get(GlobalState.getInstance().getAllMessages().size() - 1).toString());
            //this.allMessages = GlobalState.getInstance().getAllMessages();
            //this.chatActivityRecyclerViewAdapter.add(GlobalState.getInstance().getAllMessages()
            // .get(GlobalState.getInstance().getAllMessages().size() - 1));
            //this.allMessages = GlobalState.getInstance().getAllMessages();
            this.chatActivityRecyclerViewAdapter.update();
            Log.d("All Messages", GlobalState.getInstance().getAllMessages().toString());
            this.recyclerView.smoothScrollToPosition(
                    chatActivityRecyclerViewAdapter.getItemCount() - 1);
        }
    }
}