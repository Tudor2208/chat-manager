package org.sdi.chatmanager.dtos;

import org.sdi.chatmanager.entities.User;

import java.util.Date;

public class MessageResponse {
    private Long id;
    private User sender;
    private User recipient;
    private String text;
    private Date timestamp;

    public MessageResponse(Long id, User sender, User recipient, String text, Date timestamp) {
        this.id = id;
        this.sender = sender;
        this.recipient = recipient;
        this.text = text;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
