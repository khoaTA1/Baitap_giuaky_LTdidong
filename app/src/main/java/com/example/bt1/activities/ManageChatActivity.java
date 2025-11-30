package com.example.bt1.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bt1.R;
import com.example.bt1.adapters.ChatSessionAdapter;
import com.example.bt1.models.ChatSession;
import com.example.bt1.repositories.ChatRepository;

import java.util.ArrayList;
import java.util.List;

public class ManageChatActivity extends AppCompatActivity implements ChatSessionAdapter.OnChatSessionClickListener {

    private RecyclerView recyclerChatSessions;
    private LinearLayout emptyState;
    private ImageView btnBack;

    private ChatSessionAdapter sessionAdapter;
    private List<ChatSession> chatSessions;
    private ChatRepository chatRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_chat);

        // Initialize views
        recyclerChatSessions = findViewById(R.id.recycler_chat_sessions);
        emptyState = findViewById(R.id.empty_state);
        btnBack = findViewById(R.id.btn_back);

        // Initialize repository
        chatRepository = new ChatRepository();

        // Setup RecyclerView
        setupRecyclerView();

        // Load chat sessions
        loadChatSessions();

        // Setup listeners
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        chatSessions = new ArrayList<>();
        sessionAdapter = new ChatSessionAdapter(chatSessions, this);

        recyclerChatSessions.setLayoutManager(new LinearLayoutManager(this));
        recyclerChatSessions.setAdapter(sessionAdapter);
    }

    private void loadChatSessions() {
        chatRepository.loadChatSessions(sessions -> {
            if (sessions != null && !sessions.isEmpty()) {
                chatSessions.clear();
                chatSessions.addAll(sessions);
                sessionAdapter.updateSessions(chatSessions);
                
                recyclerChatSessions.setVisibility(View.VISIBLE);
                emptyState.setVisibility(View.GONE);
            } else {
                recyclerChatSessions.setVisibility(View.GONE);
                emptyState.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onChatSessionClick(ChatSession chatSession) {
        // Mark as read
        chatRepository.markAsRead(chatSession.getSessionId());
        
        // Open chat activity in admin mode
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("session_id", chatSession.getSessionId());
        intent.putExtra("user_name", chatSession.getUserName());
        intent.putExtra("user_email", chatSession.getUserEmail());
        intent.putExtra("user_id", chatSession.getUserId());
        startActivity(intent);
    }
}
