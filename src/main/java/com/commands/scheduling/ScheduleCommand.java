package com.commands.scheduling;

import com.Bot;
import com.commands.GenericCommand;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.settings.Settings;
import com.utils.Utilities;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.PermissionException;

import java.time.DateTimeException;
import java.util.Objects;

public class ScheduleCommand extends GenericCommand {

    public ScheduleCommand(Bot bot) {
        super(bot);
        this.name = "schedule";
        this.arguments = "<event_name|date_time>";
        this.help = "schedules given event.";
    }

    @Override
    public void execCmd(CommandEvent event) {
        Settings settings = event.getClient().getSettingsFor(event.getGuild());
        TextChannel scheduleChannel = settings.getScheduleChannel(event.getGuild());
        Message message = event.getMessage();
        String args = event.getArgs();
        String eventName = args.substring(0, args.lastIndexOf(','));
        String eventTime = args.substring(eventName.length() + 2);
        String author = message.getAuthor().getId();
        ScheduledEvent scheduledEvent = new ScheduledEvent(eventName, eventTime, author);
        try { event.getMessage().delete().queue(); }
        catch (PermissionException e) { System.out.println("Permission denied."); }
        try {
            scheduledEvent.setEventId(Utilities.verboseSend((scheduleChannel != null) ? scheduleChannel : event.getChannel(), scheduledEvent.formattedString()));
            Utilities.pushEvent(scheduledEvent, bot.getConfig().getKey());
        }
        catch(DateTimeException d) {
            event.replyInDm("Invalid Date/Time format.\n[*schedule Name, MM DD HH:mm AM/PM]");
        }
    }
}
