package com.utils;

import com.Bot;
import com.commands.scheduling.ScheduledEvent;
import kong.unirest.Unirest;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.io.File;
import java.net.URISyntaxException;
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
        String id = channel.sendMessage(content).complete().getId();
        return id;
    }

    public static ScheduledEvent pushEvent(ScheduledEvent event, String key) {
        String body =
                "{\"MessageId\":\"" + event.getEventId() + "\"," +
                        "\"Name\":\"" + event.getName() + "\"," +
                        "\"DateTime\":\"" + event.getTime().toString() + "\"," +
                        "\"Author\":\"" + event.getAuthor() + "\"}";
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
}
