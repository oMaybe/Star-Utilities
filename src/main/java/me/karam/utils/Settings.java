package me.karam.utils;

import me.karam.Main;
import me.karam.config.Config;
import org.json.simple.JSONObject;

import javax.annotation.Nonnull;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

public class Settings {
    public static boolean DEBUG = true;
    public static Config config;
    public static String GUILD_ID;
    public static String TOKEN;
    public static String TICKET_LOG_CHANNEL;
    public static String TICKET_CATEGORY;
    public static String TICKET_PING_ROLE;

    public static boolean loadFromConfig(){
        if (!TokenManager.load()) return false;

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

        if (config.getObject("botData").get("ticket_category_id") != null){
            TICKET_LOG_CHANNEL = String.valueOf(config.getObject("botData").get("ticket_category_id"));
        }

        // TODO: add ticket saver/loader
        return true;
    }

    public static boolean saveToConfig(){
        JSONObject o = new JSONObject();
        o.put("guild_id", GUILD_ID);
        o.put("ticket_channel", TICKET_LOG_CHANNEL);
        o.put("ticket_category_id", TICKET_CATEGORY);
        config.insert("botData", o);
        config.save();
        return false;
    }
}
