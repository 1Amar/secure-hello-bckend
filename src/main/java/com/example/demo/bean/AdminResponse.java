package com.example.demo.bean;

public class AdminResponse {
    private String message;
    private long timestamp;

    public AdminResponse(String message, long timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
