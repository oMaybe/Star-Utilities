package me.karam.utils.info;

import me.karam.utils.Settings;

public class BotLogger {

    public static void log(Severity level, Object message){
        System.out.println(level.getColorNames() + "[" + level.name() + "] " + Color.ANSI_RESET + message);
    }

    public static void debug(Object message){
        Severity level = Severity.DEBUG;
        System.out.println(level.getColorNames() + "[" + level.name() + "] " + Color.ANSI_RESET + message);
    }

    public static void info(Object message){
        Severity level = Severity.INFO;
        System.out.println(level.getColorNames() + "[" + level.name() + "] " + Color.ANSI_RESET + message);
    }

    public static void logg(Object message){
        if (Settings.DEBUG) {
            Severity level = Severity.DEBUG;
            System.out.println(level.getColorNames() + "[" + level.name() + "] " + Color.ANSI_RESET + message);
        }else{
            Severity level = Severity.INFO;
            System.out.println(level.getColorNames() + "[" + level.name() + "] " + Color.ANSI_RESET + message);
        }
    }
}
