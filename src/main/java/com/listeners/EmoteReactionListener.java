package com.listeners;

import com.Bot;
import com.commands.scheduling.ScheduledEvent;
import com.commands.scheduling.Ticket;
import com.utils.Utilities;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;

import java.util.HashMap;
import java.util.function.Consumer;

public class EmoteReactionListener extends ListenerAdapter {

    private final Bot bot;

    public EmoteReactionListener(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        RestAction<Message> msgRest = event.getChannel().retrieveMessageById(event.getMessageId());
        Consumer<Message> callback = (message) -> {
            //If the message wasn't made by the bot, return. If the message doesn't begin with "Event", return.
            if(message.getAuthor() != bot.getJda().getSelfUser() || !message.getContentRaw().startsWith("Event")) return;
            ScheduledEvent scheduledEvent = Utilities.getEventByIds(
                    bot.getConfig().getKey(),
                    message.getIdLong(),
                    message.getChannel().getIdLong());
            if(scheduledEvent != null) {
                HashMap<String, Ticket> attendees = Utilities.getAttendeesByEvent(
                        bot.getConfig().getKey(),
                        scheduledEvent.getRestId()
                );
                Ticket ticket = new Ticket(scheduledEvent.getRestId(), event.getUserId());
                if(!attendees.containsValue(ticket)) {
                    attendees.put(ticket.getRestId(), ticket);
                    scheduledEvent.setAttending(attendees);
                    Utilities.addAttendee(bot.getConfig().getKey(), ticket);
                    message.editMessage(scheduledEvent.formattedString()).queue();
                }
            }
            else {
                System.out.println("ReactionEvent error.");
            }
        };
        msgRest.queue(callback);
    }
}
