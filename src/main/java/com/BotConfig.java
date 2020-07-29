package com;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import com.utils.Utilities;

import java.nio.file.Path;

public class BotConfig {
    private Path path = null;
    private String token, key, prefix, game, help;
    private long owner;
    private boolean valid;

    public void load() {
        try {
            valid = false;
            //Get the location of the config file.
            //Config file is actually called reference.conf.
            path = Utilities.getPath(System.getProperty("config.file", System.getProperty("config", "config.txt")));
            if(path.toFile().exists())
            {
                //Make sure the file is not empty.
                if(System.getProperty("config.file") == null)
                    System.setProperty("config.file", System.getProperty("config", "config.txt"));
                ConfigFactory.invalidateCaches();
            }

            Config conf = ConfigFactory.load();

            //Load values
            token = conf.getString("token");
            owner = Long.parseLong(conf.getString("owner"));
            prefix = conf.getString("prefix");
            game = conf.getString("game");
            help = conf.getString("help");

            //Check for valid bot token.
            if(token == null || token.isEmpty()) {
                System.out.println("Please provide a valid bot token in " + path.toAbsolutePath().toString() + ".");
                return;
            }

            // Validate owner ID.
            if(owner <= 0) {
                System.out.println("Please provide a valid owner ID in " + path.toAbsolutePath().toString() + ".");
                return;
            }

            valid = true;
        }
        catch(ConfigException e) {
            System.out.println("Config Exception");
        }
    }


    public String getToken() {
        return token;
    }

    public String getKey() {
        return key;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getGame() {
        return game;
    }

    public String getHelp() {
        return help;
    }

    public long getOwner() {
        return owner;
    }

    public String getConfigLocation() {
        return path.toFile().getAbsolutePath();
    }

    public boolean isValid() {
        return valid;
    }
}
