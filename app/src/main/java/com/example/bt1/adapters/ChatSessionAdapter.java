package com.example.bt1.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bt1.R;
import com.example.bt1.models.ChatSession;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatSessionAdapter extends RecyclerView.Adapter<ChatSessionAdapter.ChatSessionViewHolder> {

    private List<ChatSession> chatSessions;
    private OnChatSessionClickListener listener;
    private SimpleDateFormat timeFormat;
    private SimpleDateFormat dateFormat;

    public interface OnChatSessionClickListener {
        void onChatSessionClick(ChatSession chatSession);
    }

    public ChatSessionAdapter(List<ChatSession> chatSessions, OnChatSessionClickListener listener) {
        this.chatSessions = chatSessions;
        this.listener = listener;
        this.timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public ChatSessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_session, parent, false);
        return new ChatSessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatSessionViewHolder holder, int position) {
        ChatSession session = chatSessions.get(position);
        holder.bind(session);
    }

    @Override
    public int getItemCount() {
        return chatSessions != null ? chatSessions.size() : 0;
    }

    public void updateSessions(List<ChatSession> newSessions) {
        this.chatSessions = newSessions;
        notifyDataSetChanged();
    }

    class ChatSessionViewHolder extends RecyclerView.ViewHolder {
        TextView textUserName, textUserEmail, textLastMessage, textTime, textUnreadCount;

        ChatSessionViewHolder(@NonNull View itemView) {
            super(itemView);
            textUserName = itemView.findViewById(R.id.text_user_name);
            textUserEmail = itemView.findViewById(R.id.text_user_email);
            textLastMessage = itemView.findViewById(R.id.text_last_message);
            textTime = itemView.findViewById(R.id.text_time);
            textUnreadCount = itemView.findViewById(R.id.text_unread_count);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onChatSessionClick(chatSessions.get(position));
                }
            });
        }

        void bind(ChatSession session) {
            textUserName.setText(session.getUserName() != null ? session.getUserName() : "Khách hàng");
            textUserEmail.setText(session.getUserEmail() != null ? session.getUserEmail() : "");
            textLastMessage.setText(session.getLastMessage() != null ? session.getLastMessage() : "");

            // Format time
            if (session.getLastMessageTime() != null) {
                Date messageDate = new Date(session.getLastMessageTime());
                Date today = new Date();
                
                // Check if message is from today
                if (dateFormat.format(messageDate).equals(dateFormat.format(today))) {
                    textTime.setText(timeFormat.format(messageDate));
                } else {
                    textTime.setText(dateFormat.format(messageDate));
                }
            }

            // Show unread count badge
            if (session.getUnreadCount() > 0) {
                textUnreadCount.setVisibility(View.VISIBLE);
                textUnreadCount.setText(String.valueOf(session.getUnreadCount()));
            } else {
                textUnreadCount.setVisibility(View.GONE);
            }
        }
    }
}
