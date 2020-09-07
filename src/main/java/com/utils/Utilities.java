package com.utils;

import com.Bot;
import com.commands.scheduling.ScheduledEvent;
import com.commands.scheduling.Ticket;
import kong.unirest.*;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.HashMap;
import java.util.Locale;

public class Utilities {

    private final static String WINDOWS_INVALID_PATH = "c:\\windows\\system32\\";
    private static String apiKey;

    public static Path getPath(String path) {
        path = "src/main/resources/" + path;
        //Special logic to prevent trying to access system32
        if(path.toLowerCase().startsWith(WINDOWS_INVALID_PATH))
        {
            String filename = path.substring(WINDOWS_INVALID_PATH.length());
            try
            {
                path = new File(Bot.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getPath() + File.separator + filename;
            }
            catch(URISyntaxException ex) {
                System.out.println("URISyntax Error!");
            }
        }
        return Paths.get(path);
    }

    public static HashMap<String, ScheduledEvent> getStartingEvents(String key, ZonedDateTime time) {
        HashMap<String, ScheduledEvent> events = new HashMap<>();
        time = Utilities.simpleDate(time);
        try {
            JSONArray hold = Unirest
                .get("https://sophiadb-1e63.restdb.io/rest/events?q=" + URLEncoder.encode("{\"DateTime\": \"" + time.toString() + "\"}", "UTF-8"))
                .header("x-apikey", key)
                .header("cache-control", "no-cache")
                .asJson()
                .getBody()
                .getArray();
            hold.forEach((entry) -> {
                if (entry instanceof JSONObject) {
                    ScheduledEvent event = new ScheduledEvent((JSONObject) entry);
                    events.put(event.getRestId(), event);
                }
            });
            return events;
        }
        catch (UnsupportedEncodingException e) {
            System.out.println("Encoding error");
            return null;
        }
    }

    public static ScheduledEvent getEventByIds(String key, long messageId, long channelId) {
        try {
            JSONArray hold = Unirest.get("https://sophiadb-1e63.restdb.io/rest/events?q=" + URLEncoder.encode(
                        "{\"MessageId\": \"" + messageId + "\", \"ChannelId\": \"" + channelId + "\"}", "UTF-8"
                        )
                    )
                    .header("x-apikey", key)
                    .header("cache-control", "no-cache")
                    .asJson()
                    .getBody()
                    .getArray();
            //I hate this. Find a better way of doing this in future.
            return new ScheduledEvent((JSONObject) hold.get(0));
        }
        catch (UnsupportedEncodingException e) {
            System.out.println("Encoding error");
            return null;
        }
    }

    public static HashMap<String, Ticket> getAttendeesByEvent(String key, String eventId) {
        HashMap<String, Ticket> tickets = new HashMap<>();
        try {
            JSONArray hold = Unirest.get("https://sophiadb-1e63.restdb.io/rest/attendees?q="
                    + URLEncoder.encode("{\"EventId\": \"" + eventId + "\"}", "UTF-8"))
                    .header("x-apikey", key)
                    .header("cache-control", "no-cache")
                    .asJson()
                    .getBody()
                    .getArray();
            hold.forEach((entry) -> {
                if (entry instanceof JSONObject) {
                    Ticket ticket = new Ticket((JSONObject) entry);
                    tickets.put(ticket.key(), ticket);
                }
            });
            return tickets;
        }
        catch (UnsupportedEncodingException e) {
            System.out.println("Encoding error");
            return null;
        }
    }

    public static void removeAttendeesByEvent(String key, String eventId) {
        //TODO: This needs to be fixed.
        try {
            Unirest.delete("https://sophiadb-1e63.restdb.io/rest/attendees/"
                    + URLEncoder.encode("*?q={\"EventId\": \"" + eventId + "\"}", "UTF-8"))
                    .header("x-apikey", key)
                    .header("cache-control", "no-cache")
                    .asString();
        }
        catch (UnsupportedEncodingException e) {
            System.out.println("Encoding error");
        }
    }

    public static void pushEvent(String key, ScheduledEvent event) {
        String body = "{" +
            "\"MessageId\":\"" + event.getMessageId() + "\"," +
            "\"ChannelId\":\"" + event.getChannelId() + "\"," +
            "\"Name\":\"" + event.getName() + "\"," +
            "\"DateTime\":\"" + event.getTime().toString() + "\"," +
            "\"Author\":\"" + event.getAuthor() + "\"" +
        "}";
        Unirest.post("https://sophiadb-1e63.restdb.io/rest/events")
            .header("content-type", "application/json")
            .header("x-apikey", key)
            .header("cache-control", "no-cache")
            .body(body)
            .asString();
    }

    public static void removeEvent(String key, ScheduledEvent event) {
        Unirest.delete("https://sophiadb-1e63.restdb.io/rest/events/" + event.getRestId())
                .header("content-type", "application/json")
                .header("x-apikey", key)
                .header("cache-control", "no-cache")
                .asString();
    }

    public static void addAttendee(String key, Ticket ticket) {
        String body = "{" +
            "\"EventId\":\"" + ticket.getEventId() + "\"," +
            "\"UserId\":\"" + ticket.getUserId() + "\"" +
        "}";
        Unirest.post("https://sophiadb-1e63.restdb.io/rest/attendees")
                .header("content-type", "application/json")
                .header("x-apikey", key)
                .header("cache-control", "no-cache")
                .body(body)
                .asString();
    }

    public static void removeAttendee(String key, Ticket ticket) {
        Unirest.delete("https://sophiadb-1e63.restdb.io/rest/attendees/" + ticket.getRestId())
                .header("content-type", "application/json")
                .header("x-apikey", key)
                .header("cache-control", "no-cache")
                .asString();
    }

    //Month Day XX:XX AM/PM
    public static ZonedDateTime getDate(String raw) {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("M d h:m a")
                .parseDefaulting(ChronoField.YEAR, ZonedDateTime.now().getYear())
                .toFormatter(Locale.US);
        try {
            LocalDateTime hold = LocalDateTime.parse(raw.trim().toUpperCase(), formatter);
            if (hold.isBefore(LocalDateTime.now())) hold = hold.plusYears(1);
            return ZonedDateTime.from(hold.atZone(ZoneId.systemDefault()));
        }
        catch (DateTimeException e) {
            return null;
        }
    }

    public static ZonedDateTime simpleDate(ZonedDateTime time) {
        return ZonedDateTime.of(
            time.getYear(),
            time.getMonthValue(),
            time.getDayOfMonth(),
            time.getHour(),
            time.getMinute(),
            0,
            0,
            time.getZone()
        );
    }
}
