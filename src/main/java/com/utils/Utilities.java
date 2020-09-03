package com.utils;

import com.Bot;
import com.commands.scheduling.ScheduledEvent;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.HashMap;

public class Utilities {

    private final static String WINDOWS_INVALID_PATH = "c:\\windows\\system32\\";

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

    public static String verboseSend(MessageChannel channel, String content) {
        return channel.sendMessage(content).complete().getId();
    }

    public static HashMap<String, ScheduledEvent> getStartingEvents(ZonedDateTime time) {
        HashMap<String, ScheduledEvent> events = new HashMap<>();
        time = Utilities.simpleDate(time);
        try {
            JSONArray hold = Unirest
                .get("https://sophiadb-1e63.restdb.io/rest/events?q=" + URLEncoder.encode("{\"DateTime\": \"" + time.toString() + "\"}", "UTF-8"))
                .header("x-apikey", "424a281b952e3c472b5532069ff977adef923")
                .header("cache-control", "no-cache")
                .asJson()
                .getBody()
                .getArray();
            System.out.println(hold);
            for (Object obj : hold) {
                ScheduledEvent event = parseEvent(obj.toString());
                events.put(event.getEventId(), event);
            }
            return events;
        }
        catch (UnsupportedEncodingException e) {
            System.out.println("Encoding error");
            return null;
        }
    }

    public static ScheduledEvent pushEvent(ScheduledEvent event, String key) {
        String body = "{" +
            "\"MessageId\":\"" + event.getEventId() + "\"," +
            "\"ChannelId\":\"" + event.getChannelId() + "\"," +
            "\"Name\":\"" + event.getName() + "\"," +
            "\"DateTime\":\"" + event.getTime().toString() + "\"," +
            "\"Author\":\"" + event.getAuthor() + "\"" +
        "}";
        String hold = Unirest.post("https://sophiadb-1e63.restdb.io/rest/events")
            .header("content-type", "application/json")
            .header("x-apikey", key)
            .header("cache-control", "no-cache")
            .body(body)
            .asJson()
            .getBody()
            .toString();
        return parseEvent(hold);
    }

    private static ScheduledEvent parseEvent(String string) {
        HashMap<String, String> map = getStringMap(string);

        return new ScheduledEvent(
            map.get("_id"),
            map.get("MessageId"),
            map.get("ChannelId"),
            map.get("Name"),
            ZonedDateTime.parse(map.get("DateTime")),
            map.get("Author")
        );
    }

    private static HashMap<String, String> getStringMap(String string) {
        String[] raw = trim(string).split(",");
        HashMap<String, String> map = new HashMap<>();
        for (String entry : raw) {
            if(entry.contains(":")) {
                String[] hold = entry.split(":", 2);
                map.put(trim(hold[0]), trim(hold[1]));
            }
        }
        return map;
    }

    private static String trim(String string) {
        if(string.length() < 3) return "";
        return string.substring(1, string.length() - 1);
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
