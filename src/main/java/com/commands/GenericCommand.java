package com.commands;

import com.Bot;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.settings.Settings;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.PermissionException;

public abstract class GenericCommand extends Command {

    protected final Bot bot;

    public GenericCommand(Bot bot) {
        this.bot = bot;
        this.category = new Category("general");
    }

    @Override
    protected void execute(CommandEvent event) {
        Settings settings = event.getClient().getSettingsFor(event.getGuild());
        TextChannel channel = settings.getTextChannel(event.getGuild());
        System.out.println(settings);
        //Checks if the command is enabled in channel.
        if(channel != null && channel != event.getTextChannel()) {
            try { event.getMessage().delete().queue(); }
            catch (PermissionException e) { System.out.println("Permission denied."); }
            event.replyInDm(event.getClient().getError() + "You must be in " + channel.getAsMention() + " to use that command.");
        }
        execCmd(event);
    }

    public abstract void execCmd(CommandEvent event);
}
