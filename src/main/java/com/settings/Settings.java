package com.settings;

import com.jagrosh.jdautilities.command.GuildSettingsProvider;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class Settings implements GuildSettingsProvider {

    private final SettingsManager manager;
    private long scheduleId;
    private long voiceId;

    public Settings(SettingsManager manager, String scheduleId, String voiceId) {
        this.manager = manager;
        try { this.scheduleId = Long.parseLong(scheduleId); }
        catch (NumberFormatException e) { this.scheduleId = 0; }
        try { this.voiceId = Long.parseLong(voiceId); }
        catch (NumberFormatException e) { this.voiceId = 0; }
    }


    public long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public TextChannel getScheduleChannel (Guild guild) {
         return (guild == null) ? null : guild.getTextChannelById(scheduleId);
    }

    public long getVoiceId() {
        return voiceId;
    }

    public void setVoiceId(long voiceId) {
        this.voiceId = voiceId;
    }

    public VoiceChannel getVoiceChannel(Guild guild) {
        return (guild == null) ? null : guild.getVoiceChannelById(voiceId);
    }

    @Override
    public String toString() {
        return "Setting: \nTextId: " + scheduleId + "\nVoiceId: " + voiceId;
    }
}
