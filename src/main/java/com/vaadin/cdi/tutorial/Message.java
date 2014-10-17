package com.vaadin.cdi.tutorial;

import java.util.Date;

public class Message {

    private final User sender;
    private final User recipient;
    private final String message;
    private final Date sendTime;

    public Message(User sender, User recipient, String message) {
        this.sender = sender;
        this.recipient = recipient;
        this.message = message;
        this.sendTime = new Date();
    }

    public Message(User sender, User recipient, String message, Date sendTime) {
        this.sender = sender;
        this.recipient = recipient;
        this.message = message;
        this.sendTime = sendTime;
    }

    public User getSender() {
        return sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public String getMessage() {
        return message;
    }

    public Date getSendTime() {
        return sendTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(sendTime.toLocaleString());
        sb.append("] ");
        sb.append(sender.getName());
        sb.append(": ");
        sb.append(message);
        return sb.toString();
    }

    public boolean involves(User... users) {
        for (User user : users) {
            if (!user.equals(recipient) && !user.equals(sender)) {
                return false;
            }
        }
        return true;
    }
}
