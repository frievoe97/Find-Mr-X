package com.friedrichvoelkers.mobbs_layout.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.friedrichvoelkers.mobbs_layout.R;
import com.friedrichvoelkers.mobbs_layout.model.GlobalState;
import com.friedrichvoelkers.mobbs_layout.model.Message;

import java.util.ArrayList;

public class ChatScreenRecyclerViewAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private final Context mContext;
    private final ArrayList<Message> mMessageList;

    public ChatScreenRecyclerViewAdapter (Context mContext, ArrayList<Message> messages) {
        this.mContext = mContext;
        this.mMessageList = messages;
    }

    @SuppressLint ("NotifyDataSetChanged")
    public void add (Message message) {
        this.mMessageList.add(message);
        notifyDataSetChanged();
    }

    @SuppressLint ("NotifyDataSetChanged")
    public void update () {
        notifyDataSetChanged();
    }

    @SuppressLint ("NotifyDataSetChanged")
    public void clear () {
        this.mMessageList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_send, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_recieved, parent, false);
            return new ReceivedMessageHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder (@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = this.mMessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount () {
        return this.mMessageList.size();
    }

    @Override
    public int getItemViewType (int position) {
        Message message = this.mMessageList.get(position);
        if (GlobalState.getInstance().getMyUserData().getUsername().trim()
                .equalsIgnoreCase(message.getUsername().trim())) {
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    private static class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;

        ReceivedMessageHolder (View itemView) {
            super(itemView);
            this.messageText = itemView.findViewById(R.id.text_gchat_message_other);
            this.timeText = itemView.findViewById(R.id.text_gchat_date_other);
            this.nameText = itemView.findViewById(R.id.text_gchat_user_other);
        }

        void bind (Message message) {
            this.messageText.setText(message.getMessage());
            this.nameText.setText(message.getUsername());
            this.timeText.setText(String.valueOf(message.getTimestamp()));
        }
    }

    private static class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        SentMessageHolder (View itemView) {
            super(itemView);
            this.messageText = itemView.findViewById(R.id.text_gchat_message_me);
            this.timeText = itemView.findViewById(R.id.text_gchat_date_me);
        }

        void bind (Message message) {
            this.messageText.setText(message.getMessage());
            this.timeText.setText(String.valueOf(message.getTimestamp()));
        }
    }
}
