package com;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.settings.SettingsManager;
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
        BotConfig config = new BotConfig();
        config.load();
        if(!config.isValid()) {
            System.out.println("Invalid configuration");
            return;
        }

        EventWaiter waiter = new EventWaiter();
        SettingsManager settings = new SettingsManager();
        Bot bot = new Bot(waiter, config, settings);
        CommandClientBuilder builder = new CommandClientBuilder()
                .setPrefix(config.getPrefix())
                .setOwnerId(Long.toString(config.getOwner()))
                .setHelpWord(config.getHelp())
                .setActivity(Activity.watching(config.getGame()))
        ;

        JDABuilder.createDefault(config.getToken())
                .setStatus(OnlineStatus.ONLINE)
                .addEventListeners(waiter, builder.build())
                .build();
    }
}
