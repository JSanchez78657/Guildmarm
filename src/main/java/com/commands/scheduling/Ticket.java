package com.commands.scheduling;

import kong.unirest.json.JSONObject;

public class Ticket {

    private String restId, eventId, userId;

    public Ticket(String eventId, String userId) {
        this.eventId = eventId;
        this.userId = userId;
    }

    public Ticket(JSONObject json) {
        this.restId = json.get("_id").toString();
        this.eventId = json.get("EventId").toString();
        this.userId = json.get("UserId").toString();
    }

    public String getMention() {
        return "<@" + getUserId() + ">";
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRestId() {
        return restId;
    }

    public void setRestId(String restId) {
        this.restId = restId;
    }

    public String key() { return this.eventId.concat(this.userId); }
}
