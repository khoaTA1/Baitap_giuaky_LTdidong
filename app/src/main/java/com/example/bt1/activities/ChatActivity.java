package com.example.bt1.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bt1.R;
import com.example.bt1.adapters.MessageAdapter;
import com.example.bt1.models.Message;
import com.example.bt1.repositories.ChatRepository;
import com.example.bt1.utils.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerMessages;
    private EditText editMessage;
    private ImageView btnSend, btnBack;
    
    private MessageAdapter messageAdapter;
    private List<Message> messages;
    private ChatRepository chatRepository;
    
    private String userId;
    private String userName;
    private String userEmail;
    private String sessionId;
    private boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialize views
        recyclerMessages = findViewById(R.id.recycler_messages);
        editMessage = findViewById(R.id.edit_message);
        btnSend = findViewById(R.id.btn_send);
        btnBack = findViewById(R.id.btn_back);

        // Check if opened as admin
        sessionId = getIntent().getStringExtra("session_id");
        
        if (sessionId != null) {
            // Admin mode - replying to user
            isAdmin = true;
            userId = "admin";
            userName = "Admin";
            userEmail = "admin@healthymulti.com";
        } else {
            // User mode - initiating chat
            SharedPreferencesManager prefManager = SharedPreferencesManager.getInstance(this);
            userId = prefManager.getUserId();
            userName = prefManager.getUserName();
            userEmail = prefManager.getUserEmail();
            
            // Generate session ID (user_id)
            sessionId = userId != null ? userId : "guest_" + System.currentTimeMillis();
        }
        
        Log.d(">>> ChatActivity", "Session ID: " + sessionId + ", isAdmin: " + isAdmin + ", userId: " + userId);

        // Initialize repository
        chatRepository = new ChatRepository();

        // Setup RecyclerView
        setupRecyclerView();

        // Load messages
        loadMessages();
        
        // Mark as read when user opens chat (only for non-admin users)
        if (!isAdmin) {
            chatRepository.markAsRead(sessionId);
        }

        // Setup listeners
        btnSend.setOnClickListener(v -> sendMessage());
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        messages = new ArrayList<>();
        messageAdapter = new MessageAdapter(messages);
        
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // Start from bottom
        
        recyclerMessages.setLayoutManager(layoutManager);
        recyclerMessages.setAdapter(messageAdapter);
    }

    private void loadMessages() {
        chatRepository.loadMessages(sessionId, messagesList -> {
            if (messagesList != null) {
                messages.clear();
                messages.addAll(messagesList);
                messageAdapter.notifyDataSetChanged();
                
                // Scroll to bottom
                if (!messages.isEmpty()) {
                    recyclerMessages.post(() -> 
                        recyclerMessages.smoothScrollToPosition(messages.size() - 1)
                    );
                }
            }
        });
    }

    private void sendMessage() {
        String messageText = editMessage.getText().toString().trim();
        
        if (TextUtils.isEmpty(messageText)) {
            Toast.makeText(this, "Vui lòng nhập tin nhắn", Toast.LENGTH_SHORT).show();
            return;
        }

        // Clear input immediately for better UX
        editMessage.setText("");
        
        Log.d(">>> ChatActivity", "Sending message: " + messageText + " to session: " + sessionId);

        // Create message object
        Message message = new Message(
                userId,
                userName != null ? userName : (isAdmin ? "Admin" : "Khách hàng"),
                userEmail != null ? userEmail : "",
                messageText,
                System.currentTimeMillis(),
                !isAdmin, // isFromUser (true if not admin)
                sessionId
        );

        // Send message
        chatRepository.sendMessage(message, (success, msg) -> {
            if (!success) {
                Toast.makeText(ChatActivity.this, msg, Toast.LENGTH_SHORT).show();
                // Restore message text if failed
                editMessage.setText(messageText);
            }
            // Success case: message will appear via real-time listener
        });
    }
}
