package com.commands.scheduling;

import com.Bot;
import com.commands.GenericCommand;
import com.commands.scheduling.ScheduledEvent;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.settings.Settings;
import com.utils.Utilities;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.RestAction;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Consumer;

public class ScheduleCheckCommand extends GenericCommand {

    public ScheduleCheckCommand(Bot bot) {
        super(bot);
        this.name = "check";
        this.arguments = "";
        this.help = "Checks for any relevant events.";
    }

    @Override
    public void execCmd(CommandEvent event) {
//        ZonedDateTime now = ZonedDateTime.now();
//        System.out.println("Checking events... Time: " + now.toString());
//        HashMap<String, ScheduledEvent> events = Utilities.getStartingEvents(bot.getConfig().getKey(),now);
//        if (events != null)
//            events.forEach((k,e) -> {
//                TextChannel channel = Objects.requireNonNull(event.getJDA().getTextChannelById(e.getChannelId()));
//                RestAction<Message> fetchMessage = channel.retrieveMessageById(e.getMessageId());
//                Consumer<Message> callback = (message) -> {
//                    Utilities.removeEvent(bot.getConfig().getKey(), e);
//                    Utilities.removeAttendeesByEvent(bot.getConfig().getKey(), e.getRestId());
//                    channel.editMessageById(e.getMessageId(), "~~" + message.getContentRaw() +"~~").queue();
//                    channel.sendMessage("Event Starting: " + e.getName()).queue();
//                };
//                fetchMessage.queue(callback);
//            });
    }
}
