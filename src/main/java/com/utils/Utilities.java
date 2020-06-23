package com.utils;

import com.Bot;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Utilities {

    private final static String WINDOWS_INVALID_PATH = "c:\\windows\\system32\\";

    public static Path getPath(String path) {
        // special logic to prevent trying to access system32
        if(path.toLowerCase().startsWith(WINDOWS_INVALID_PATH))
        {
            String filename = path.substring(WINDOWS_INVALID_PATH.length());
            try
            {
                path = new File(Bot.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getPath() + File.separator + filename;
            }
            catch(URISyntaxException ex) {
                System.out.println("URISyntax Error!");
            }
        }
        return Paths.get(path);
    }
}
