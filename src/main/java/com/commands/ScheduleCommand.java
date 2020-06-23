package com.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class ScheduleCommand extends Command {

    private String key;
    private String replyChannel;

    public ScheduleCommand(String key) { this.key = key; }

    @Override
    protected void execute(CommandEvent event) {
    }
}
