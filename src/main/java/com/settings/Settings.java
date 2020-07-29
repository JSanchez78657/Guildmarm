package com.settings;

import com.jagrosh.jdautilities.command.GuildSettingsProvider;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class Settings implements GuildSettingsProvider {

    private final SettingsManager manager;
    private long textId;
    private long voiceId;

    public Settings(SettingsManager manager, String textId, String voiceId) {
        this.manager = manager;
        try { this.textId = Long.parseLong(textId); }
        catch (NumberFormatException e) { this.textId = 0; }
        try { this.voiceId = Long.parseLong(voiceId); }
        catch (NumberFormatException e) { this.voiceId = 0; }
    }


    public long getTextId() {
        return textId;
    }

    public void setTextId(long textId) {
        this.textId = textId;
    }

    public TextChannel getTextChannel(Guild guild) {
         return (guild == null) ? null : guild.getTextChannelById(textId);
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
        return "Setting: \nTextId: " + textId + "\nVoiceId: " + voiceId;
    }
}
