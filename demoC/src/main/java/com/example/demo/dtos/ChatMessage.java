package com.example.demo.dtos;

import java.time.LocalDateTime;

public class ChatMessage {
    private String sender; // Username for display
    private String senderId; // Auth User ID as string
    private String content;
    private MessageType type;
    private String to; // Receiver userId (Auth ID) as string
    private String timestamp;

    public ChatMessage() {
        this.timestamp = java.time.LocalDateTime.now().toString();
    }

    public ChatMessage(String sender, String content, MessageType type) {
        this.sender = sender;
        this.content = content;
        this.type = type;
        this.timestamp = java.time.LocalDateTime.now().toString();
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE,
        TYPING,
        ALERT
    }
}
