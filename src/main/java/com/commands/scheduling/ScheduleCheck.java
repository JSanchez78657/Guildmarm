package com.commands.scheduling;

import com.Bot;
import com.commands.GenericCommand;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.settings.Settings;
import com.utils.Utilities;

import java.time.ZonedDateTime;
import java.util.HashMap;

public class ScheduleCheck extends GenericCommand {

    public ScheduleCheck(Bot bot) {
        super(bot);
        this.name = "check";
        this.arguments = "";
        this.help = "Checks for any relevant events.";
    }

    @Override
    public void execCmd(CommandEvent event) {
        ZonedDateTime now = ZonedDateTime.now();
        System.out.println("Checking events... Time: " + now.toString());
        HashMap<String, ScheduledEvent> events = Utilities.getStartingEvents(now);
        StringBuilder hold = new StringBuilder();
        events.forEach((k,e) -> hold.append(e.toString()).append("\n"));
        System.out.println(hold.toString());
        event.reply(hold.toString());
    }
}
