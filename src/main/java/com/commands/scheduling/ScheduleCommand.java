package com.commands.scheduling;

import com.Bot;
import com.commands.GenericCommand;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.settings.Settings;
import com.utils.Utilities;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.PermissionException;

import java.time.DateTimeException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.function.Consumer;

public class ScheduleCommand extends GenericCommand {

    public ScheduleCommand(Bot bot) {
        super(bot);
        this.name = "schedule";
        this.arguments = "<event_name>, <date_time>";
        this.help = "schedules given event.";
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
            return;
        }
        String author = message.getAuthor().getId();
        ScheduledEvent scheduledEvent = new ScheduledEvent(eventName, eventTime, author);
        try { event.getMessage().delete().queue(); }
        catch (PermissionException e) { System.out.println("Permission denied."); }
        try {
            //If the server has a specific channel for schedules, send it there, otherwise just reply.
            sendChannel = (scheduleChannel != null) ? scheduleChannel : event.getTextChannel();
            sendChannel.sendMessage(scheduledEvent.formattedString()).queue(m -> {
                scheduledEvent.setEventId(m.getId());
                scheduledEvent.setChannelId(sendChannel.getId());
                Utilities.pushEvent(scheduledEvent, bot.getConfig().getKey());
            });
        }
        catch(DateTimeException d) {
            event.replyInDm("Invalid Date/Time format.\n[*schedule Name, MM DD HH:mm AM/PM]");
        }
    }
}
