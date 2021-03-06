package com.commands;

import com.Bot;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.settings.Settings;

public abstract class GenericCommand extends Command {

    protected final Bot bot;

    public GenericCommand(Bot bot) {
        this.bot = bot;
        this.category = new Category("General");
    }

    @Override
    protected void execute(CommandEvent event) {
        Settings settings = event.getClient().getSettingsFor(event.getGuild());
//        TextChannel channel = settings.getTextChannel(event.getGuild());
        //Checks if the command is enabled in channel.
//        if(channel != null && channel != event.getTextChannel()) {
//            try { event.getMessage().delete().queue(); }
//            catch (PermissionException e) { System.out.println("Permission denied."); }
//            event.replyInDm(event.getClient().getError() + "You must be in " + channel.getAsMention() + " to use that command.");
//        }
        execCmd(event);
    }

    public abstract void execCmd(CommandEvent event);
}
