package com.commands.scheduling;

import kong.unirest.json.JSONObject;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScheduledEvent {

    private static final String nameHeader = "Event: ";
    private static final String timeHeader = "Time: ";
    private static final String dateHeader = "Date: ";
    private static final String attendingHeader = "Attending: ";
    private static final String absentHeader = "Not Attending: ";

    private final ZonedDateTime time;
    private final String name;
    private final String author;
    private String messageId;
    private String channelId;
    private String restId;
    private HashMap<String, Ticket> attending = new HashMap<>();
    private HashMap<String, Ticket> absent = new HashMap<>();

    public ScheduledEvent(String name, ZonedDateTime dateTime, String author, Ticket... attendees) {
        this.name = name;
        this.time = dateTime;
        this.author = author;
        for(Ticket ticket : attendees) {
            if(ticket.isAttending())
                this.attending.put(ticket.key(), ticket);
            else
                this.absent.put(ticket.key(), ticket);
        }
    }

    public ScheduledEvent(String restId, String messageId, String channelId, String name, ZonedDateTime time, String author) {
        this.restId = restId;
        this.messageId = messageId;
        this.channelId = channelId;
        this.name = name;
        this.time = time;
        this.author = author;
    }

    public ScheduledEvent(JSONObject json) {
        this.restId = json.get("_id").toString();
        this.messageId = json.get("MessageId").toString();
        this.channelId = json.get("ChannelId").toString();
        this.name = json.get("Name").toString();
        this.time = ZonedDateTime.parse(json.get("DateTime").toString());
        this.author = json.get("Author").toString();
    }

    private ZonedDateTime getDateFormatted(String date, int hour, int minute) {
        //Saturday, October 19
        String[] separated = date.split(" ");
        ZonedDateTime now = ZonedDateTime.now();
        int month = getMonth(separated[1]),
            day = Integer.parseInt(separated[2]);
        return ZonedDateTime.of(now.getYear(), month, day, hour, minute, 0, 0, now.getZone());
    }

    private int getMilHour(int hour, String period) {
        if(hour == 12) hour = 0;
        return hour + ((period.toUpperCase().equals("PM")) ? 12 : 0);
    }

    private int getMonth(String name) {
        switch(name.toUpperCase()) {
            case "JANUARY"  : return 1;
            case "FEBRUARY" : return 2;
            case "MARCH"    : return 3;
            case "APRIL"    : return 4;
            case "MAY"      : return 5;
            case "JUNE"     : return 6;
            case "JULY"     : return 7;
            case "AUGUST"   : return 8;
            case "SEPTEMBER": return 9;
            case "OCTOBER"  : return 10;
            case "NOVEMBER" : return 11;
            case "DECEMBER" : return 12;
            default: return -1;
        }
    }

    @Override
    public String toString() {
        return  "(" + name + ", " + messageId + ", " + channelId + ")";
    }

    public String formattedString() {
        StringBuilder stringBuilder = new StringBuilder();
        ZonedDateTime timeEST = time.plus(3, ChronoUnit.HOURS);
        int hourPST = (time.getHour() > 12) ? time.getHour() - 12 : time.getHour();
        int hourEST = (timeEST.getHour() > 12) ? timeEST.getHour() - 12 : timeEST.getHour();
        String periodPST = getPeriod(time.getHour());
        String periodEST = getPeriod(timeEST.getHour());
        String pstString = ((hourPST == 0) ? 12 : hourPST) + ":" + convertMin(time.getMinute()) + " " + periodPST + " PST";
        String estString = ((hourEST == 0) ? 12 : hourEST) + ":" + convertMin(timeEST.getMinute()) + " " + periodEST + " EST";
        String hold =
            nameHeader + name + "\n" +
            timeHeader + pstString + " (" + estString + ")\n" +
            dateHeader + toTitleCase(time.getDayOfWeek().toString()) + ", " + toTitleCase(time.getMonth().toString()) + " " + time.getDayOfMonth() + ", " + time.getYear();
        stringBuilder.append(hold);
        if(!attending.isEmpty()) {
            stringBuilder.append("\n"+ attendingHeader);
            attending.forEach((id, ticket) -> stringBuilder.append(" ").append(ticket.getMention()));
        }
        if(!absent.isEmpty()) {
            stringBuilder.append("\n" + absentHeader);
            absent.forEach((id, ticket) -> stringBuilder.append(" ").append(ticket.getMention()));
        }
        stringBuilder.append("\nReact to join. React with :no_entry_sign: if you are unable to attend.");
        return stringBuilder.toString();
    }

    private String toTitleCase(String in) {
        if(in.length() < 2) return in.toUpperCase();
        return in.substring(0, 1).toUpperCase() + in.substring(1).toLowerCase();
    }

    private String getPeriod(int hour) { return (hour < 12) ? "AM" : "PM"; }

    private String convertMin(int min) { return (min < 10) ? "0" + min : String.valueOf(min); }

    public ZonedDateTime getTime() { return time; }

    public String getName() { return name; }

    public String getAuthor() { return author; }

    public String getMessageId() { return messageId; }

    public HashMap<String, Ticket> getAttending() { return attending; }

    public String getRestId() { return restId; }

    public void setMessageId(String messageId) { this.messageId = messageId; }

    public void setRestId(String restId) { this.restId = restId; }

    public String getChannelId() { return channelId; }

    public void setChannelId(String channelId) { this.channelId = channelId; }

    public void setAttending(HashMap<String, Ticket> attending) { this.attending =  attending; }

    public void addTicket(Ticket ticket) {
        if (ticket.isAttending()) {
            absent.remove(ticket.key());
            attending.put(ticket.key(), ticket);
        }
        else {
            attending.remove(ticket.key());
            absent.put(ticket.key(), ticket);
        }
    }

    public void removeTicket(Ticket ticket) {
        absent.remove(ticket.key());
        attending.remove(ticket.key());
    }
}