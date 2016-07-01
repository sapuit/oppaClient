package vn.soaap.onlinepharmacy.entities;

import java.io.Serializable;

public class Message implements Serializable {
    String type, message, createdAt, title;

    int flag;

    public Message() {
    }

    public Message(String type, String message, String createdAt) {
        this.type = type;
        this.message = message;
        this.createdAt = createdAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}