package com.listeners;

import com.Bot;
import com.commands.scheduling.ScheduledEvent;
import com.commands.scheduling.Ticket;
import com.utils.Utilities;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.logging.Level;

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
            ScheduledEvent scheduledEvent = getEvent(message);
            if(scheduledEvent != null) {
                HashMap<String, Ticket> tickets = Utilities.getAttendeesByEvent(
                        bot.getConfig().getKey(),
                        scheduledEvent.getRestId()
                );
                //If the reaction is a cancel emote, cancel the event.
                if(event.getUserIdLong() == bot.getConfig().getOwner() && event.getReactionEmote().toString().equals(cancelEmote)) {
                    bot.removeEvent(scheduledEvent);
                    if (tickets != null) {
                        tickets.forEach((tk, ticket) -> Utilities.removeAttendee(bot.getConfig().getKey(), ticket));
                    }
                    message.editMessage("~~" + message.getContentRaw() + "~~").queue();
                    Utilities.removeEvent(bot.getConfig().getKey(), scheduledEvent);
                    Utilities.log(Level.INFO,
                            "(" + ((event.getUser() != null) ? event.getUser().getName() : "") + ", " + event.getUserId() + ") cancelled " +
                                    scheduledEvent.toString() + "."
                    );
                    return;
                }
                Ticket ticket = new Ticket(scheduledEvent.getRestId(), event.getUserId());
                if (tickets != null && !tickets.containsKey(ticket.key())) {
                    tickets.put(ticket.key(), ticket);
                    scheduledEvent.setAttending(tickets);
                    message.editMessage(scheduledEvent.formattedString()).queue();
                    Utilities.addAttendee(bot.getConfig().getKey(), ticket);
                }
                Utilities.log(Level.INFO,
                "(" + ((event.getUser() != null) ? event.getUser().getName() : "") + ", " + event.getUserId() + ") joined " +
                        scheduledEvent.toString() + "."
                );
            }
            else {
                Utilities.log(Level.WARNING,
                "(" + ((event.getUser() != null) ? event.getUser().getName() : "") + ", " + event.getUserId() + ") added reaction to " +
                        "(" + event.getMessageId() + ", " + event.getChannel().getId() + ") this is not a valid event."
                );
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
            ScheduledEvent scheduledEvent = getEvent(message);
            if(scheduledEvent != null) {
                HashMap<String, Ticket> attendees = Utilities.getAttendeesByEvent(
                        bot.getConfig().getKey(),
                        scheduledEvent.getRestId()
                );
                Ticket ticket = new Ticket(scheduledEvent.getRestId(), event.getUserId());
                if (attendees != null && attendees.containsKey(ticket.key())) {
                    ticket = attendees.remove(ticket.key());
                    scheduledEvent.setAttending(attendees);
                    message.editMessage(scheduledEvent.formattedString()).queue();
                    Utilities.removeAttendee(bot.getConfig().getKey(), ticket);
                }
                Utilities.log(Level.INFO,
                "(" + ((event.getUser() != null) ? event.getUser().getName() : "") + ", " + event.getUserId() + ") left " +
                        scheduledEvent.toString() + "."
                );
            }
            else {
                Utilities.log(Level.WARNING,
                "(" + ((event.getUser() != null) ? event.getUser().getName() : "") + ", " + event.getUserId() + ") removed reaction to " +
                        "(" + event.getMessageId() + ", " + event.getChannel().getId() + ") this is not a valid event."
                );
            }
        };
        msgRest.queue(callback);
    }

    private ScheduledEvent getEvent(Message message) {
        for(String key : bot.getEvents().keySet()) {
            ScheduledEvent hold = bot.getEvents().get(key);
            if(hold.getMessageId().equals(message.getId()) && hold.getChannelId().equals(message.getChannel().getId()))
                return hold;
        }
        return Utilities.getEventByIds(
                bot.getConfig().getKey(),
                message.getIdLong(),
                message.getChannel().getIdLong()
        );
    }
}
