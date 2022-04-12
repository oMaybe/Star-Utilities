package me.karam.utils;

public class BotLogger {

    public static void log(me.karam.utils.Severity level, Object message){
        System.out.println(level.getColorNames() + "[" + level.name() + "] " + me.karam.utils.Color.ANSI_RESET + message);
    }

    public static void log(Object message){
        Severity level = Severity.INFO;
        System.out.println(level.getColorNames() + "[" + level.name() + "] " + me.karam.utils.Color.ANSI_RESET + message);
    }
}
