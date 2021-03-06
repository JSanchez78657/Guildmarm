package com.commands.scheduling;

import com.Bot;
import com.commands.GenericCommand;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.settings.Settings;
import com.utils.Utilities;
import kong.unirest.JsonNode;
import kong.unirest.json.JSONObject;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.PermissionException;
import java.time.ZonedDateTime;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.logging.Level;

public class ScheduleCommand extends GenericCommand {

    public ScheduleCommand(Bot bot) {
        super(bot);
        this.category = new Category("Scheduling");
        this.name = "schedule";
        this.arguments = "[Event Name], [MM DD HH:mm AM/PM]";
        this.help =
                "Schedules a given event for a given time. " +
                "Events can be joined by reacting to the event's post in Discord." +
                "Events are cancelled by reacting with `:no_entry_sign:`. Only the event creator may do this." +
                "At the time of the event, Guildmarm will notify all attendees of the event.";
    }

    @Override
    public void execCmd(CommandEvent event) {
        Settings settings = event.getClient().getSettingsFor(event.getGuild());
        TextChannel scheduleChannel = settings.getScheduleChannel(event.getGuild());
        TextChannel sendChannel;
        Message message = event.getMessage();
        StringTokenizer args = new StringTokenizer(event.getArgs(), ",");
        String eventName, eventTime;
        try {
            eventName = args.nextToken();
            eventTime = args.nextToken();
        }
        catch (NoSuchElementException e) {
            event.replyInDm("Invalid argument format. \n[*schedule EventName, MM DD HH:mm AM/PM]");
            Utilities.log(Level.INFO,
            "(" + event.getAuthor().getName() + ", " + event.getAuthor().getId() + ") attempted " +
                    "'" + message.getContentRaw() + "'. NoSuchElementException tripped."
            );
            return;
        }
        String author = message.getAuthor().getId();
        ZonedDateTime eventDate = Utilities.getDate(eventTime);
        if(eventDate == null) {
            event.replyInDm("Incorrect DateTime format, must be in the form [MM DD HH:mm AM/PM]");
            Utilities.log(Level.INFO,
            "(" + event.getAuthor().getName() + ", " + event.getAuthor().getId() + ") attempted " +
                    "'" + message.getContentRaw() + "'. IncorrectDateTime tripped."
            );
            return;
        }
        ScheduledEvent scheduledEvent = new ScheduledEvent(eventName, eventDate, author);
        try { event.getMessage().delete().queue(); }
        catch (PermissionException e) { Utilities.log(Level.WARNING, "Could not delete message for a schedule command."); }
        //If the server has a specific channel for schedules, send it there, otherwise just reply.
        sendChannel = (scheduleChannel != null) ? scheduleChannel : event.getTextChannel();
        sendChannel.sendMessage(scheduledEvent.formattedString()).queue(m -> {
            scheduledEvent.setMessageId(m.getId());
            scheduledEvent.setChannelId(sendChannel.getId());
            bot.addEvent(new ScheduledEvent(Utilities.pushEvent(bot.getConfig().getKey(), scheduledEvent)));
            Utilities.log(Level.INFO,
                    "(" + event.getAuthor().getName() + ", " + event.getAuthor().getId() + ") started " +
                            scheduledEvent.toString() + "."
            );
        });
    }
}
