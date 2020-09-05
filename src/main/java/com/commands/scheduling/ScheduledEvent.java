package com.commands.scheduling;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.*;

public class ScheduledEvent {

    private static String nameHeader = "Event: ";
    private static String timeHeader = "Time: ";
    private static String dateHeader = "Date: ";
    private static String attendingHeader = "Attending:";

    private ZonedDateTime time;
    private String name;
    private String author;
    private String eventId;
    private String channelId;
    private String restId;
    private HashMap<String, Ticket> attending;

    public ScheduledEvent(String name, String dateTime, String author, Ticket... attendees) {
        this.name = name;
        this.time = getDate(dateTime);
        this.author = author;
        this.attending = new HashMap<>();
        for(Ticket ticket : attendees) { this.attending.put(ticket.getRestId(), ticket); }
    }

    public ScheduledEvent(String eventId, String name, ZonedDateTime time, String author) {
        this.eventId = eventId;
        this.name = name;
        this.time = time;
        this.author = author;
        this.attending = new HashMap<>();
    }

    public ScheduledEvent(String restId, String eventId, String channelId, String name, ZonedDateTime time, String author) {
        this.restId = restId;
        this.eventId = eventId;
        this.channelId = channelId;
        this.name = name;
        this.time = time;
        this.author = author;
        this.attending = new HashMap<>();
    }

    //Month Day XX:XX AM/PM
    private ZonedDateTime getDate(String raw){
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("M d h:m a")
                .parseDefaulting(ChronoField.YEAR, ZonedDateTime.now().getYear())
                .toFormatter(Locale.US);
        LocalDateTime hold = LocalDateTime.parse(raw.trim().toUpperCase(), formatter);
        if(hold.isBefore(LocalDateTime.now())) hold = hold.plusYears(1);
        return ZonedDateTime.from(hold.atZone(ZoneId.systemDefault()));
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

    public boolean hasUser(Ticket ticket) { return attending.containsKey(ticket.getRestId()); }

    public void addUser(Ticket attendee) {
        attending.put(attendee.getRestId(), attendee);
    }

    public void removeUser(Ticket attendee) {
        attending.remove(attendee.getRestId());
    }

    @Override
    public String toString() {
        return  name + ", at " + time.getHour() + ":" + convertMin(time.getMinute()) + " on " +
                toTitleCase(time.getDayOfWeek().toString()) + ", " + toTitleCase(time.getMonth().toString()) + " " + time.getDayOfMonth() + ".";
    }

    public String formattedString() {
        String period = getPeriod(time.getHour());
        StringBuilder stringBuilder = new StringBuilder();
        int hour = (time.getHour() > 12) ? time.getHour() - 12 : time.getHour();
        String hold =
            nameHeader + name + "\n" +
            timeHeader + ((hour == 0) ? 12 : hour) + ":" + convertMin(time.getMinute()) + " " + period + " PST\n" +
            dateHeader + toTitleCase(time.getDayOfWeek().toString()) + ", " + toTitleCase(time.getMonth().toString()) + " " + time.getDayOfMonth() + ", " + time.getYear() + "\n";
        stringBuilder.append(hold);
        if(!attending.isEmpty()) {
            List<Ticket> list = new ArrayList<>(attending.values());
            stringBuilder.append(attendingHeader);
            for(Ticket ticket : list) stringBuilder.append(" ").append(ticket.getMention());
        }
        stringBuilder.append("\nReact to join.");
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

    public String getEventId() { return eventId; }

    public HashMap<String, Ticket> getAttending() { return attending; }

    public String getRestId() { return restId; }

    public void setEventId(String eventId) { this.eventId = eventId; }

    public void setRestId(String restId) { this.restId = restId; }

    public String getChannelId() { return channelId; }

    public void setChannelId(String channelId) { this.channelId = channelId; }

    public void setAttending(HashMap<String, Ticket> attending) { this.attending =  attending; }
}