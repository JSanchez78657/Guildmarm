package com.commands.admin;

import com.Bot;
import com.commands.GenericCommand;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.settings.Settings;
import com.utils.Utilities;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.logging.Level;

public class ShutdownCommand extends GenericCommand {

    public ShutdownCommand(Bot bot) {
        super(bot);
        this.category = new Category("Administration");
        this.name = "shutdown";
        this.arguments = "";
        this.help = "Shuts down bot. Only works for the bot owner.";
    }

    @Override
    public void execCmd(CommandEvent event) {
        event.getMessage().delete().queue();
        if(bot.getConfig().getOwner() != event.getAuthor().getIdLong()) {
            event.replyInDm("Hey, you stop that.");
            Utilities.log(Level.WARNING, event.getAuthor().getName() + " (" + event.getAuthor().getId() + ") attempted shutdown command.");
        }
        else {
            Utilities.log(Level.INFO, event.getAuthor().getName() + " (" + event.getAuthor().getId() + ") initiated shutdown.");
            try {
                Thread.sleep(2000);
                bot.getJda().shutdown();
                System.exit(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
