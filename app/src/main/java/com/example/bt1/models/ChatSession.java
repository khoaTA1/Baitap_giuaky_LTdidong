package com.example.bt1.models;

import com.google.firebase.firestore.PropertyName;

public class ChatSession {
    @PropertyName("session_id")
    private String sessionId;
    
    @PropertyName("user_id")
    private String userId;
    
    @PropertyName("user_name")
    private String userName;
    
    @PropertyName("user_email")
    private String userEmail;
    
    @PropertyName("last_message")
    private String lastMessage;
    
    @PropertyName("last_message_time")
    private Long lastMessageTime;
    
    @PropertyName("unread_count")
    private int unreadCount;
    
    @PropertyName("last_sender_is_user")
    private boolean lastSenderIsUser;

    public ChatSession() {
        // Required empty constructor for Firestore
    }

    public ChatSession(String sessionId, String userId, String userName, String userEmail, 
                       String lastMessage, Long lastMessageTime, int unreadCount, boolean lastSenderIsUser) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
        this.unreadCount = unreadCount;
        this.lastSenderIsUser = lastSenderIsUser;
    }

    // Getters and Setters
    @PropertyName("session_id")
    public String getSessionId() {
        return sessionId;
    }

    @PropertyName("session_id")
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @PropertyName("user_id")
    public String getUserId() {
        return userId;
    }

    @PropertyName("user_id")
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @PropertyName("user_name")
    public String getUserName() {
        return userName;
    }

    @PropertyName("user_name")
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @PropertyName("user_email")
    public String getUserEmail() {
        return userEmail;
    }

    @PropertyName("user_email")
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    @PropertyName("last_message")
    public String getLastMessage() {
        return lastMessage;
    }

    @PropertyName("last_message")
    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    @PropertyName("last_message_time")
    public Long getLastMessageTime() {
        return lastMessageTime;
    }

    @PropertyName("last_message_time")
    public void setLastMessageTime(Long lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    @PropertyName("unread_count")
    public int getUnreadCount() {
        return unreadCount;
    }

    @PropertyName("unread_count")
    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    @PropertyName("last_sender_is_user")
    public boolean isLastSenderIsUser() {
        return lastSenderIsUser;
    }

    @PropertyName("last_sender_is_user")
    public void setLastSenderIsUser(boolean lastSenderIsUser) {
        this.lastSenderIsUser = lastSenderIsUser;
    }
}
