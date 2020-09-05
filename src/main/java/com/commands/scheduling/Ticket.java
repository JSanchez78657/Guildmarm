package com.commands.scheduling;

public class Ticket {

    private String restId, userId;

    public Ticket(String restId, String userId) {
        this.restId = restId;
        this.userId = userId;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
