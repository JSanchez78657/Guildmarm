package com.listeners;

import com.Bot;
import com.commands.scheduling.ScheduledEvent;
import com.commands.scheduling.Ticket;
import com.utils.Utilities;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class EventTimerListener extends ListenerAdapter {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Bot bot;

    public EventTimerListener(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        System.out.println("Starting Timer");
        //"check" contains what is run every minute by the scheduler.
        //In this case, it is checking to see if any events are scheduled at the given time.
        final Runnable check = () -> {
            ZonedDateTime now = ZonedDateTime.now();
            HashMap<String, ScheduledEvent> events = Utilities.getStartingEvents(bot.getConfig().getKey(),now);
            if (events != null)
                events.forEach((k,e) -> {
                    TextChannel channel = Objects.requireNonNull(event.getJDA().getTextChannelById(e.getChannelId()));
                    HashMap<String, Ticket> tickets = Utilities.getAttendeesByEvent(bot.getConfig().getKey(), e.getRestId());
                    RestAction<Message> fetchMessage = channel.retrieveMessageById(e.getMessageId());
                    Consumer<Message> callback = (message) -> {
                        StringBuilder mentions = new StringBuilder();
                        Utilities.removeEvent(bot.getConfig().getKey(), e);
                        if (tickets != null) {
                            tickets.forEach((tk, ticket) -> Utilities.removeAttendee(bot.getConfig().getKey(), ticket));
                        }
                        if (tickets != null)
                            tickets.forEach((id, ticket) -> mentions.append(ticket.getMention()).append(" "));
                        channel.editMessageById(e.getMessageId(), "~~" + message.getContentRaw() +"~~").queue();
                        channel.sendMessage("Event Starting: " + e.getName() + "\n" + mentions.toString()).queue();
                    };
                    fetchMessage.queue(callback);
                });
        };
        scheduler.scheduleAtFixedRate(check, 0, 1, TimeUnit.MINUTES);
    }
}
