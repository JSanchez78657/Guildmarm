package com;

import com.commands.admin.ShutdownCommand;
import com.commands.scheduling.ScheduleCommand;
import com.listeners.EmoteReactionListener;
import com.listeners.EventTimerListener;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.settings.SettingsManager;
import com.utils.Utilities;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;
import java.util.logging.Level;

public class Guildmarm {

    public static void main(String[] args) {
        //This might be necessary when exported to a JAR, the paths might switch up.
        //System.out.println(System.getProperty("user.dir"));
        //Load based on a configuration file
        EventWaiter waiter = new EventWaiter();
        SettingsManager settings = new SettingsManager();
        BotConfig config = new BotConfig();
        config.load();
        if(!config.isValid()) {
            Utilities.log(Level.CONFIG, "No valid configuration file found.");
            return;
        }
        Bot bot = new Bot(waiter, config, settings);
        EventTimerListener timer = new EventTimerListener(bot);
        EmoteReactionListener reactions = new EmoteReactionListener(bot);
        CommandClientBuilder builder =
            new CommandClientBuilder()
            .setPrefix(config.getPrefix())
            .setOwnerId(Long.toString(config.getOwner()))
            .setHelpWord(config.getHelp())
            .setActivity(Activity.watching(config.getGame()))
            .setGuildSettingsManager(settings)
            .addCommands(
                new ScheduleCommand(bot),
                    new ShutdownCommand(bot)
            );

        try {
            JDA jda = JDABuilder.createDefault(config.getToken())
                    .setStatus(OnlineStatus.ONLINE)
                    .addEventListeners(
                            waiter,
                            builder.build(),
                            timer,
                            reactions)
                    .build();
            bot.setJda(jda);
        }
        catch (LoginException ex)
        {
            Utilities.log(Level.SEVERE, "Unable to login, login exception");
            System.exit(1);
        }
    }
}
