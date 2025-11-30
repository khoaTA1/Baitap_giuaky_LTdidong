package com.example.bt1.models;

import com.google.firebase.firestore.PropertyName;

public class Message {
    private String id;
    
    @PropertyName("sender_id")
    private String senderId;
    
    @PropertyName("sender_name")
    private String senderName;
    
    @PropertyName("sender_email")
    private String senderEmail;
    
    private String message;
    
    private Long timestamp;
    
    @PropertyName("is_from_user")
    private boolean isFromUser;
    
    @PropertyName("session_id")
    private String sessionId;

    public Message() {
        // Required empty constructor for Firestore
    }

    public Message(String senderId, String senderName, String senderEmail, String message, 
                   Long timestamp, boolean isFromUser, String sessionId) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderEmail = senderEmail;
        this.message = message;
        this.timestamp = timestamp;
        this.isFromUser = isFromUser;
        this.sessionId = sessionId;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @PropertyName("sender_id")
    public String getSenderId() {
        return senderId;
    }

    @PropertyName("sender_id")
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    @PropertyName("sender_name")
    public String getSenderName() {
        return senderName;
    }

    @PropertyName("sender_name")
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    @PropertyName("sender_email")
    public String getSenderEmail() {
        return senderEmail;
    }

    @PropertyName("sender_email")
    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @PropertyName("is_from_user")
    public boolean getIsFromUser() {
        return isFromUser;
    }

    @PropertyName("is_from_user")
    public void setIsFromUser(boolean fromUser) {
        isFromUser = fromUser;
    }
    
    // Convenience method for code readability
    public boolean isFromUser() {
        return isFromUser;
    }
    
    // IMPORTANT: Add this setter for Firestore deserialization
    // Firestore may call this when field is "fromUser" in the document
    public void setFromUser(boolean fromUser) {
        this.isFromUser = fromUser;
    }

    @PropertyName("session_id")
    public String getSessionId() {
        return sessionId;
    }

    @PropertyName("session_id")
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
