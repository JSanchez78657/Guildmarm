package com.commands.scheduling;

import com.utils.Utilities;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAmount;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ScheduleTimer extends ListenerAdapter {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public void onReady(ReadyEvent event) {
        System.out.println("Starting Timer");
        //"check" contains what is run every minute by the scheduler.
        //In this case, it is checking to see if any events are scheduled at the given time.
        final Runnable check = () -> {
            ZonedDateTime now = ZonedDateTime.now();
            System.out.println("Checking events... Time: " + now.toString());
            HashMap<String, ScheduledEvent> events = Utilities.getStartingEvents(now);
            StringBuilder hold = new StringBuilder();
            events.forEach((k,e) -> hold.append(e.toString()).append("\n"));
            System.out.println(hold.toString());
        };
        scheduler.scheduleAtFixedRate(check, 0, 1, TimeUnit.MINUTES);
    }
}
