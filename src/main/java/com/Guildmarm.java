package com;

import com.commands.scheduling.ScheduleCheck;
import com.commands.scheduling.ScheduleCommand;
import com.commands.scheduling.ScheduleTimer;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.settings.SettingsManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class Guildmarm {

    public static void main(String[] args) throws IOException, LoginException {
        //This might be necessary when exported to a JAR, the paths might switch up.
        //System.out.println(System.getProperty("user.dir"));
        //Load based on a configuration file
        JDA jda = null;
        EventWaiter waiter = new EventWaiter();
        SettingsManager settings = new SettingsManager();
        ScheduleTimer timer = new ScheduleTimer(jda);
        BotConfig config = new BotConfig();
        config.load();
        if(!config.isValid()) {
            System.out.println("Invalid configuration");
            return;
        }
        Bot bot = new Bot(waiter, config, settings);
        CommandClientBuilder builder =
            new CommandClientBuilder()
            .setPrefix(config.getPrefix())
            .setOwnerId(Long.toString(config.getOwner()))
            .setHelpWord(config.getHelp())
            .setActivity(Activity.watching(config.getGame()))
            .setGuildSettingsManager(settings)
            .addCommands(
                new ScheduleCommand(bot),
                new ScheduleCheck(bot)
            );

        try {
            jda = JDABuilder.createDefault(config.getToken())
                    .setStatus(OnlineStatus.ONLINE)
                    .addEventListeners(waiter, builder.build(), timer)
                    .build();
            bot.setJda(jda);
        }
        catch (LoginException ex)
        {
            System.out.println("LoginException");
            System.exit(1);
        }
        catch (IllegalArgumentException ex)
        {
            System.out.println("IllegalArgs");
            System.exit(1);
        }
    }
}
