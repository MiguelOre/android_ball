package com.app.mg.aoe.upc.Entities;

public class MessageBody {
    private String sender;
    private boolean toTV;
    private String message;

    public MessageBody() {
    }

    public MessageBody(String sender, boolean toTV, String message) {
        this.sender = sender;
        this.toTV = toTV;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public MessageBody setSender(String sender) {
        this.sender = sender;
        return this;
    }

    public boolean isToTV() {
        return toTV;
    }

    public MessageBody setToTV(boolean toTV) {
        this.toTV = toTV;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public MessageBody setMessage(String message) {
        this.message = message;
        return this;
    }
}
