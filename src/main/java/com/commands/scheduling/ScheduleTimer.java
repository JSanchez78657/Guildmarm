package com.commands.scheduling;

import com.utils.Utilities;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAmount;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ScheduleTimer extends ListenerAdapter {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private JDA jda;

    public ScheduleTimer(JDA jda) {
        super();
        this.jda = jda;
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        System.out.println("Starting Timer");
        //"check" contains what is run every minute by the scheduler.
        //In this case, it is checking to see if any events are scheduled at the given time.
        final Runnable check = () -> {
            ZonedDateTime now = ZonedDateTime.now();
            HashMap<String, ScheduledEvent> events = Utilities.getStartingEvents(now);
            if (events != null)
                events.forEach((k,e) -> {
                    Objects.requireNonNull(event.getJDA().getTextChannelById(e.getChannelId()))
                            .sendMessage("Event Starting: " + e.getName())
                            .queue();
                });
        };
        scheduler.scheduleAtFixedRate(check, 0, 1, TimeUnit.MINUTES);
    }
}
