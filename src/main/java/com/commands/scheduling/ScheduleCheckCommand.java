package com.commands.scheduling;

import com.Bot;
import com.commands.GenericCommand;
import com.commands.scheduling.ScheduledEvent;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.settings.Settings;
import com.utils.Utilities;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Objects;

public class ScheduleCheckCommand extends GenericCommand {

    public ScheduleCheckCommand(Bot bot) {
        super(bot);
        this.name = "check";
        this.arguments = "";
        this.help = "Checks for any relevant events.";
    }

    @Override
    public void execCmd(CommandEvent event) {
        ZonedDateTime now = ZonedDateTime.now();
        System.out.println("Checking events... Time: " + now.toString());
        HashMap<String, ScheduledEvent> events = Utilities.getStartingEvents(bot.getConfig().getKey(),now);
        if (events != null)
            events.forEach((k,e) -> {
                Objects.requireNonNull(event.getJDA().getTextChannelById(e.getChannelId()))
                        .sendMessage("Event Starting: " + e.getName())
                        .queue();
                System.out.println(e.toString());
            });
    }
}
