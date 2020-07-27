package com;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.settings.SettingsManager;
import net.dv8tion.jda.api.JDA;

public class Bot {
    private final EventWaiter waiter;
    private final BotConfig config;
    private final SettingsManager settings;

    private JDA jda;

    public Bot(EventWaiter waiter, BotConfig config, SettingsManager settings) {
        this.waiter = waiter;
        this.config = config;
        this.settings = settings;
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
}
