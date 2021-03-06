package com.settings;

import com.jagrosh.jdautilities.command.GuildSettingsManager;
import com.utils.Utilities;
import net.dv8tion.jda.api.entities.Guild;
import org.json.JSONObject;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.logging.Level;

public class SettingsManager implements GuildSettingsManager {

    private final HashMap<Long, Settings> settings;

    public SettingsManager() {
        this.settings = new HashMap<>();
        try {
            JSONObject savedSettings = new JSONObject(new String(Files.readAllBytes(Utilities.getPath("serversettings.json"))));
            savedSettings.keySet().forEach((key) -> {
                JSONObject obj = savedSettings.getJSONObject(key);
                settings.put(Long.parseLong(key), new Settings(
                        this,
                        obj.has("schedule_channel") ? obj.getString("schedule_channel") : null,
                        obj.has("voice_channel") ? obj.getString("voice_channel") : null
                ));
            });
        }
        catch (IOException e) {
            Utilities.log(Level.WARNING, "Unable to find server settings.");
        }
    }

    @Override
    public Object getSettings(Guild guild) {
        return getSettings(guild.getIdLong());
    }

    public Object getSettings(long guild) {
        return settings.computeIfAbsent(guild, id -> defaultSettings());
    }

    private Settings defaultSettings() {
        return new Settings(this, null, null);
    }

    private void writeSettings() {
        JSONObject obj = new JSONObject();
        settings.keySet().forEach((key) -> {
            JSONObject o = new JSONObject();
            Settings s = settings.get(key);
            if(s.getScheduleId()!=0)
                o.put("schedule_channel", Long.toString(s.getScheduleId()));
            if(s.getVoiceId()!=0)
                o.put("voice_channel", Long.toString(s.getVoiceId()));
            obj.put(Long.toString(key), o);
        });
        try {
            Files.write(Utilities.getPath("serversettings.json"), obj.toString(4).getBytes());
        } catch(IOException ex){
            Utilities.log(Level.SEVERE, "Unable to write to server settings file.");
        }
    }
}
