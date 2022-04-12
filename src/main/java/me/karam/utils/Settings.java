package me.karam.utils;

import me.karam.Main;
import me.karam.config.Config;

import javax.annotation.Nonnull;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

public class Settings {

    public static Config config;

    public static String GUILD_ID = "954271232067530782"; // TODO: this is test server change to main server later!
    public static String TOKEN;
    public static String TICKET_LOG_CHANNEL;

    public static boolean loadFromConfig(){
        if (TokenManager.load()) return false;

        if (TOKEN == null || TOKEN.length() == 0){
            return false;
        }

        Config config = new Config(new File(System.getProperty("user.dir") + File.separator + "config" + File.separator + "mainConfig.json"));
        Settings.config = config;

        if (config.getObject("botData").get("guild_id") != null){
            GUILD_ID = String.valueOf(config.getObject("botData").get("guild_id"));
        }

        if (config.getObject("botData").get("ticket_channel") != null){
            TICKET_LOG_CHANNEL = String.valueOf(config.getObject("botData").get("ticket_channel"));
        }

        // TODO: add ticket saver/loader

        return true;
    }

    public static boolean saveToConfig(){
        config.save();
        return true;
    }
}
