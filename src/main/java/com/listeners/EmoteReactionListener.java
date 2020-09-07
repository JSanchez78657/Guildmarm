package com.listeners;

import com.Bot;
import com.commands.scheduling.ScheduledEvent;
import com.commands.scheduling.Ticket;
import com.utils.Utilities;
import jdk.jshell.execution.Util;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;

import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class EmoteReactionListener extends ListenerAdapter {

    private final Bot bot;
    private final String cancelEmote = "RE:U+1f6ab";

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
                if(event.getUserIdLong() == bot.getConfig().getOwner() && event.getReactionEmote().toString().equals(cancelEmote)) {
                    Utilities.removeEvent(bot.getConfig().getKey(), scheduledEvent);
                    Utilities.removeAttendeesByEvent(bot.getConfig().getKey(), scheduledEvent.getRestId());
                    message.editMessage("~~" + message.getContentRaw() + "~~").queue();
                }
                Ticket ticket = new Ticket(scheduledEvent.getRestId(), event.getUserId());
                if(!attendees.containsKey(ticket.key())) {
                    attendees.put(ticket.key(), ticket);
                    scheduledEvent.setAttending(attendees);
                    Utilities.addAttendee(bot.getConfig().getKey(), ticket);
                    message.editMessage(scheduledEvent.formattedString()).queue();
                }
            }
            else {
                System.out.println("Not a valid event.");
            }
        };
        msgRest.queue(callback);
    }

    @Override
    public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent event) {
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
                if(attendees.containsKey(ticket.key())) {
                    ticket = attendees.remove(ticket.key());
                    scheduledEvent.setAttending(attendees);
                    Utilities.removeAttendee(bot.getConfig().getKey(), ticket);
                    message.editMessage(scheduledEvent.formattedString()).queue();
                }
            }
            else {
                System.out.println("Not a valid event.");
            }
        };
        msgRest.queue(callback);
    }
}
