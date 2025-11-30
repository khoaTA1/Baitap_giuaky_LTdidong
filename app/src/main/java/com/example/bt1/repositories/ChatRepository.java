package com.example.bt1.repositories;

import android.util.Log;

import com.example.bt1.models.ChatSession;
import com.example.bt1.models.Message;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatRepository {
    private static final String COLLECTION_MESSAGES = "messages";
    private static final String COLLECTION_CHAT_SESSIONS = "chat_sessions";
    private final FirebaseFirestore db;

    public ChatRepository() {
        db = FirebaseFirestore.getInstance();
    }

    // Send message
    public void sendMessage(Message message, OnCompleteListener listener) {
        String sessionId = message.getSessionId();
        
        // Add message to messages collection
        db.collection(COLLECTION_MESSAGES)
                .add(message)
                .addOnSuccessListener(documentReference -> {
                    message.setId(documentReference.getId());
                    
                    // Update or create chat session
                    updateChatSession(message, listener);
                    
                    Log.d(">>> ChatRepository", "Message sent: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e(">>> ChatRepository", "Error sending message", e);
                    if (listener != null) {
                        listener.onComplete(false, "Không thể gửi tin nhắn");
                    }
                });
    }

    // Update chat session
    private void updateChatSession(Message message, OnCompleteListener listener) {
        String sessionId = message.getSessionId();
        
        db.collection(COLLECTION_CHAT_SESSIONS)
                .document(sessionId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    ChatSession session;
                    
                    if (documentSnapshot.exists()) {
                        // Update existing session
                        session = documentSnapshot.toObject(ChatSession.class);
                        if (session != null) {
                            session.setLastMessage(message.getMessage());
                            session.setLastMessageTime(message.getTimestamp());
                            session.setLastSenderIsUser(message.isFromUser());
                            
                            // Increment unread count if message is from user
                            if (message.isFromUser()) {
                                session.setUnreadCount(session.getUnreadCount() + 1);
                            }
                        }
                    } else {
                        // Create new session
                        session = new ChatSession(
                                sessionId,
                                message.getSenderId(),
                                message.getSenderName(),
                                message.getSenderEmail(),
                                message.getMessage(),
                                message.getTimestamp(),
                                message.isFromUser() ? 1 : 0,
                                message.isFromUser()
                        );
                    }
                    
                    // Save session
                    db.collection(COLLECTION_CHAT_SESSIONS)
                            .document(sessionId)
                            .set(session)
                            .addOnSuccessListener(aVoid -> {
                                if (listener != null) {
                                    listener.onComplete(true, "Tin nhắn đã được gửi");
                                }
                            })
                            .addOnFailureListener(e -> {
                                if (listener != null) {
                                    listener.onComplete(false, "Lỗi cập nhật session");
                                }
                            });
                });
    }

    // Load messages for a session
    public void loadMessages(String sessionId, OnMessagesLoadedListener listener) {
        Log.d(">>> ChatRepository", "Loading messages for session: " + sessionId);
        
        db.collection(COLLECTION_MESSAGES)
                .whereEqualTo("session_id", sessionId)
                // Temporarily removed orderBy to avoid index requirement
                // .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.e(">>> ChatRepository", "Error loading messages", error);
                        if (listener != null) {
                            listener.onMessagesLoaded(null);
                        }
                        return;
                    }
                    
                    if (snapshots != null) {
                        List<Message> messages = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : snapshots) {
                            Message message = doc.toObject(Message.class);
                            message.setId(doc.getId());
                            messages.add(message);
                        }
                        
                        // Sort by timestamp on client side
                        messages.sort((m1, m2) -> {
                            if (m1.getTimestamp() == null) return -1;
                            if (m2.getTimestamp() == null) return 1;
                            return m1.getTimestamp().compareTo(m2.getTimestamp());
                        });
                        
                        Log.d(">>> ChatRepository", "Loaded " + messages.size() + " messages");
                        
                        if (listener != null) {
                            listener.onMessagesLoaded(messages);
                        }
                    }
                });
    }

    // Load all chat sessions for admin
    public void loadChatSessions(OnChatSessionsLoadedListener listener) {
        db.collection(COLLECTION_CHAT_SESSIONS)
                .orderBy("last_message_time", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.e(">>> ChatRepository", "Error loading chat sessions", error);
                        if (listener != null) {
                            listener.onChatSessionsLoaded(null);
                        }
                        return;
                    }
                    
                    if (snapshots != null) {
                        List<ChatSession> sessions = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : snapshots) {
                            ChatSession session = doc.toObject(ChatSession.class);
                            sessions.add(session);
                        }
                        
                        if (listener != null) {
                            listener.onChatSessionsLoaded(sessions);
                        }
                    }
                });
    }

    // Mark messages as read
    public void markAsRead(String sessionId) {
        db.collection(COLLECTION_CHAT_SESSIONS)
                .document(sessionId)
                .update("unread_count", 0)
                .addOnSuccessListener(aVoid -> {
                    Log.d(">>> ChatRepository", "Marked as read: " + sessionId);
                })
                .addOnFailureListener(e -> {
                    Log.e(">>> ChatRepository", "Error marking as read", e);
                });
    }

    // Interfaces
    public interface OnCompleteListener {
        void onComplete(boolean success, String message);
    }

    public interface OnMessagesLoadedListener {
        void onMessagesLoaded(List<Message> messages);
    }

    public interface OnChatSessionsLoadedListener {
        void onChatSessionsLoaded(List<ChatSession> sessions);
    }
}
