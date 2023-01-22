package com.friedrichvoelkers.mobbs_layout.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.friedrichvoelkers.mobbs_layout.R;

import java.util.ArrayList;

public class JoiningScreenRecyclerViewAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final ArrayList<String> userList;

    public JoiningScreenRecyclerViewAdapter (Context mContext) {
        this.mContext = mContext;
        userList = new ArrayList<>();
    }

    public void add (String name) {
        userList.add(name);
        notifyDataSetChanged();
    }

    public void clear () {
        userList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.joining_screen_recycler_view_item, parent, false);
        return new UsernameHolder(view);
    }

    @Override
    public void onBindViewHolder (@NonNull RecyclerView.ViewHolder holder, int position) {
        String userData = userList.get(position);
        ((UsernameHolder) holder).bind(userData);
    }

    @Override
    public int getItemCount () {
        return userList.size();
    }

    private static class UsernameHolder extends RecyclerView.ViewHolder {
        TextView textView;

        UsernameHolder (View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.joining_screen_recycler_view_item_text);
        }

        void bind (String name) {
            textView.setText(name);
        }
    }
}
