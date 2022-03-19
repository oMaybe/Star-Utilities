package me.karam.utils;

import me.karam.Main;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public class Settings {

    public static String TOKEN = "";

    public static boolean loadFromConfig(){
        if (TokenManager.load()) return false;

        if (TOKEN == null || TOKEN.length() == 0){
            return false;
        }

        return true;
    }
}
