package com.commands.admin;

import com.Bot;
import com.commands.GenericCommand;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.settings.Settings;
import net.dv8tion.jda.api.entities.TextChannel;

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
        if(bot.getConfig().getOwner() != event.getAuthor().getIdLong()) {
            event.getMessage().delete().queue();
            event.replyInDm("Hey, you stop that.");
        }
        else {
            event.getMessage().delete().queue();
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
