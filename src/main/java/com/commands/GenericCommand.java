package com.commands;

import com.Bot;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import okhttp3.internal.http2.Settings;

public class GenericCommand extends Command {

    protected final Bot bot;

    public GenericCommand(Bot bot) {
        this.bot = bot;
    }

    @Override
    protected void execute(CommandEvent event) {
        //TODO: Work on getting settings working.
    }
}
