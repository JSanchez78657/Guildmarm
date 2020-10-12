package com;

import com.commands.scheduling.ScheduledEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.settings.SettingsManager;
import com.utils.Utilities;
import net.dv8tion.jda.api.JDA;

import java.util.HashMap;

public class Bot {
    private final EventWaiter waiter;
    private final BotConfig config;
    private final SettingsManager settings;

    private JDA jda;
    private HashMap<String, ScheduledEvent> events;

    public Bot(EventWaiter waiter, BotConfig config, SettingsManager settings) {
        this.waiter = waiter;
        this.config = config;
        this.settings = settings;
        this.events = Utilities.getAllEvents(this.config.getKey());
    }

    public EventWaiter getWaiter() {
        return waiter;
    }

    public BotConfig getConfig() {
        return config;
    }

    public SettingsManager getSettings() {
        return settings;
    }

    public JDA getJda() {
        return jda;
    }

    public void setJda(JDA jda) {
        this.jda = jda;
    }

    public void addEvent(ScheduledEvent event) { events.put(event.getMessageId(), event); }

    public void removeEvent(ScheduledEvent event) { events.remove(event.getMessageId()); }

    public HashMap<String, ScheduledEvent> getEvents() { return events; }

    public void setEvents(HashMap<String, ScheduledEvent> events) { this.events = events; }
}
