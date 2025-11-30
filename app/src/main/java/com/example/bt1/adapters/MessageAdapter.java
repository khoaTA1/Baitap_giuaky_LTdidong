package com.example.bt1.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bt1.R;
import com.example.bt1.models.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    
    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_ADMIN = 2;
    
    private List<Message> messages;
    private SimpleDateFormat timeFormat;

    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
        this.timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        return message.isFromUser() ? VIEW_TYPE_USER : VIEW_TYPE_ADMIN;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_USER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_user, parent, false);
            return new UserMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_admin, parent, false);
            return new AdminMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        
        if (holder instanceof UserMessageViewHolder) {
            ((UserMessageViewHolder) holder).bind(message);
        } else if (holder instanceof AdminMessageViewHolder) {
            ((AdminMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages != null ? messages.size() : 0;
    }

    public void updateMessages(List<Message> newMessages) {
        this.messages = newMessages;
        notifyDataSetChanged();
    }

    // User Message ViewHolder
    class UserMessageViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage, textTime;

        UserMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.text_message);
            textTime = itemView.findViewById(R.id.text_time);
        }

        void bind(Message message) {
            textMessage.setText(message.getMessage());
            if (message.getTimestamp() != null) {
                String time = timeFormat.format(new Date(message.getTimestamp()));
                textTime.setText(time);
            }
        }
    }

    // Admin Message ViewHolder
    class AdminMessageViewHolder extends RecyclerView.ViewHolder {
        TextView textSenderName, textMessage, textTime;

        AdminMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textSenderName = itemView.findViewById(R.id.text_sender_name);
            textMessage = itemView.findViewById(R.id.text_message);
            textTime = itemView.findViewById(R.id.text_time);
        }

        void bind(Message message) {
            textSenderName.setText(message.getSenderName() != null ? message.getSenderName() : "Admin");
            textMessage.setText(message.getMessage());
            if (message.getTimestamp() != null) {
                String time = timeFormat.format(new Date(message.getTimestamp()));
                textTime.setText(time);
            }
        }
    }
}
