package com.commands.scheduling;

public class Ticket {

    private String restId, messageId, userId;

    public Ticket(String restId, String messageId, String userId) {
        this.restId = restId;
        this.messageId = messageId;
        this.userId = userId;
    }

    public Ticket(String messageId, String userId) {
        this.messageId = messageId;
        this.userId = userId;
    }

    public String getKey() {
        return getMessageId().concat(getUserId());
    }

    public String getMention() {
        return "<@" + getUserId() + ">";
    }

    public String getRestId() {
        return restId;
    }

    public void setRestId(String restId) {
        this.restId = restId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
