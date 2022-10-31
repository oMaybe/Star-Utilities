package me.karam.utils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Cooldown {

    private static HashMap<String, Long> cooldown = new HashMap<>();

    public static boolean hasCooldown(String ID){
        if (cooldown.containsKey(ID) && (cooldown.get(ID) - System.currentTimeMillis() > 0)){
            return true;
        }
        return false;
    }

    public static void put(String ID, Date date){
        cooldown.put(ID, date.getTime());
    }
}
