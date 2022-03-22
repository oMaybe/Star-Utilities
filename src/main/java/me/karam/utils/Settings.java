package me.karam.utils;

import me.karam.Main;
import me.karam.config.Config;

import javax.annotation.Nonnull;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

public class Settings {

    private static Config config;

    public static String GUILD_ID = "954271232067530782"; // TODO: this is test server change to main server later!
    public static String TOKEN = "";
    public static String SUPPORT_TEXT_CHANNEL = "955372374335827968";

    public static boolean loadFromConfig(){
        if (TokenManager.load()) return false;

        if (TOKEN == null || TOKEN.length() == 0){
            return false;
        }

        Config config = new Config(new File(System.getProperty("user.dir") + File.separator + "config" + File.separator + "mainConfig.json"));
        Settings.config = config;

        if (config.getString("GUILD_ID") != null){
            //GUILD_ID = config.getString("GUILD_ID");
        }

        if (config.getString("SUPPORT_TEXT_CHANNEL") != null){

        }

        return true;
    }

    public static boolean saveToConfig(){
        config.save();
        return true;
    }
}
