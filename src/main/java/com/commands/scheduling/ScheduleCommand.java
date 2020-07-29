package com.commands.scheduling;

import com.Bot;
import com.commands.GenericCommand;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class ScheduleCommand extends GenericCommand {

    public ScheduleCommand(Bot bot) {
        super(bot);
        this.name = "schedule";
        this.arguments = "<event_name|date_time>";
        this.help = "schedules given event.";
    }

    @Override
    public void execCmd(CommandEvent event) {
        //TODO: FIX THE FUCKING SERVERSETTINGS LOADING
        System.out.println("command!");
    }
}
